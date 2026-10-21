package com.hikari.rent.rental.search.repository.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.hikari.rent.common.result.PageResult;
import com.hikari.rent.rental.search.document.RentalSearchDocument;
import com.hikari.rent.rental.search.model.RentalSearchCriteria;
import com.hikari.rent.rental.search.repository.RentalSearchRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class RentalSearchRepositoryImpl
  implements RentalSearchRepository {

  private static final String INDEX =
    "hikari_rentals";

  private static final long MAX_RESULT_WINDOW =
    10_000L;

  @Resource
  private ElasticsearchClient rentalElasticsearchClient;

  @Override
  public PageResult<Long> searchPage(
    RentalSearchCriteria criteria
  ) {
    long offset =
      (long) (criteria.pageNum() - 1)
        * criteria.pageSize();

    if (
      offset + criteria.pageSize()
        > MAX_RESULT_WINDOW
    ) {
      throw new IllegalArgumentException(
        "Requested page exceeds the search result window"
      );
    }

    BoolQuery.Builder bool = new BoolQuery.Builder();

    addKeywordQuery(bool, criteria);
    addAreaFilter(bool, criteria);
    addRentFilter(bool, criteria);

    SearchRequest.Builder search =
      new SearchRequest.Builder()
        .index(INDEX)
        .from(Math.toIntExact(offset))
        .size(criteria.pageSize())
        .trackTotalHits(track -> track.enabled(true))
        .allowPartialSearchResults(false)
        .source(source -> source.fetch(false))
        .query(query -> query.bool(bool.build()));

    addSort(search, criteria);

    try {
      SearchResponse<Void> response =
        rentalElasticsearchClient.search(
          search.build(),
          Void.class
        );

      if (response.timedOut()) {
        throw new IllegalStateException(
          "Rental search timed out"
        );
      }

      if (response.hits().total() == null) {
        throw new IllegalStateException(
          "Rental search did not return total hits"
        );
      }

      List<Long> rentalIds =
        response.hits()
          .hits()
          .stream()
          .map(hit -> {
            assert hit.id() != null;
            return Long.valueOf(hit.id());
          })
          .toList();

      return new PageResult<>(
        criteria.pageNum(),
        criteria.pageSize(),
        response.hits().total().value(),
        rentalIds
      );

    } catch (IOException ex) {
      throw new IllegalStateException(
        "Failed to search rental index",
        ex
      );
    }
  }

  private void addKeywordQuery(
    BoolQuery.Builder bool,
    RentalSearchCriteria criteria
  ) {
    if (criteria.keyword() == null) {
      return;
    }

    bool.must(query -> query.multiMatch(match ->
      match
        .query(criteria.keyword())
        .fields(
          "name^3",
          "address^2",
          "description"
        )
        .operator(Operator.And)
    ));
  }

  private void addAreaFilter(
    BoolQuery.Builder bool,
    RentalSearchCriteria criteria
  ) {
    if (criteria.areaCode() == null) {
      return;
    }

    bool.filter(query -> query.term(term ->
      term
        .field("areaPathCodes")
        .value(criteria.areaCode())
    ));
  }

  private void addRentFilter(
    BoolQuery.Builder bool,
    RentalSearchCriteria criteria
  ) {
    if (
      criteria.minRent() == null
        && criteria.maxRent() == null
    ) {
      return;
    }

    bool.filter(query -> query.range(range ->
      range.number(number -> {
        number.field("monthlyRent");

        if (criteria.minRent() != null) {
          number.gte(
            criteria.minRent().doubleValue()
          );
        }

        if (criteria.maxRent() != null) {
          number.lte(
            criteria.maxRent().doubleValue()
          );
        }

        return number;
      })
    ));
  }

  private void addSort(
    SearchRequest.Builder search,
    RentalSearchCriteria criteria
  ) {
    switch (criteria.sort()) {
      case "rentAsc" ->
        search.sort(sort -> sort.field(field ->
          field
            .field("monthlyRent")
            .order(SortOrder.Asc)
        ));

      case "rentDesc" ->
        search.sort(sort -> sort.field(field ->
          field
            .field("monthlyRent")
            .order(SortOrder.Desc)
        ));

      case "areaDesc" ->
        search.sort(sort -> sort.field(field ->
          field
            .field("area")
            .order(SortOrder.Desc)
        ));

      default -> {
        if (criteria.keyword() != null) {
          search.sort(sort -> sort.score(score ->
            score.order(SortOrder.Desc)
          ));
        }

        search.sort(sort -> sort.field(field ->
          field
            .field("publishedAt")
            .order(SortOrder.Desc)
        ));
      }
    }

    // Use a unique field to make equal-value ordering deterministic.
    search.sort(sort -> sort.field(field ->
      field
        .field("rentalId")
        .order(SortOrder.Desc)
    ));
  }

  @Override
  public void saveBatch(
    List<RentalSearchDocument> documents
  ) {
    Objects.requireNonNull(
      documents,
      "documents must not be null"
    );

    if (documents.isEmpty()) {
      return;
    }

    if (documents.size() > 500) {
      throw new IllegalArgumentException(
        "A batch must not contain more than 500 documents"
      );
    }

    BulkRequest.Builder bulk =
      new BulkRequest.Builder()
        .requireAlias(true);

    for (RentalSearchDocument document : documents) {
      validateDocument(document);

      bulk.operations(operation ->
        operation.index(index ->
          index
            .index(INDEX)
            .id(document.getId())
            .document(document)
        )
      );
    }

    try {
      BulkResponse response =
        rentalElasticsearchClient.bulk(
          bulk.build()
        );

      if (response.errors()) {
        String failures = response.items()
          .stream()
          .filter(item -> item.error() != null)
          .limit(5)
          .map(item ->
            "id=" + item.id()
              + ", reason=" + item.error().reason()
          )
          .collect(Collectors.joining("; "));

        throw new IllegalStateException(
          "Some rental documents failed to index: "
            + failures
        );
      }

    } catch (IOException ex) {
      throw new IllegalStateException(
        "Failed to bulk index rental documents",
        ex
      );
    }
  }

  @Override
  public void refresh() {
    try {
      var response =
        rentalElasticsearchClient.indices()
          .refresh(request ->
            request.index(INDEX)
          );

      assert response.shards() != null;
      if (response.shards().failed().intValue() > 0) {
        throw new IllegalStateException(
          "Some shards failed to refresh the rental index"
        );
      }

    } catch (IOException ex) {
      throw new IllegalStateException(
        "Failed to refresh the rental index",
        ex
      );
    }
  }

  private void validateDocument(
    RentalSearchDocument document
  ) {
    Objects.requireNonNull(
      document,
      "document must not be null"
    );

    Long rentalId = document.getRentalId();

    if (rentalId == null || rentalId <= 0) {
      throw new IllegalArgumentException(
        "rentalId must be greater than zero"
      );
    }

    if (!rentalId.toString().equals(document.getId())) {
      throw new IllegalArgumentException(
        "Document id must match rentalId"
      );
    }
  }
}

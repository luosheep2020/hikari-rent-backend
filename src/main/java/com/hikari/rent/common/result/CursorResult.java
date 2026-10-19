package com.hikari.rent.common.result;

import java.util.List;

public record CursorResult<T>(
  List<T> items,
  String nextCursor,
  boolean hasMore
) {
}

package com.hikari.rent.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

  // 通用错误
  BAD_REQUEST(
    400, "Invalid request parameters", HttpStatus.BAD_REQUEST
  ),
  NOT_FOUND(
    404, "Resource not found", HttpStatus.NOT_FOUND
  ),
  CONFLICT(
    409, "Data conflict", HttpStatus.CONFLICT
  ),
  INTERNAL_ERROR(
    500, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR
  ),

  // 品牌
  BRAND_NOT_FOUND(
    10001, "Brand not found", HttpStatus.NOT_FOUND
  ),
  BRAND_NAME_ALREADY_EXISTS(
    10002, "Brand name already exists", HttpStatus.CONFLICT
  ),
  BRAND_HAS_PRODUCTS(
    10003, "Brand has associated products", HttpStatus.CONFLICT
  ),

  // 分类
  CATEGORY_NOT_FOUND(
    11001, "Category not found", HttpStatus.NOT_FOUND
  ),
  CATEGORY_PARENT_NOT_FOUND(
    11002, "Parent category not found", HttpStatus.BAD_REQUEST
  ),
  CATEGORY_NAME_ALREADY_EXISTS(
    11003, "Category name already exists under this parent",
    HttpStatus.CONFLICT
  ),
  CATEGORY_PARENT_IS_SELF(
    11004, "Category cannot be its own parent",
    HttpStatus.BAD_REQUEST
  ),
  CATEGORY_PARENT_IS_DESCENDANT(
    11005, "Category parent cannot be its descendant",
    HttpStatus.BAD_REQUEST
  ),
  CATEGORY_HAS_CHILDREN(
    11006, "Category has child categories", HttpStatus.CONFLICT
  ),
  CATEGORY_HAS_PRODUCTS(
    11007, "Category has associated products", HttpStatus.CONFLICT
  ),
  CATEGORY_HIERARCHY_INVALID(
    11008, "Category hierarchy is invalid",
    HttpStatus.INTERNAL_SERVER_ERROR
  ),
  // SPU
  SPU_NOT_FOUND(
    12001, "Product not found", HttpStatus.NOT_FOUND
  ),
  SPU_HAS_SKUS(
    12002, "Product has associated SKUs", HttpStatus.CONFLICT
  ),
  SPU_MUST_BE_OFF_SHELF(
    12003, "Product must be taken off shelf before deletion",
    HttpStatus.CONFLICT
  ),
  SPU_HAS_NO_SKUS(
    12004, "Product must have at least one SKU before being put on shelf",
    HttpStatus.CONFLICT
  ),

  // SKU
  SKU_NOT_FOUND(
    13001, "SKU not found", HttpStatus.NOT_FOUND
  ),
  SKU_SN_ALREADY_EXISTS(
    13002, "SKU code already exists", HttpStatus.CONFLICT
  ),
  SKU_SPEC_ALREADY_EXISTS(
    13003, "SKU specification combination already exists for this product",
    HttpStatus.CONFLICT
  ),
  SKU_STOCK_INSUFFICIENT(
    13004, "Insufficient SKU stock", HttpStatus.CONFLICT
  ),
  SKU_HAS_LOCKED_STOCK(
    13005, "SKU has locked stock", HttpStatus.CONFLICT
  );

  private final int code;
  private final String message;
  private final HttpStatus status;
}

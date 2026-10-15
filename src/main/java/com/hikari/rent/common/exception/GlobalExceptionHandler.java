package com.hikari.rent.common.exception;


import com.hikari.rent.common.enums.ErrorCode;
import com.hikari.rent.common.result.ApiResponse;
import com.hikari.rent.rental.exception.RentalNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  // Business Exception
  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ApiResponse<Void>> handleBusinessException(
    BusinessException ex) {

    ErrorCode errorCode = ex.getErrorCode();

    return ResponseEntity.status(errorCode.getStatus())
      .body(ApiResponse.failure(
        errorCode.getCode(),
        ex.getMessage()
      ));
  }

  //  SpringMVC Exception
  @Override
  protected ResponseEntity<Object> handleExceptionInternal(
    Exception ex,
    Object body,
    HttpHeaders headers,
    HttpStatusCode status,
    WebRequest request
  ) {
    if (body instanceof ApiResponse<?>) {
      return super.handleExceptionInternal(
        ex, body, headers, status, request
      );
    }
    String message;
    if (status.is5xxServerError()) {
      log.error("Request processing failed", ex);
      message = "Internal Server Error";
    } else {
      message = switch (status.value()) {
        case 400 -> "Bad Request";
        case 404 -> "Not Found";
        case 405 -> "Method Not Allowed";
        case 415 -> "Unsupported Media Type";
        default -> "Request processing failed";
      };
    }
    return super.handleExceptionInternal(
      ex,
      ApiResponse.failure(status.value(),message),
      headers,
      status,
      request);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
    MethodArgumentNotValidException ex,
    HttpHeaders headers,
    HttpStatusCode status,
    WebRequest request) {

    String message = ex.getBindingResult()
      .getAllErrors()
      .stream()
      .map(DefaultMessageSourceResolvable::getDefaultMessage)
      .filter(java.util.Objects::nonNull)
      .distinct()
      .sorted()
      .collect(java.util.stream.Collectors.joining("; "));

    if (message.isBlank()) {
      message = "Invalid request parameters";
    }

    return handleExceptionInternal(
      ex,
      ApiResponse.failure(400, message),
      headers,
      status,
      request
    );
  }
//  UnexpectedException
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleUnexpectedException(Exception ex){
      log.error("Unhandled exception");

      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponse.failure(500,"Internal Server Error"));
  }
  @ExceptionHandler(RentalNotFoundException.class)
  public ResponseEntity<ApiResponse<Void>> handleRentalNotFound(
    RentalNotFoundException exception
  ) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
      .body(ApiResponse.failure(
        404,
        exception.getMessage()
      ));
  }
}

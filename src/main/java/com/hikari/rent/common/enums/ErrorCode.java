package com.hikari.rent.common.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

  // Common request errors.
  BAD_REQUEST(
    10000, "Bad request", HttpStatus.BAD_REQUEST
  ),
  VALIDATION_FAILED(
    10001, "Invalid request parameters", HttpStatus.BAD_REQUEST
  ),
  INVALID_REQUEST_BODY(
    10002, "Invalid request body", HttpStatus.BAD_REQUEST
  ),
  RESOURCE_NOT_FOUND(
    10003, "Resource not found", HttpStatus.NOT_FOUND
  ),
  METHOD_NOT_ALLOWED(
    10004, "Method not allowed", HttpStatus.METHOD_NOT_ALLOWED
  ),
  UNSUPPORTED_MEDIA_TYPE(
    10005, "Unsupported media type", HttpStatus.UNSUPPORTED_MEDIA_TYPE
  ),
  TOO_MANY_REQUESTS(
    10006, "Too many requests", HttpStatus.TOO_MANY_REQUESTS
  ),
  DATA_CONFLICT(
    10007, "Data conflict", HttpStatus.CONFLICT
  ),
  INTERNAL_SERVER_ERROR(
    10099, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR
  ),

  // Authentication and authorization.
  AUTHENTICATION_REQUIRED(
    20000, "Authentication required", HttpStatus.UNAUTHORIZED
  ),
  INVALID_CREDENTIALS(
    20001, "Invalid email or password", HttpStatus.UNAUTHORIZED
  ),
  SESSION_EXPIRED(
    20002, "Session expired", HttpStatus.UNAUTHORIZED
  ),
  INVALID_TOKEN(
    20003, "Invalid authentication token", HttpStatus.UNAUTHORIZED
  ),
  ACCESS_DENIED(
    20004, "Access denied", HttpStatus.FORBIDDEN
  ),
  ACCOUNT_DISABLED(
    20005, "Account disabled", HttpStatus.FORBIDDEN
  ),
  EMAIL_ALREADY_REGISTERED(
    20006, "Email already registered", HttpStatus.CONFLICT
  ),
  EMAIL_NOT_VERIFIED(
    20007, "Email verification required", HttpStatus.FORBIDDEN
  ),
  VERIFICATION_CODE_INVALID(
    20008, "Invalid verification code", HttpStatus.BAD_REQUEST
  ),
  VERIFICATION_CODE_EXPIRED(
    20009, "Verification code expired", HttpStatus.BAD_REQUEST
  ),
  INVALID_CSRF_TOKEN(
    20010, "Invalid CSRF token", HttpStatus.FORBIDDEN
  ),
  PASSWORD_RESET_TOKEN_INVALID(
    20011, "Invalid or expired password reset token",
    HttpStatus.BAD_REQUEST
  ),

  // Account and profile.
  USER_NOT_FOUND(
    30000, "User not found", HttpStatus.NOT_FOUND
  ),
  CURRENT_PASSWORD_INCORRECT(
    30001, "Current password is incorrect", HttpStatus.BAD_REQUEST
  ),
  PASSWORD_UNCHANGED(
    30002, "New password must differ from the current password",
    HttpStatus.BAD_REQUEST
  ),
  PROFILE_UPDATE_CONFLICT(
    30003, "Profile changed; refresh and try again",
    HttpStatus.CONFLICT
  ),
  AVATAR_FILE_INVALID(
    30004, "Invalid avatar file", HttpStatus.BAD_REQUEST
  ),

  // Rentals.
  RENTAL_NOT_FOUND(
    40000, "Rental not found", HttpStatus.NOT_FOUND
  ),
  RENTAL_NOT_AVAILABLE(
    40001, "Rental is no longer available", HttpStatus.CONFLICT
  ),
  INVALID_RENT_RANGE(
    40002, "Minimum rent must not exceed maximum rent",
    HttpStatus.BAD_REQUEST
  ),
  INVALID_MAP_BOUNDS(
    40003, "Invalid map bounds", HttpStatus.BAD_REQUEST
  ),
  MAP_AREA_TOO_LARGE(
    40004, "Map search area is too large; zoom in",
    HttpStatus.BAD_REQUEST
  ),
  INVALID_PROPERTY_TYPE(
    40005, "Invalid property type", HttpStatus.BAD_REQUEST
  ),
  RENTAL_FEATURE_NOT_FOUND(
    40006, "Rental feature not found", HttpStatus.BAD_REQUEST
  ),
  RENTAL_UPDATE_CONFLICT(
    40007, "Rental changed; refresh and try again",
    HttpStatus.CONFLICT
  ),
  RENTAL_STATUS_TRANSITION_NOT_ALLOWED(
    40008, "Rental status transition is not allowed",
    HttpStatus.CONFLICT
  ),
  RENTAL_REVIEW_TRANSITION_NOT_ALLOWED(
    40009, "Rental review transition is not allowed",
    HttpStatus.CONFLICT
  ),
  RENTAL_PUBLISH_REQUIREMENTS_NOT_MET(
    40010, "Rental does not meet publication requirements",
    HttpStatus.BAD_REQUEST
  ),
  RENTAL_AGENT_INVALID(
    40011, "Agent must be active and belong to the rental company",
    HttpStatus.BAD_REQUEST
  ),
  RENTAL_IMAGE_LIMIT_EXCEEDED(
    40012, "Rental image limit exceeded", HttpStatus.BAD_REQUEST
  ),
  RENTAL_DELETE_NOT_ALLOWED(
    40013, "Rental cannot be deleted in its current state",
    HttpStatus.CONFLICT
  ),

  // Favorites, browsing history, and viewing appointments.
  FAVORITE_LIMIT_EXCEEDED(
    50000, "Favorite limit exceeded", HttpStatus.CONFLICT
  ),
  APPOINTMENT_NOT_FOUND(
    50001, "Viewing appointment not found", HttpStatus.NOT_FOUND
  ),
  APPOINTMENT_TIME_UNAVAILABLE(
    50002, "Viewing appointment time is unavailable",
    HttpStatus.CONFLICT
  ),
  APPOINTMENT_STATUS_TRANSITION_NOT_ALLOWED(
    50003, "Viewing appointment status transition is not allowed",
    HttpStatus.CONFLICT
  ),

  // Conversations and messages.
  CONVERSATION_NOT_FOUND(
    60000, "Conversation not found", HttpStatus.NOT_FOUND
  ),
  CONVERSATION_CLOSED(
    60001, "Conversation is closed", HttpStatus.CONFLICT
  ),
  MESSAGE_NOT_FOUND(
    60002, "Message not found", HttpStatus.NOT_FOUND
  ),
  MESSAGE_EMPTY(
    60003, "Message must contain text or an attachment",
    HttpStatus.BAD_REQUEST
  ),
  MESSAGE_TOO_LONG(
    60004, "Message exceeds the maximum length",
    HttpStatus.BAD_REQUEST
  ),
  MESSAGE_ATTACHMENT_LIMIT_EXCEEDED(
    60005, "Message attachment limit exceeded",
    HttpStatus.BAD_REQUEST
  ),
  MESSAGE_RECIPIENT_UNAVAILABLE(
    60006, "Message recipient is unavailable", HttpStatus.CONFLICT
  ),
  MESSAGE_RECALL_NOT_ALLOWED(
    60007, "Message cannot be recalled", HttpStatus.CONFLICT
  ),

  // Files and object storage.
  FILE_NOT_FOUND(
    70000, "File not found", HttpStatus.NOT_FOUND
  ),
  FILE_TOO_LARGE(
    70001, "File exceeds the maximum size",
    HttpStatus.PAYLOAD_TOO_LARGE
  ),
  FILE_TYPE_NOT_ALLOWED(
    70002, "File type is not allowed", HttpStatus.BAD_REQUEST
  ),
  FILE_CONTENT_INVALID(
    70003, "File content is invalid", HttpStatus.BAD_REQUEST
  ),
  FILE_NOT_READY(
    70004, "File is not ready", HttpStatus.CONFLICT
  ),
  FILE_PURPOSE_MISMATCH(
    70005, "File purpose does not match the requested operation",
    HttpStatus.BAD_REQUEST
  ),
  FILE_IN_USE(
    70006, "File is currently in use", HttpStatus.CONFLICT
  ),
  FILE_UPLOAD_EXPIRED(
    70007, "Upload authorization expired", HttpStatus.BAD_REQUEST
  ),
  FILE_UPLOAD_VERIFICATION_FAILED(
    70008, "Uploaded file verification failed",
    HttpStatus.BAD_REQUEST
  ),
  STORAGE_UNAVAILABLE(
    70009, "Storage service temporarily unavailable",
    HttpStatus.SERVICE_UNAVAILABLE
  ),

  // Areas, railway lines, and stations.
  AREA_NOT_FOUND(
    80000, "Area not found", HttpStatus.NOT_FOUND
  ),
  INVALID_AREA_LEVEL(
    80001, "Invalid area level", HttpStatus.BAD_REQUEST
  ),
  STATION_NOT_FOUND(
    80002, "Station not found", HttpStatus.NOT_FOUND
  ),
  RAILWAY_LINE_NOT_FOUND(
    80003, "Railway line not found", HttpStatus.NOT_FOUND
  ),
  RAILWAY_OPERATOR_NOT_FOUND(
    80004, "Railway operator not found", HttpStatus.NOT_FOUND
  ),
  STATION_LINE_MISMATCH(
    80005, "Station does not belong to the selected railway line",
    HttpStatus.BAD_REQUEST
  ),

  // Companies, organization, and RBAC.
  COMPANY_NOT_FOUND(
    90000, "Company not found", HttpStatus.NOT_FOUND
  ),
  COMPANY_DISABLED(
    90001, "Company disabled", HttpStatus.FORBIDDEN
  ),
  DEPARTMENT_NOT_FOUND(
    90002, "Department not found", HttpStatus.NOT_FOUND
  ),
  POSITION_NOT_FOUND(
    90003, "Position not found", HttpStatus.NOT_FOUND
  ),
  ORGANIZATION_COMPANY_MISMATCH(
    90004, "Department or position does not belong to the company",
    HttpStatus.BAD_REQUEST
  ),
  ROLE_NOT_FOUND(
    90005, "Role not found", HttpStatus.NOT_FOUND
  ),
  ROLE_CODE_ALREADY_EXISTS(
    90006, "Role code already exists", HttpStatus.CONFLICT
  ),
  PERMISSION_NOT_FOUND(
    90007, "Permission not found", HttpStatus.NOT_FOUND
  ),
  PERMISSION_CODE_ALREADY_EXISTS(
    90008, "Permission code already exists", HttpStatus.CONFLICT
  ),
  PROTECTED_ROLE_OPERATION_NOT_ALLOWED(
    90009, "Operation on a protected role is not allowed",
    HttpStatus.FORBIDDEN
  ),
  ROLE_IN_USE(
    90010, "Role is currently assigned to users", HttpStatus.CONFLICT
  ),
  ORGANIZATION_UNIT_IN_USE(
    90011, "Organization unit is currently in use",
    HttpStatus.CONFLICT
  ),
  LAST_ADMIN_OPERATION_NOT_ALLOWED(
    90012, "The last active platform administrator must be retained",
    HttpStatus.CONFLICT
  );

  private final int code;
  private final String message;
  private final HttpStatus status;

  ErrorCode(int code, String message, HttpStatus status) {
    this.code = code;
    this.message = message;
    this.status = status;
  }
}

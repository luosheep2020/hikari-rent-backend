package com.hikari.rent.rental.model;

public record RentalAccessScope(
  Type type,
  Long userId,
  Long companyId
) {

  public enum Type {
    PLATFORM,
    COMPANY,
    SELF
  }

  public boolean isPlatform() {
    return type == Type.PLATFORM;
  }

  public boolean isCompany() {
    return type == Type.COMPANY;
  }

  public boolean isSelf() {
    return type == Type.SELF;
  }
}

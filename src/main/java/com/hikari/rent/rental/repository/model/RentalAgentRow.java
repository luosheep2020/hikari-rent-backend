package com.hikari.rent.rental.repository.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RentalAgentRow {

  private Long id;

  private String name;

  private String avatarStorageKey;

  private String company;

  private String businessHours;

  private String closedDays;
}

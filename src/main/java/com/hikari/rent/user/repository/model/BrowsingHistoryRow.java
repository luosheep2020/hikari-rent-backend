package com.hikari.rent.user.repository.model;


import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BrowsingHistoryRow {

  private Long rentalId;

  private LocalDateTime lastViewedAt;
}

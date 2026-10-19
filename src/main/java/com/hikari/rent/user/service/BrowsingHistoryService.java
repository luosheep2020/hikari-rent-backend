package com.hikari.rent.user.service;

import java.util.List;

import com.hikari.rent.rental.vo.RentalListVO;

public interface BrowsingHistoryService {

  List<RentalListVO> getHistory();

  void recordView(Long rentalId);

  void remove(Long rentalId);

  void clear();

  void synchronizePending();
}

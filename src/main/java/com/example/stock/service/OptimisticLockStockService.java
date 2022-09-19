package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.dto.StockDTO;
import com.example.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OptimisticLockStockService {

  private final StockRepository stockRepository;

  @Transactional
  public void decrease(Long id, Long quantity) {

    //get stock
    //재고감소
    //저장
    Stock stock = stockRepository.findByIdWithOptimisticLock(id);

    stock.decrease(quantity);

//    stockRepository.save(stock);
  }

}

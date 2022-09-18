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
  public synchronized void decrease(Long id, Long quantity) {

      //get stock
      //재고감소
      //저장

    Stock stock = stockRepository.findById(id).orElseThrow();

    stock.decrease(quantity);

    stockRepository.saveAndFlush(stock);
  }

  @Transactional
  public StockDTO getStock(Long id) {

    Stock stock = stockRepository.findByProductId(id);

    return new StockDTO(stock.getId(), stock.getProductId(), stock.getQuantity());
  }


}

package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.dto.StockDTO;
import com.example.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OptimisticLockStockService {

  private final StockRepository stockRepository;

  @Transactional
  public void decrease(Long id, Long quantity) {

//    Optional<Stock> stockVal = stockRepository.findById(id);
//    log.info("=> stockVal : {}", stockVal.isPresent());
    //get stock
    //재고감소
    //저장
    Stock stock = stockRepository.findByIdWithOptimisticLock(id);

    stock.decrease(quantity);

    stockRepository.save(stock);// 그런데
  }

}

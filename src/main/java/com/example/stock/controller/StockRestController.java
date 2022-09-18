package com.example.stock.controller;


import com.example.stock.dto.StockDTO;
import com.example.stock.service.PessimisticStockService;
import com.example.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StockRestController {

  private final PessimisticStockService pessimisticStockService;
  private final StockService stockService;

  @PostMapping("/api/stock")
  public void buyOne() {
    log.info("buyOne");
    pessimisticStockService.decrease(100l, 1l);

  }

  @GetMapping("/api/stock")
  public StockDTO getOne() {
    log.info("getOne()");
    StockDTO stock = stockService.getStock(100l);
    return stock;
  }

}

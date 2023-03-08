package com.example.stock.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PessimisticStockServiceTest {

  @Autowired
  private PessimisticStockService stockService;

  @Autowired
  private StockRepository stockRepository;

  Stock stock;
  @BeforeEach
  public void before() {
    stock = new Stock(1l, 100l);
    stockRepository.saveAndFlush(stock);
  }

  @AfterEach
  public void after() {

    stockRepository.deleteAll();

  }

  //요청 1개라 가정
  @Test
  public void stock_decrease() {
    stockService.decrease(1l, 1l);

    //100-1 == 99
    Stock stock = stockRepository.findById(1l).orElseThrow();
    assertEquals(99, stock.getQuantity());
  }

  @Test
  public void 동시에_100개의_요청() throws InterruptedException {

    Long id = stock.getId();

    int threadCount = 100;
    ExecutorService executorService = Executors.newFixedThreadPool(16);
    // 100개 요청 끝날때까지 기달려야 하므로 countLatch 사용
    // countdown latch 는 단일스레드에서 수행중인 작업이 완료될 때까지 대기할 수 있도록 도와주는 클래스

    CountDownLatch latch = new CountDownLatch(threadCount);

    for (int i = 0; i < threadCount; i++) {
      executorService.submit(
          () -> {

            try {
              stockService.decrease(id, 1l);
            } finally {
              latch.countDown();
            }

          }
      );
    }
    latch.await();
    //모은 요청이 완료되면 stockRepository 를 통해 값을 비교해줌
    List<Stock> all = stockRepository.findAll();
    System.out.println("all = " + all.toString());
    Stock stock = stockRepository.findById(id).orElseThrow();
    //100 - (100) == 0 <- 기대값
    assertEquals(0l, stock.getQuantity());

    // not happening
    // race condition 이 발생하기 때문

  }


}
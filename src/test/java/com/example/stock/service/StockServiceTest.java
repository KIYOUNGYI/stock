package com.example.stock.service;

import static org.junit.jupiter.api.Assertions.*;

import com.example.stock.domain.Stock;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.stock.repository.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * a> race-condition 발생하는 상황
 */
@SpringBootTest
class StockServiceTest {

  @Autowired
  private StockService stockService;

  @Autowired
  private StockRepository stockRepository;

  @BeforeEach
  public void before() {
    Stock stock = new Stock(1l, 100l);
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

    //100개의 요청을 보낼 것이므로
    int threadCount = 100;
    ExecutorService executorService = Executors.newFixedThreadPool(32);
    // 100개 요청 끝날때까지 기달려야 하므로 countLatch 사용
    // countdown latch 는 단일스레드에서 수행중인 작업이 완료될 때까지 대기할 수 있도록 도와주는 클래스

    CountDownLatch latch = new CountDownLatch(threadCount);

    for (int i = 0; i < threadCount; i++) {
      executorService.submit(
          () -> {
            try {
              stockService.decrease(1l, 1l);
            } finally {
              latch.countDown();
            }
          }
      );
    }
    latch.await();
    //모은 요청이 완료되면 stockRepository 를 통해 값을 비교해줌

    Stock stock = stockRepository.findById(1l).orElseThrow();
    //100 - (100) == 0 <- 기대값
    assertEquals(0l, stock.getQuantity());

    // not happening
    // race condition 이 발생하기 때문

  }


}
package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.facade.LettuceLockStockFacade;
import com.example.stock.facade.RedissonLockStockFacade;
import com.example.stock.repository.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class RedissonLockStockFacadeTest {

    @Autowired
    private RedissonLockStockFacade stockService;

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

    @Test
    public void 동시에_100개의_요청() throws InterruptedException {

        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(16);
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
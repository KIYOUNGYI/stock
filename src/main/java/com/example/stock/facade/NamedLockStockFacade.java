package com.example.stock.facade;

import com.example.stock.repository.LockRepository;
import com.example.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Component
@RequiredArgsConstructor
@Service
public class NamedLockStockFacade {

    private final LockRepository lockRepository;

    private final StockService stockService;

    @Transactional
    public void decrease(Long id, Long quantity) {

        try {
            lockRepository.getLock(id.toString());
            stockService.decreasePropagationRequiresNew(id, quantity);
        } finally {
            lockRepository.releaseLock(id.toString());
        }
    }
}

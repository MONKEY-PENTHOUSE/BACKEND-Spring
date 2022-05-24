package com.monkeypenthouse.core.component;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

@Component
@RequiredArgsConstructor
public class OrderIdGenerator {

    private final CacheManager cacheManager;
    private volatile LocalDate orderDate = LocalDate.now();

    // Thread-Safe Method
    public String generate() {

        final LocalDate today = LocalDate.now();
        // 날짜 변경 시 업데이트 및 리셋 로직
        if (!today.equals(orderDate)) {
            // Thread-Safe
            synchronized (this) {
                // 이미 리셋되었을 경우를 대비하여 한번 더 체크
                if (!today.equals(orderDate)) {
                    orderDate = today;
                    cacheManager.setOrderIdSerialNum(1L);
                }
            }
        }

//        RLock lock = cacheManager.tryLockOnOrderIdSerialNum(2, 1);

        Long value = cacheManager.getOrderIdSerialNum();

        cacheManager.setOrderIdSerialNum(value + 1);

//        cacheManager.unLock(lock);

        return orderDate.format(
                DateTimeFormatter.ofPattern("yyMMdd")) +
                String.format("%06d", value
                );
    }


}

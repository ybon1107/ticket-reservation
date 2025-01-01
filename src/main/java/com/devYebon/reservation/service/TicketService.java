package com.devYebon.reservation.service;

import com.devYebon.reservation.util.RedisQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class TicketService {

    @Autowired
    private RedisQueue redisQueue;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String QUEUE_NAME = "ticket_reservation_queue";
    private static final String LOCK_KEY_PREFIX = "lock:seat:";

    public CompletableFuture<String> reserveTicket(String seatNumber, String userName) {
        String reservationMessage = seatNumber + "," + userName;
        redisQueue.addToQueue(QUEUE_NAME, reservationMessage);
        return processQueue();
    }

    @Async
    public CompletableFuture<String> processQueue() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String message;
                while ((message = redisQueue.popFromQueue(QUEUE_NAME)) != null) {
                    String[] parts = message.split(",");
                    String seatNumber = parts[0];
                    String userName = parts[1];

                    String lockKey = LOCK_KEY_PREFIX + seatNumber;
                    Boolean lock = redisTemplate.opsForValue().setIfAbsent(lockKey, "locked", 5, TimeUnit.SECONDS);

                    if (lock != null && lock) {
                        try {
                            if (reserveSeat(seatNumber, userName)) {
                                return "예약 성공: " + seatNumber + " for " + userName;
                            } else {
                                return "예약 실패: " + seatNumber + " for " + userName;
                            }
                        } finally {
                            redisTemplate.delete(lockKey);  // 잠금 해제
                        }
                    } else {
                        return "예약 실패: " + seatNumber + "은(는) 이미 예약 중입니다.";
                    }
                }
            } catch (Exception e) {
                return "서버 오류: " + e.getMessage();
            }
            return "처리 완료";
        });
    }

    public boolean reserveSeat(String seatNumber, String userName) {
        String seatKey = "seat:" + seatNumber;
        if (!redisTemplate.hasKey(seatKey)) {
            redisTemplate.opsForValue().set(seatKey, userName);
            return true;
        }
        throw new IllegalArgumentException("이미 예약된 좌석입니다.");
    }

    public boolean isSeatReserved(String seatNumber) {
        String seatKey = "seat:" + seatNumber;
        return redisTemplate.hasKey(seatKey);
    }
}

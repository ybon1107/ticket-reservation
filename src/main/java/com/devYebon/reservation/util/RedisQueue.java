package com.devYebon.reservation.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisQueue {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void addToQueue(String queueName, String message) {
        redisTemplate.opsForList().rightPush(queueName, message);
    }

    public String popFromQueue(String queueName) {
        return redisTemplate.opsForList().leftPop(queueName);
    }

    public RedisTemplate<String, String> getRedisTemplate() {
        return redisTemplate;
    }
}

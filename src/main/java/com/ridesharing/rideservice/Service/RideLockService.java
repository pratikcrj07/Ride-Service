package com.ridesharing.rideservice.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RideLockService {

    private final StringRedisTemplate redisTemplate;

    public boolean lockRide(Long rideId) {
        String key = "ride:lock:" + rideId;
        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(key, "locked", Duration.ofSeconds(10));
        return Boolean.TRUE.equals(success);
    }

    public void unlockRide(Long rideId) {
        redisTemplate.delete("ride:lock:" + rideId);
    }
}

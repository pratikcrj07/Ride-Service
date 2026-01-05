package com.ridesharing.rideservice.Service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class DriverLocationService {

    private static final String DRIVER_GEO_KEY = "drivers:location";
    private static final String DRIVER_ONLINE_KEY = "drivers:online";

    private final StringRedisTemplate redisTemplate;

    /**
     * Called every 2–5 seconds from Driver App
     */
    public void updateLocation(
            Long driverId,
            double latitude,
            double longitude) {

        // 1. Update GEO location
        redisTemplate.opsForGeo().add(
                DRIVER_GEO_KEY,
                new Point(longitude, latitude),
                driverId.toString()
        );

        // 2. Mark driver online
        redisTemplate.opsForSet()
                .add(DRIVER_ONLINE_KEY, driverId.toString());

        // 3. Heartbeat with TTL (driver liveness)
        redisTemplate.opsForValue().set(
                heartbeatKey(driverId),
                "1",
                Duration.ofSeconds(15)
        );
    }

    public void markOffline(Long driverId) {
        redisTemplate.opsForSet()
                .remove(DRIVER_ONLINE_KEY, driverId.toString());
        redisTemplate.opsForGeo()
                .remove(DRIVER_GEO_KEY, driverId.toString());
        redisTemplate.delete(heartbeatKey(driverId));
    }

    private String heartbeatKey(Long driverId) {
        return "driver:" + driverId + ":heartbeat";
    }
}

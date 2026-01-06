package com.ridesharing.rideservice.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class DriverLocationService {

    private static final String DRIVER_GEO_KEY = "drivers:location";
    private static final String DRIVER_ONLINE_KEY = "drivers:online";
    private static final Duration HEARTBEAT_TTL = Duration.ofSeconds(15);

    private final StringRedisTemplate redisTemplate;

    public void updateLocation(Long driverId, double lat, double lon) {

        //  Update GEO location
        redisTemplate.opsForGeo().add(
                DRIVER_GEO_KEY,
                new org.springframework.data.geo.Point(lon, lat),
                driverId.toString()
        );

        //  Mark driver online
        redisTemplate.opsForSet()
                .add(DRIVER_ONLINE_KEY, driverId.toString());

        //  Refresh heartbeat
        redisTemplate.opsForValue().set(
                heartbeatKey(driverId),
                "1",
                HEARTBEAT_TTL
        );
    }

    public void markOffline(Long driverId) {

        redisTemplate.opsForSet()
                .remove(DRIVER_ONLINE_KEY, driverId.toString());

        redisTemplate.opsForGeo()
                .remove(DRIVER_GEO_KEY, driverId.toString());

        redisTemplate.delete(heartbeatKey(driverId));
    }

    public boolean isHeartbeatAlive(Long driverId) {
        return Boolean.TRUE.equals(
                redisTemplate.hasKey(heartbeatKey(driverId))
        );
    }

    private String heartbeatKey(Long driverId) {
        return "driver:" + driverId + ":heartbeat";
    }
}

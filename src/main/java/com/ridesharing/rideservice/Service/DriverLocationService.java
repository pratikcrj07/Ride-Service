package com.ridesharing.rideservice.Service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DriverLocationService {

    private final StringRedisTemplate redisTemplate;

    public void updateLocation(Long driverId, double lat, double lon) {
        redisTemplate.opsForGeo().add(
                "drivers:location",
                new Point(lon, lat),
                driverId.toString()
        );
    }
}

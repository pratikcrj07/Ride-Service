package com.ridesharing.rideservice.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DriverSearchService {

    private static final String DRIVER_GEO_KEY = "drivers:location";
    private static final String DRIVER_ONLINE_KEY = "drivers:online";

    private final StringRedisTemplate redisTemplate;

    public List<Long> findNearbyDrivers(
            double latitude,
            double longitude,
            double radiusKm) {

        Circle circle = new Circle(
                new Point(longitude, latitude),
                new Distance(radiusKm, Metrics.KILOMETERS)
        );

        GeoResults<RedisGeoCommands.GeoLocation<String>> results =
                redisTemplate.opsForGeo()
                        .radius(DRIVER_GEO_KEY, circle);

        if (results == null) return List.of();

        Set<String> onlineDrivers =
                redisTemplate.opsForSet()
                        .members(DRIVER_ONLINE_KEY);

        if (onlineDrivers == null) return List.of();

        return results.getContent()
                .stream()
                .map(r -> r.getContent().getName())
                .filter(onlineDrivers::contains)
                .map(Long::parseLong)
                .toList();
    }
}


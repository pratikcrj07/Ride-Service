package com.ridesharing.rideservice.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DriverHeartbeatMonitor {

    private static final String DRIVER_ONLINE_KEY = "drivers:online";

    private final StringRedisTemplate redisTemplate;
    private final DriverLocationService locationService;

//    filter out expired driver after every 10 sec
    @Scheduled(fixedRate = 10000)
    public void evictOfflineDrivers() {

        Set<String> onlineDrivers =
                redisTemplate.opsForSet().members(DRIVER_ONLINE_KEY);

        if (onlineDrivers == null || onlineDrivers.isEmpty()) {
            return;
        }

        for (String driverIdStr : onlineDrivers) {
            Long driverId = Long.parseLong(driverIdStr);

            boolean alive = locationService.isHeartbeatAlive(driverId);

            if (!alive) {
                locationService.markOffline(driverId);
            }
        }
    }
}

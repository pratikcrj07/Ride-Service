package com.ridesharing.matching;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MatchingService {

    public void reassignRide(Long rideId) {
        // For now just log (this proves wiring works)
        log.info("Starting reassignment flow for rideId={}", rideId);

        /*
         NEXT PHASE (we will implement):
         1. Fetch pickup location
         2. Redis GEO search drivers (5km)
         3. Send offers
         4. Timeout + retry
        */
    }
}

package com.ridesharing.matching;

import com.ridesharing.rideservice.DTOs.RideEvent;
import com.ridesharing.rideservice.Entities.RideEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RideEventConsumer {

    private final MatchingService matchingService;

    @KafkaListener(
            topics = "ride-events",
            groupId = "ride-matching-group"
    )
    public void handleRideEvent(RideEvent event) {

        log.info("Received ride event: {}", event);

        if (event.getType() == RideEventType.DRIVER_CANCELLED) {
            matchingService.reassignRide(event.getRideId());
        }
    }
}

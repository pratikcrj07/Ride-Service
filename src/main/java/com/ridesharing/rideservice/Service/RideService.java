package com.ridesharing.rideservice.Service;

import com.ridesharing.rideservice.DTOs.RideEvent;
import com.ridesharing.rideservice.DTOs.RideRequestDto;
import com.ridesharing.rideservice.Entities.Ride;
import com.ridesharing.rideservice.Entities.RideEventType;
import com.ridesharing.rideservice.Entities.RideStatus;
import com.ridesharing.rideservice.Repository.RideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
@Service
@RequiredArgsConstructor
public class RideService {

    private final RideRepository rideRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public Ride requestRide(RideRequestDto dto, Long userId) {

        Ride ride = Ride.builder()
                .userId(userId)
                .pickupLat(dto.getPickupLat())
                .pickupLon(dto.getPickupLon())
                .dropLat(dto.getDropLat())
                .dropLon(dto.getDropLon())
                .build();

        Ride saved = rideRepository.save(ride);

        RideEvent event = new RideEvent(
                RideEventType.RIDE_REQUESTED,
                saved.getId(),
                userId,
                null,                 // driverId is null at request time
                Instant.now()
        );

        kafkaTemplate.send("ride-events", saved.getId().toString(), event);

        return saved;
    }

    @Transactional
    public void cancelRide(Long rideId, Long userId) {

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        if (!ride.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized cancellation attempt");
        }

        if (ride.getStatus() != RideStatus.REQUESTED) {
            throw new RuntimeException("Ride cannot be cancelled now");
        }

        ride.setStatus(RideStatus.CANCELLED);
        rideRepository.save(ride);

        RideEvent event = new RideEvent(
                RideEventType.RIDE_CANCELLED,
                rideId,
                userId,
                null,
                Instant.now()
        );


        kafkaTemplate.send("ride-events", rideId.toString(), event);
    }
}

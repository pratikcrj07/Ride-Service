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
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RideService {

    private final RideRepository rideRepository;
    private final KafkaTemplate<String, RideEvent> kafkaTemplate;
    private final RideLockService rideLockService;
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
                null,
                Instant.now()
        );

        kafkaTemplate.send("ride-events", saved.getId().toString(), event);

        return saved;
    }

    @Transactional(readOnly = true)
    public List<Ride> getMyRides(Long userId) {
        return rideRepository.findByUserIdOrderByRequestedAtDesc(userId);
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
    @Transactional
    public Ride acceptRide(Long rideId, Long driverId) {

        if (!rideLockService.lockRide(rideId)) {
            throw new RuntimeException("Ride already being accepted");
        }

        try {
            Ride ride = rideRepository.findByIdAndStatus(rideId, RideStatus.REQUESTED)
                    .orElseThrow(() -> new RuntimeException("Ride not available"));

            ride.setDriverId(driverId);
            ride.setStatus(RideStatus.ACCEPTED);

            Ride saved = rideRepository.save(ride);

            kafkaTemplate.send("ride-events",
                    rideId.toString(),
                    new RideEvent(
                            RideEventType.RIDE_ACCEPTED,
                            rideId,
                            ride.getUserId(),
                            driverId,
                            Instant.now()
                    )
            );

            return saved;
        } finally {
            rideLockService.unlockRide(rideId);
        }
    }


    @Transactional
    public Ride startRide(Long rideId, Long driverId) {

        Ride ride = rideRepository.findByIdAndDriverId(rideId, driverId)
                .orElseThrow(() -> new RuntimeException("Ride not assigned"));

        if (ride.getStatus() != RideStatus.ACCEPTED) {
            throw new RuntimeException("Ride cannot be started");
        }

        ride.setStatus(RideStatus.STARTED);

        Ride saved = rideRepository.save(ride);

        kafkaTemplate.send("ride-events",
                rideId.toString(),
                new RideEvent(
                        RideEventType.RIDE_STARTED,
                        rideId,
                        ride.getUserId(),
                        driverId,
                        Instant.now()
                )
        );

        return saved;
    }
    @Transactional
    public Ride completeRide(Long rideId, Long driverId) {

        Ride ride = rideRepository.findByIdAndDriverId(rideId, driverId)
                .orElseThrow(() -> new RuntimeException("Ride not assigned"));

        if (ride.getStatus() != RideStatus.STARTED) {
            throw new RuntimeException("Ride not in progress");
        }

        ride.setStatus(RideStatus.COMPLETED);
        ride.setCompletedAt(LocalDateTime.now());

        Ride saved = rideRepository.save(ride);

        kafkaTemplate.send("ride-events",
                rideId.toString(),
                new RideEvent(
                        RideEventType.RIDE_COMPLETED,
                        rideId,
                        ride.getUserId(),
                        driverId,
                        Instant.now()
                )
        );

        return saved;
    }
    @Transactional
    public void driverCancelRide(Long rideId, Long driverId) {

        Ride ride = rideRepository.findByIdAndDriverId(rideId, driverId)
                .orElseThrow(() -> new RuntimeException("Ride not assigned"));

        if (ride.getStatus() != RideStatus.ACCEPTED) {
            throw new RuntimeException("Cannot cancel now");
        }

        ride.setDriverId(null);
        ride.setStatus(RideStatus.REQUESTED);

        rideRepository.save(ride);

        kafkaTemplate.send("ride-events",
                rideId.toString(),
                new RideEvent(
                        RideEventType.RIDE_CANCELLED,
                        rideId,
                        ride.getUserId(),
                        driverId,
                        Instant.now()
                )
        );
        KafkaTemplate.send ("ride_events",
                rideId.toString(),
                new RideEvent(
                        RideEventType.RIDE_REASSIGN_REQUEST,
                        rideId,
                        driverId,
                        Instant.now()
                )
        );
    }


}

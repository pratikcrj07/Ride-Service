package com.ridesharing.rideservice.Service;

import com.ridesharing.rideservice.DTOs.RideRequestDto;
import com.ridesharing.rideservice.Entities.Ride;
import com.ridesharing.rideservice.Entities.RideStatus;
import com.ridesharing.rideservice.Repository.RideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RideService {

    private final RideRepository rideRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;


    @Transactional
    public Ride requestRide(RideRequestDto dto, Long userId) {

        Ride ride = Ride.builder()
                .userId(userId)
                .pickupLat(dto.getPickupLat())
                .pickupLon(dto.getPickupLon())
                .dropLat(dto.getDropLat())
                .dropLon(dto.getDropLon())
                .build(); // status + requestedAt handled by @PrePersist

        Ride saved = rideRepository.save(ride);

        kafkaTemplate.send("ride.requested", saved.getId().toString());

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

        kafkaTemplate.send("ride.cancelled", rideId.toString());
    }
}

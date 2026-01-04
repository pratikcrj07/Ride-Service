package com.ridesharing.rideservice.Repository;

import com.ridesharing.rideservice.Entities.Ride;
import com.ridesharing.rideservice.Entities.RideStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RideRepository extends JpaRepository<Ride, Long> {

    List<Ride> findByUserIdOrderByRequestedAtDesc(Long userId);

    Optional<Ride> findByIdAndStatus(Long id, RideStatus status);

    Optional<Ride> findByIdAndDriverId(Long id, Long driverId);

    List<Ride> findByStatus(RideStatus status);
}

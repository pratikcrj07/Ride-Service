package com.ridesharing.rideservice.Repository;

import com.ridesharing.rideservice.Entities.Ride;
import com.ridesharing.rideservice.Entities.RideStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RideRepository extends JpaRepository<Ride, Long> {

    List<Ride> findByUserIdOrderByRequestedAtDesc(Long userId);

    List<Ride> findByStatus(RideStatus status);
}

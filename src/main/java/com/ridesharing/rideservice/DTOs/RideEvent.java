package com.ridesharing.rideservice.DTOs;

import com.ridesharing.rideservice.Entities.RideEventType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class RideEvent {

        private RideEventType eventType;
        private Long rideId;
        private Long userId;
        private Long driverId;
        private Instant timestamp;
    }



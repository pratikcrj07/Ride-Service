package com.ridesharing.rideservice.DTOs;

import com.ridesharing.rideservice.Entities.RideEventType;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideEvent implements Serializable {

    private RideEventType type;
    private Long rideId;
    private Long userId;
    private Long driverId;
    private Instant timestamp;
}

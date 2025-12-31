package com.ridesharing.rideservice.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Entity
@Table(name = "rides")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long driverId;

    @Enumerated(EnumType.STRING)
    private RideStatus status;

    private Double pickupLat;
    private Double pickupLon;
    private Double dropLat;
    private Double dropLon;

    private Double fare;

    private LocalDateTime requestedAt;
    private LocalDateTime completedAt;

    @PrePersist
    public void onCreate() {
        this.requestedAt = LocalDateTime.now();
        this.status = RideStatus.REQUESTED;
    }
}

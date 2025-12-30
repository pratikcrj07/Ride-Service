package com.ridesharing.rideservice.DTOs;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RideRequestDto {

    @NotNull
    private Double pickupLat;

    @NotNull
    private Double pickupLon;

    @NotNull
    private Double dropLat;

    @NotNull
    private Double dropLon;
}

package com.ridesharing.rideservice.Controller;

import com.ridesharing.rideservice.DTOs.DriverLocationUpdateRequest;
import com.ridesharing.rideservice.Service.DriverLocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Request;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping ("api/driver/location")
@RequiredArgsConstructor
public class DriverLocationController {
    private final DriverLocationService locationService;

    @PostMapping
    public RequestEntity<Void> updateLocation(
            @RequestHeader ("X-DRIVER-ID") Long DriverId,
            @Valid @RequestBody DriverLocationUpdateRequest request) {
        locationService.updateLocation(
                driverId,
                request.getLatitude(),
                request.getLongitude()

        );
        return ResponseEntity.ok().build();

    }
    @PostMapping("/offline")
    public ResponseEntity<Void> goOffline(
            @RequestHeader("X-DRIVER-ID") Long driverId) {

        locationService.markOffline(driverId);
        return ResponseEntity.ok().build();
    }
}



}

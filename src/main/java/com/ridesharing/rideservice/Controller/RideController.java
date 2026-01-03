    package com.ridesharing.rideservice.Controller;
    
    import com.ridesharing.rideservice.DTOs.RideRequestDto;
    import com.ridesharing.rideservice.Entities.Ride;
    import com.ridesharing.rideservice.Service.RideService;
    import jakarta.validation.Valid;
    import lombok.RequiredArgsConstructor;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.security.core.Authentication;
    import org.springframework.web.bind.annotation.*;
    
    import java.util.List;
    
    @RestController
    @RequestMapping("/api/rides")
    @RequiredArgsConstructor
    @PreAuthorize("hasRole('USER')")
    public class RideController {
    
        private final RideService rideService;
    
        @PostMapping("/request")
        public ResponseEntity<Ride> requestRide(
                @Valid @RequestBody RideRequestDto dto,
                Authentication authentication
        ) {
            Long userId = Long.parseLong(authentication.getName());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(rideService.requestRide(dto, userId));
        }
    
        @GetMapping("/my")
        public ResponseEntity<List<Ride>> myRides(Authentication authentication) {
            Long userId = Long.parseLong(authentication.getName());
            return ResponseEntity.ok(rideService.getMyRides(userId));
        }
    
        @PostMapping("/{rideId}/cancel")
        public ResponseEntity<Void> cancelRide(
                @PathVariable Long rideId,
                Authentication authentication
        ) {
            Long userId = Long.parseLong(authentication.getName());
            rideService.cancelRide(rideId, userId);
            return ResponseEntity.ok().build();
        }
    }

package com.ridesharing.rideservice.Controller;


import com.ridesharing.rideservice.Entities.Ride;
import com.ridesharing.rideservice.Service.RideService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
    @RequestMapping("/api/driver/rides")
    @RequiredArgsConstructor
    public class DriverRideController {

        private final RideService rideService;

        @PostMapping("/{rideId}/accept")
        public Ride accept(
                @PathVariable Long rideId,
                Authentication auth
        ) {
            Long driverId = (Long) auth.getPrincipal();
            return rideService.acceptRide(rideId, driverId);
        }

        @PostMapping("/{rideId}/start")
        public Ride start(
                @PathVariable Long rideId,
                Authentication auth
        ) {
            Long driverId = (Long) auth.getPrincipal();
            return rideService.startRide(rideId, driverId);
        }

        @PostMapping("/{rideId}/complete")
        public Ride complete(
                @PathVariable Long rideId,
                Authentication auth
        ) {
            Long driverId = (Long) auth.getPrincipal();
            return rideService.completeRide(rideId, driverId);
        }

        @PostMapping("/{rideId}/cancel")
        public void cancel(
                @PathVariable Long rideId,
                Authentication auth
        ) {
            Long driverId = (Long) auth.getPrincipal();
            rideService.driverCancelRide(rideId, driverId);
        }
    }



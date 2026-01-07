package com.ridesharing.rideservice;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RideServiceApplication {

    public static void main(String[] args) {
        // Load .env
        Dotenv dotenv = Dotenv.configure().ignoreIfMalformed().ignoreIfMissing().load();


        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());
        });

        SpringApplication.run(RideServiceApplication.class, args);
    }

}

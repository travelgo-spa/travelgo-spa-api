package com.travelgo.review;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class TravelgoReviewServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(TravelgoReviewServiceApplication.class, args);
    }
}

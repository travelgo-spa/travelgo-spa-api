package com.travelgo.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class TravelgoPaymentServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(TravelgoPaymentServiceApplication.class, args);
    }
}
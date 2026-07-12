package com.travelgo.payment.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "travelgo-spa-api-reservations", url = "${reservationservice.base-url}")
public interface ReservationServiceClient {

    @GetMapping("/api/reservations/user/{userId}")
    Object getReservationsByUser(@PathVariable("userId") Long userId);
}
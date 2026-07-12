package com.travelgo.review.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "travelgo-spa-api", url = "${packageservice.base-url}")
public interface PackageServiceClient {

    @GetMapping("/api/packages/{id}")
    Object getPackageById(@PathVariable("id") Long id);
}

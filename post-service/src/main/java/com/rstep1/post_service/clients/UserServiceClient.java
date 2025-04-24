package com.rstep1.post_service.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(value = "user-service", url = "${user-service.url}")
public interface UserServiceClient {
    @GetMapping("/api/v1/auth/validate")
    ResponseEntity<Long> validateToken(@RequestHeader("Authorization") String token);
}

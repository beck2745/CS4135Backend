package com.skillswap.booking.client.identity;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "identity-service", contextId = "identity", fallbackFactory = IdentityClientFallbackFactory.class)
public interface IdentityClient {

    @GetMapping("/api/internal/users/{userId}/exists")
    Map<String, Boolean> userExists(@PathVariable("userId") Long userId);
}
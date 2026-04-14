package com.skillswap.tutor.client.identity;

import com.skillswap.tutor.client.identity.dto.UserPublicDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "identity-service", contextId = "identityUserClient", path = "/api/internal/users")
public interface IdentityUserClient {

    @PostMapping("/resolve")
    List<UserPublicDTO> resolveUsers(@RequestBody List<Long> userIds);
}

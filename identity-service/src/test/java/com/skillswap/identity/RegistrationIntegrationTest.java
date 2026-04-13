package com.skillswap.identity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillswap.identity.dto.RegisterRequestDTO;
import com.skillswap.identity.valueobject.UserRole;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RegistrationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerDuplicateEmailReturns400() throws Exception {
        String email = "dup-" + UUID.randomUUID() + "@example.com";
        RegisterRequestDTO first = new RegisterRequestDTO();
        first.setEmail(email);
        first.setPassword("abc12");
        first.setRole(UserRole.STUDENT);
        first.setName("A");
        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(first)))
                .andExpect(status().isOk());

        RegisterRequestDTO second = new RegisterRequestDTO();
        second.setEmail(email);
        second.setPassword("def34");
        second.setRole(UserRole.STUDENT);
        second.setName("B");
        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(second)))
                .andExpect(status().isBadRequest());
    }
}
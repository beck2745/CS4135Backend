package com.skillswap.tutor.client.identity.dto;

/**
 * Mirrors {@code com.skillswap.identity.dto.UserPublicDTO} for Feign deserialization.
 */
public record UserPublicDTO(Long userId, String name, String email) {}

package com.skillswap.identity.service;

import com.skillswap.identity.entity.User;

public interface TokenService {
    String generateToken(User user);
}
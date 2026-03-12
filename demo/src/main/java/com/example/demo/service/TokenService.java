package com.example.demo.service;

import com.example.demo.entity.User;

public interface TokenService {
    String generateToken(User user);
}
package com.example.demo.dto;

import com.example.demo.valueobject.UserRole;

public class RegisterRequestDTO {
    private String email;
    private String password;
    private UserRole role;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
}
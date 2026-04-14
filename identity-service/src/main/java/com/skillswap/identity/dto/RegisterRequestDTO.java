package com.skillswap.identity.dto;

import com.skillswap.identity.valueobject.UserRole;

import jakarta.validation.constraints.Pattern;

public class RegisterRequestDTO {
    private String email;

    @Pattern(
            regexp = "^(?=.*\\d).{5,}$",
            message = "Password must be at least 5 characters long and contain at least one number")
    private String password;
    private UserRole role;
    private Long userId;
    private String name;
    private String adminCode;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdminCode() {
        return adminCode;
    }

    public void setAdminCode(String adminCode) {
        this.adminCode = adminCode;
    }
}
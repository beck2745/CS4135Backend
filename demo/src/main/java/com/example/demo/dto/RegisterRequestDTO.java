package com.example.demo.dto;

import com.example.demo.valueobject.UserRole;

public class RegisterRequestDTO {
    private String email;
    private String password;
    private UserRole role;
    private Long userId;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public Long getUserId(){ return userId;}

    private String adminCode;
    public String getAdminCode() { return adminCode; }
    public void setAdminCode(String adminCode) { this.adminCode = adminCode; }
}
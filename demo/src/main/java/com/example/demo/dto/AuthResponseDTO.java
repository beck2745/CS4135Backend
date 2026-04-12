package com.example.demo.dto;

public class AuthResponseDTO {
    private String token;
    private String email;
    private String role;
    private String status;
    private Long userId;
    private String name;

    public AuthResponseDTO(String token, String email, String role, String status, Long userId, String name) {
        this.token = token;
        this.email = email;
        this.role = role;
        this.status = status;
        this.userId = userId;
        this.name = name;
    }

    public String getToken() { return token; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getStatus() { return status; }
    public Long getUserId(){ return userId;}
    public String getName() { return name; }
}
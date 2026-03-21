package com.example.demo.entity;

import com.example.demo.valueobject.AccountState;
import com.example.demo.valueobject.UserRole;
import jakarta.persistence.*;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountState status;

    public User() {}

    public User(String email, String passwordHash, UserRole role, AccountState status, Long userId) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.status = status;
        this.userId=userId;
    }

    public Long getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public UserRole getRole() { return role; }
    public AccountState getStatus() { return status; }

    public void setStatus(AccountState status) { this.status = status; }
}

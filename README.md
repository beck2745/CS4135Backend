### CS4135Backend

## Overview

This repository contains the backend implementation of the **SkillSwap platform**, a skill-sharing system that enables users to act as both students and tutors. The platform supports booking sessions, communication between users, tutor discovery, and administrative moderation.

The backend is built using **Spring Boot** and follows a **layered, architecture** structured around **Domain-Driven Design (DDD)** principles.

---

## Architecture

The system follows a **client–server, microservice-based architecture**, with a React frontend consuming RESTful APIs exposed by this backend.

### Bounded Contexts

- **Identity & Access** – authentication, authorization, user roles  
- **Booking & Scheduling** – session bookings and student profiles  
- **Messaging System** – communication between users  
- **Tutor Profile & Matching** – tutor profiles, skills, and reviews  
- **Admin Features** – reporting, moderation, and blocked content  

Each context encapsulates its own domain logic and communicates via well-defined API contracts.

---

## Tech Stack

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- H2 Database (development)
- Maven
- JWT (authentication)

---

## Project Structure

-  controller - REST API controllers
- service - Business logic layer
- repository - Data access layer (JPA)
- entity / model - Domain entities
- dto - Data Transfer Objects
- valueobject - Enums and domain constraints
-  config - Security and configuration
-  exception - Global exception handling


---

## Features

### Authentication & User Management
- User registration and login
- JWT-based authentication
- Role-based access control (Student, Tutor, Admin)
- Profile update functionality

### Booking & Scheduling
- Create and manage bookings
- Booking lifecycle handling (approve, reject, cancel, complete)
- Student profile management

### Messaging System
- Message threads linked to bookings
- Sending and retrieving messages

### Tutor Profile & Matching
- Tutor profile creation and management
- Skill-based search and filtering
- Tutor reviews and rating system

### Admin Features
- Report submission by users
- Report review and dismissal
- Blocking and unblocking content
- Administrative audit logging

---

# Microservices scaffold (SkillSwap)

This project has **eight runnable Spring Boot apps**

| Folder | Port | Role |
|--------|------|------|
| `discovery-service/` | 8761 | Eureka (service discovery) |
| `config-service/` | 8888 | Spring Cloud Config (native `config-repo/`) |
| `api-gateway/` | 8080 | Spring Cloud Gateway → `lb://…` routes |
| `identity-service/` | 8081 | Auth, users, JWT, internal user ACL (`/api/internal/users/**`) |
| `booking-service/` | 8082 | Bookings + student profiles; **Feign → identity**; internal booking read API |
| `messaging-service/` | 8083 | Threads/messages; **Feign → booking** + Resilience4j circuit breaker |
| `tutor-service/` | 8084 | Tutor profiles, skills, reviews; Feign → identity + booking |
| `admin-service/` | 8085 | Reports, moderation, audit |

## Prerequisites

- Java **21**
- **Maven**
- **Docker Desktop** (optional, for `docker-compose`)

## Run locally (no Docker)

Start in this order:

1. **Discovery** — `cd discovery-service` → `mvn -q -DskipTests spring-boot:run` → `http://localhost:8761`
2. **Config** (from `config-service` so `file:./config-repo` resolves) — `mvn -q -DskipTests spring-boot:run`
3. **Domain services** — each of `identity-service`, `booking-service`, `messaging-service`, `tutor-service`, `admin-service`
4. **Gateway** — `cd api-gateway` → `mvn -q -DskipTests spring-boot:run`

### Smoke tests (through gateway on port 8080)

- `http://localhost:8080/api/auth/health`
- `http://localhost:8080/api/bookings/health`
- `http://localhost:8080/api/student-profiles/health`
- `http://localhost:8080/api/messages/health`
- `http://localhost:8080/api/tutors/health`
- `http://localhost:8080/api/reviews/health`
- `http://localhost:8080/api/admin/health`
- `http://localhost:8080/api/reports/health`

CORS on the gateway allows the Vite dev server on **http://localhost:5173**.

## Run with Docker

From this backend repo root:

```bash
git clone https://github.com/beck2745/CS4135Backend.git
docker compose up --build
```

## Versions

- Spring Boot **3.5.10**
- Spring Cloud BOM **2025.0.0**

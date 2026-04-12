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
src/main/java/com/example/demo

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

## Getting Started

### Prerequisites

- Java 21
- Maven

### Run the Application

```bash
git clone https://github.com/beck2745/CS4135Backend.git
cd demo
mvn spring-boot:run

# 🏦 xBank Backend

**xBank** is a comprehensive banking application demonstration.
The backend is engineered using **Spring Boot (Java 21)**, adhering to **Clean Architecture** principles to provide a
robust, scalable, and testable REST API. It is designed to serve a **Vue.js** frontend.

---

## 📋 Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Architecture](#-architecture)
- [Getting Started](#-getting-started)
- [Testing](#-testing)

---

## ✨ Features

- **Secure Authentication**: Stateless authentication using **JWT (JSON Web Tokens)** and Spring Security.
- **User & Customer Management**: Full lifecycle management for user accounts and customer profiles.
- **Banking Operations**: Manage accounts, view balances, and track transaction history.
- **Media Management**: Secure upload and storage for user profile images and documents.
- **Error Handling**: Centralized exception handling with standardized API responses.

---

## 🚀 Tech Stack

- **Core**: Java 21, Spring Boot 3
- **Security**: Spring Security, JWT (jjwt)
- **Database**: PostgreSQL (via Docker)
- **Testing**: JUnit 5, Mockito, AssertJ
- **Build Tool**: Maven
- **Containerization**: Docker, Docker Compose

---

## 🏗 Architecture

This project follows **Clean Architecture** (Hexagonal Architecture) to separate concerns effectively:

- **Domain**: Core business logic and entities (isolated from frameworks).
- **Application**: Use cases and business rules orchestration.
- **Infrastructure**: Implementation of interfaces (Persistence, API, Security, External Services).
- **Shared**: Common utilities and cross-cutting concerns.

---

## 📦 Getting Started

### Prerequisites

- [Docker](https://www.docker.com/) & Docker Compose
- [Java 21 SDK](https://www.oracle.com/java/technologies/downloads/#java21) (for local development)
- [Maven](https://maven.apache.org/)

### Running with Docker (Recommended)

The easiest way to run the application is using Docker Compose, which sets up the application and the database.

```bash
# Build and start the containers
docker-compose up --build
```

You can also use Makefile to deploy the app using following command

```bash
make deploy
```
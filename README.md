# Hotel Booking System

## Overview

Hotel Booking System là nền tảng đặt phòng khách sạn được xây dựng theo kiến trúc Microservices, lấy cảm hứng từ các hệ thống như Booking.com và Agoda.

Hệ thống cho phép người dùng:

* Đăng ký, đăng nhập và quản lý tài khoản
* Tìm kiếm khách sạn theo địa điểm, giá và đánh giá
* Kiểm tra tình trạng phòng trống
* Đặt phòng và hủy phòng
* Thanh toán trực tuyến
* Đánh giá khách sạn sau khi lưu trú
* Nhận thông báo qua email

---

# System Architecture

```text
                        +------------------+
                        |   React Frontend |
                        +---------+--------+
                                  |
                                  v
                    +--------------------------+
                    |       API Gateway        |
                    | Spring Cloud Gateway     |
                    +------------+-------------+
                                 |
      -------------------------------------------------------
      |            |             |           |              |
      v            v             v           v              v

+-----------+ +-----------+ +-----------+ +-----------+ +-----------+
| Auth      | | Hotel     | | Booking   | | Payment   | | Review    |
| Service   | | Service   | | Service   | | Service   | | Service   |
+-----------+ +-----------+ +-----------+ +-----------+ +-----------+
      |             |             |             |              |
      ----------------------------------------------------------
                                 |
                                 v
                        +----------------+
                        | Notification   |
                        | Service        |
                        +----------------+

```

---

# Microservices

## Auth Service

Responsibilities:

* User Registration
* Login
* JWT Authentication
* Refresh Token
* User Profile Management
* Role-Based Access Control (RBAC)

Default Port:

```text
8086
```

---

## Hotel Service

Responsibilities:

* Hotel Management
* Room Management
* Hotel Search
* Hotel Details

Default Port:

```text
8081
```

---

## Booking Service

Responsibilities:

* Room Availability Checking
* Booking Creation
* Booking Cancellation
* Booking History
* Reservation Management

Default Port:

```text
8082
```

---

## Payment Service

Responsibilities:

* Payment Processing
* Refund Processing
* Transaction History

Default Port:

```text
8083
```

---

## Notification Service

Responsibilities:

* Booking Confirmation Email
* Cancellation Email
* Payment Notification
* Check-in Reminder

Default Port:

```text
8084
```

---

## Review Service

Responsibilities:

* Hotel Rating
* Hotel Reviews
* Review Validation

Default Port:

```text
8085
```

---

# Technology Stack

## Backend

* Java 21
* Spring Boot
* Spring Security
* Spring Data JPA
* Spring Cloud Gateway
* Spring Validation

## Database

* PostgreSQL 17

## Cache

* Redis

## Message Broker

* Apache Kafka

## Containerization

* Docker
* Docker Compose

## Orchestration

* Kubernetes

## Monitoring

* Prometheus
* Grafana

## Logging

* ELK Stack

## CI/CD

* GitHub Actions

---

# Database Architecture

Mỗi service sở hữu database riêng biệt (Database Per Service Pattern).

```text
auth-service         -> user_db
hotel-service        -> hotel_db
booking-service      -> booking_db
payment-service      -> payment_db
review-service       -> review_db
```

---

# Core Business Workflows

## Booking Flow

```text
1. Search Hotel
2. Select Room
3. Check Availability
4. Create Booking (PENDING)
5. Process Payment
6. Confirm Booking
7. Send Email Notification
```

---

## Cancellation Flow

```text
1. Request Cancellation
2. Check Refund Policy
3. Calculate Refund
4. Process Refund
5. Update Booking Status
6. Send Notification
```

---

## Review Flow

```text
1. Complete Stay
2. Submit Review
3. Validate Eligibility
4. Save Review
5. Update Hotel Rating
```

---

# Project Structure

```text
microservices_hotel_booking
│
├── api-gateway
│
├── auth-service
│
├── hotel-service
│
├── booking-service
│
├── payment-service
│
├── notification-service
│
├── review-service
│
├── common-lib
│
├── docker-compose.yml
│
├── pom.xml
│
└── README.md
```

---

# Running with Docker

## Prerequisites

* Docker
* Docker Compose

Verify installation:

```bash
docker --version
docker compose version
```

---

## Start Infrastructure

```bash
docker compose up -d
```

Services started:

* PostgreSQL
* Redis
* Kafka
* Zookeeper
* PgAdmin

---

## Build Application

```bash
mvn clean install
```

---

## Run Services

Example:

```bash
cd auth-service

mvn spring-boot:run
```

Hoặc chạy toàn bộ bằng Docker:

```bash
docker compose up --build
```

---

# API Examples

## Register

```http
POST /api/auth/register
```

Request:

```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123"
}
```

---

## Login

```http
POST /api/auth/login
```

Request:

```json
{
  "email": "john@example.com",
  "password": "password123"
}
```

---

## Search Hotels

```http
GET /api/hotels/search?city=DaNang
```

---

## Create Booking

```http
POST /api/bookings
```

Request:

```json
{
  "userId": 1,
  "roomId": 10,
  "checkIn": "2026-07-01",
  "checkOut": "2026-07-05"
}
```

---

# Security

Implemented:

* JWT Authentication
* BCrypt Password Encryption
* Role-Based Access Control (RBAC)
* Input Validation

Planned:

* Refresh Token Rotation
* Rate Limiting
* Audit Logging
* Secret Management
* HTTPS/TLS

---

# Monitoring & Observability

* Prometheus Metrics
* Grafana Dashboards
* ELK Logging
* Distributed Tracing

---

# Testing

Run Unit Tests:

```bash
mvn test
```

Run Integration Tests:

```bash
mvn verify
```

Target:

```text
Code Coverage > 80%
```

---

# Future Improvements

* Elasticsearch Integration
* Stripe Payment Gateway
* Distributed Transactions (Saga Pattern)
* Redis Distributed Lock
* Kubernetes Deployment
* Auto Scaling
* Multi-region Deployment

---

# Contributors

Developed as a Microservices Architecture Learning & Production-Ready Booking Platform Project.

---

# License

This project is licensed under the MIT License.

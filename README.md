# Hotel Booking System (Microservices)

Nền tảng đặt phòng khách sạn tương tự Booking.com, xây dựng theo kiến trúc microservices.

## Kiến trúc

```
Client → API Gateway (:8080)
           ├── Auth Service (:8086)      → PostgreSQL (user_db)
           ├── Hotel Service (:8081)     → PostgreSQL (hotel_db) + Redis
           ├── Booking Service (:8082)   → PostgreSQL (booking_db) + Kafka
           ├── Payment Service (:8083)   → PostgreSQL (payment_db) + Kafka
           ├── Review Service (:8085)    → PostgreSQL (review_db)
           └── Notification Service (:8084) ← Kafka (email events)
```

## Tech Stack

- Java 21, Spring Boot 3.4.2, Spring Cloud Gateway
- PostgreSQL 17, Redis 7, Apache Kafka
- Flyway (schema migration), OpenFeign (inter-service), JWT + BCrypt
- Docker Compose, GitHub Actions CI

## Quick Start

### Prerequisites

- Java 21, Maven 3.9+, Docker & Docker Compose

### Chạy toàn bộ hệ thống

```bash
docker compose up -d --build
```

API Gateway: http://localhost:8080

### Chạy local (dev)

```bash
# 1. Khởi động infrastructure
docker compose up -d postgres-user postgres-hotel postgres-booking postgres-payment postgres-review redis zookeeper kafka

# 2. Build project
mvn clean install -DskipTests

# 3. Chạy từng service (terminal riêng)
mvn -pl auth_service spring-boot:run
mvn -pl hotel-service spring-boot:run
mvn -pl booking-service spring-boot:run
mvn -pl payment-service spring-boot:run
mvn -pl notification-service spring-boot:run
mvn -pl review-service spring-boot:run
mvn -pl api-gateway spring-boot:run
```

## API Endpoints (qua Gateway :8080)

### Auth
| Method | Path | Mô tả |
|--------|------|-------|
| POST | `/api/auth/register` | Đăng ký |
| POST | `/api/auth/login` | Đăng nhập |
| POST | `/api/auth/refresh-token` | Làm mới token |
| POST | `/api/auth/logout` | Đăng xuất |
| GET | `/api/auth/validate` | Xác thực token |

### Users
| Method | Path | Mô tả |
|--------|------|-------|
| GET | `/api/users/profile` | Xem hồ sơ |
| PUT | `/api/users/profile` | Cập nhật hồ sơ |

### Hotels
| Method | Path | Mô tả |
|--------|------|-------|
| GET | `/api/hotels` | Danh sách khách sạn |
| GET | `/api/hotels/{id}` | Chi tiết khách sạn |
| GET | `/api/hotels/search?city=&minPrice=&maxPrice=&minRating=&sortBy=` | Tìm kiếm |

### Rooms
| Method | Path | Mô tả |
|--------|------|-------|
| GET | `/api/rooms/{id}` | Chi tiết phòng |
| GET | `/api/rooms/available?hotelId=` | Phòng khả dụng |

### Bookings
| Method | Path | Mô tả |
|--------|------|-------|
| POST | `/api/bookings` | Tạo booking (PENDING) |
| GET | `/api/bookings/{id}` | Chi tiết booking |
| GET | `/api/bookings/user?userId=` | Lịch sử booking |
| PUT | `/api/bookings/cancel?bookingId=&userId=` | Hủy booking |

### Payments
| Method | Path | Mô tả |
|--------|------|-------|
| POST | `/api/payments` | Thanh toán → xác nhận booking |
| POST | `/api/payments/refund` | Hoàn tiền |
| GET | `/api/payments/history?userId=` | Lịch sử thanh toán |

### Reviews
| Method | Path | Mô tả |
|--------|------|-------|
| POST | `/api/reviews` | Đánh giá (sau khi hoàn thành lưu trú) |
| GET | `/api/reviews/hotel/{hotelId}` | Đánh giá theo khách sạn |

## Booking Flow

```
1. POST /api/bookings        → Tạo booking (PENDING), kiểm tra availability
2. POST /api/payments        → Thanh toán → Booking chuyển CONFIRMED
3. Kafka event               → Notification gửi email xác nhận
4. PUT /api/bookings/cancel  → Hủy booking
5. POST /api/payments/refund → Hoàn tiền theo chính sách
```

## Environment Variables

| Variable | Default | Mô tả |
|----------|---------|-------|
| `JWT_SECRET` | (dev key) | Secret key cho JWT |
| `JWT_ACCESS_EXPIRE_MS` | 900000 | Access token TTL (15 phút) |
| `JWT_REFRESH_EXPIRE_MS` | 604800000 | Refresh token TTL (7 ngày) |
| `KAFKA_BOOTSTRAP_SERVERS` | localhost:9092 | Kafka broker |
| `REDIS_HOST` | localhost | Redis host |

## Testing

```bash
mvn clean test
```

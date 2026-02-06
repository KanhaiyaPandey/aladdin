# Aladdin Store API

E-commerce backend API built with Spring Boot, MongoDB, Redis, and integrations for Razorpay, Cloudinary, and Google OAuth2.

## Tech Stack
- Java 21, Spring Boot 3.2.x
- MongoDB (Spring Data MongoDB)
- Redis (Spring Data Redis)
- Spring Security + OAuth2 Client
- Razorpay payments
- Cloudinary media uploads
- Spring Mail (SMTP)
- Lombok, MapStruct

## Project Setup

### Prerequisites
- Java 21
- Maven (or use the included `./mvnw`)
- MongoDB instance
- Redis instance (optional for caching, required if enabled)
- Razorpay account (for payments)
- Cloudinary account (for media uploads)
- Google OAuth credentials (for social login)
- SMTP credentials (for email)

### Configuration
Configuration lives in:
- `src/main/resources/application.properties` (local/dev)
- `src/main/resources/production.properties` (production)

These files currently contain real values. Replace them with your own and avoid committing secrets. Suggested keys to set:
- `spring.data.mongodb.uri`
- `spring.data.secretkey` (JWT/secret)
- `server.port`
- `razorpay.api.key`, `razorpay.api.secret`
- `shiprocket.api.email`, `shiprocket.api.pass`
- `cloudinary.cloud_name`, `cloudinary.api_key`, `cloudinary.api_secret`
- `spring.redis.host`, `spring.redis.port`, `spring.redis.password`, `spring.redis.ssl.enabled`
- `spring.mail.host`, `spring.mail.port`, `spring.mail.username`, `spring.mail.password`
- `spring.security.oauth2.client.registration.google.*`
- `frontend.url`

### Run Locally
```bash
./mvnw spring-boot:run
```

### Run Tests
```bash
./mvnw test
```

Server starts on `http://localhost:8080` by default (see `server.port`).

## API Routes

Base URL: `http://localhost:8080`

### Health
- `GET /health/test`

### Auth (User)
Base: `/api/auth/user`
- `POST /login`
- `POST /register`
- `GET /google/login`
- `POST /logout`

Notes:
- `/validate-token` exists in routes but is commented out in `AuthController`.

### Auth Validation
Admin base: `/api/aladdin/admin`
- `GET /validate-token`

User base: `/api/aladdin/user`
- `GET /validate-token`

### Public
Base: `/api/aladdin/public`
- `GET /product/all-products` (supports `name`, `minPrice`, `maxPrice`, `category`, `collection`, `stockStatus` query params)
- `GET /product/{productId}`
- `GET /product/{productId}/related-products`
- `GET /category/all-categories`
- `GET /category/{id}`

### Admin: Categories
Base: `/api/aladdin/admin/category`
- `POST /create-category`
- `PUT /update-category/{categoryId}`
- `DELETE /delete-categories`
- `POST /create-attribute`
- `GET /all-attributes`
- `PUT /update-attribute/{attributeId}`
- `DELETE /delete-attributes`

### Admin: Media
Base: `/api/aladdin/admin/media`
- `POST /upload-media` (multipart/form-data)

### Admin: Products
Base: `/api/aladdin/admin/product`
- `POST /create-product`
- `PUT /update-product/{productId}`
- `DELETE /delete-product/{productId}`

Additional admin product route:
- `GET /api/aladdin/admin/product/{productId}`

### Admin: Orders
Base: `/api/aladdin/admin/orders`
- `GET /all`
- `PUT /update-orders-status`

### Admin: Warehouses
Base: `/api/aladdin/admin/warehouse`
- `POST /create-warehouse`
- `GET /all`

### User: Profile, Address, Cart
Base: `/api/aladdin/user`
- `PUT /update-profile`
- `POST /add-address`
- `PUT /address/{addressId}`
- `DELETE /address/{addressId}` (uses `addressId` as request param)
- `PUT /address/{addressId}/default` (uses `addressId` as request param)
- `POST /update-profile-image` (multipart/form-data)
- `PUT /update-cart`

### User: Orders
Base: `/api/aladdin/user/orders`
- `POST /create`

### User: Payments
Base: `/api/aladdin/user`
- `POST /payment/create-order`
- `POST /payment/verify`
- `POST /payment/webhook`
- `POST /verify-legacy`
- `GET /status/{razorpayOrderId}`

Note: `UserRoutes` defines `/payment/status/{razorpayOrderId}`, but the controller maps `GET /status/{razorpayOrderId}` under `/api/aladdin/user`.

### Route Constants Defined But Not Implemented Yet
These are declared in route classes but do not currently have controller methods:
- Admin orders: `POST /api/aladdin/admin/orders/create`
- Admin orders: `GET /api/aladdin/admin/orders/{orderId}`
- Admin orders: `PUT /api/aladdin/admin/orders/{orderId}/update`
- Admin orders: `DELETE /api/aladdin/admin/orders/{orderId}/delete`
- User orders: `GET /api/aladdin/user/orders/my`
- User orders: `GET /api/aladdin/user/orders/{orderId}`
- User orders: `POST /api/aladdin/user/orders/{orderId}/cancel`
- User orders: `POST /api/aladdin/user/orders/add-to-cart`
- User routes: `GET /api/aladdin/user/address`
- User routes: `GET /api/aladdin/user/orders/my-orders`

## Folder Structure
```
src/
  main/
    java/com/store/aladdin/
      AladdinApplication.java
      configs/
      controllers/
        auth_controllers/
        admincontroller/
        public_controllers/
        user_controllers/
        TestingControllers.java
        UserController.java
      dtos/
      exceptions/
      filters/
      keys/
      models/
      queries/
      repository/
      routes/
      services/
        admin_services/
      utils/
        helper/
        response/
        validation/
      validations/
    resources/
      application.properties
      production.properties
  test/
    java/
```

## Build
```bash
./mvnw clean package
```

## Notes
- Make sure MongoDB and Redis are reachable based on the values in `application.properties`.
- For production, use `production.properties` or environment-based overrides.

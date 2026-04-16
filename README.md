# 🛒 Production‑Ready E‑Commerce Backend (Spring Boot 3 + Java 21)

A production-style **E-Commerce Backend** built using **Spring Boot 3**, **Java 21**, **Spring Security 6**, **JWT**, and **PostgreSQL**.
This project demonstrates real-world backend architecture including authentication, cart management, order lifecycle, payment integration, and role-based access control.

🔗 **GitHub Repository**
[https://github.com/Ajju762/Ecommerce_Backend_Using_SpringBoot](https://github.com/Ajju762/Ecommerce_Backend_Using_SpringBoot)

---

# 🚀 Features

## 🔐 Authentication & Security

* Spring Security 6 configuration
* JWT-based stateless authentication
* User registration & login
* BCrypt password encryption
* Custom JwtAuthenticationFilter
* UserDetailsService implementation
* Role-based authorization (USER / ADMIN)
* Protected APIs using Spring Security

## 🛒 Product Module

* Create product (Admin)
* Update product (Admin)
* Delete product (Admin)
* Get all products with pagination
* Search products
* Filter by category
* Sort by price / name
* DTO-based request/response architecture

## 🛍️ Cart Module

* One cart per user
* Add product to cart
* Update cart item quantity
* Remove item from cart
* Prevent duplicate cart items
* Automatic total calculation

## 📦 Order Module

* Place order from cart
* Convert cart items to order items
* Automatic cart clearing after order
* Price snapshotting at order time
* User order history
* Order status lifecycle

  * PENDING
  * CONFIRMED
  * SHIPPED
  * DELIVERED

## 💳 Payment Integration

* Razorpay payment gateway integration
* Payment order creation
* Payment verification API
* HMAC SHA256 signature validation
* Secure payment confirmation

## 👨‍💼 Admin Module

* Role-based access control
* Admin product management
* Admin order status updates
* 403 Forbidden for unauthorized access

## 🔍 Pagination & Search

* Pageable API responses
* Sorting support
* Filtering support
* Optimized catalog queries

---

# 🏗️ Tech Stack

### Backend

* Java 21
* Spring Boot 3
* Spring Security 6
* JWT Authentication
* Spring Data JPA
* Hibernate

### Database

* PostgreSQL

### Payment

* Razorpay Payment Gateway

### Build Tool

* Maven

---

# 📁 Project Structure

```
src/main/java/com/ecommerce/app
│
├── config
│   ├── RazorpayConfig.java
│   └── SecurityConfig.java
│
├── controller
│   ├── AuthController.java
│   ├── ProductController.java
│   ├── CartController.java
│   ├── OrderController.java
│   ├── PaymentController.java
│   ├── AdminProductController.java
│   └── AdminOrderController.java
│
├── dto
│   ├── AuthResponse.java
│   ├── RegisterRequest.java
│   ├── LoginRequest.java
│   ├── ProductRequestDto.java
│   ├── ProductResponseDto.java
│   ├── CartItemRequest.java
│   ├── CartItemResponse.java
│   ├── CartResponse.java
│   ├── OrderRequest.java
│   ├── OrderResponse.java
│   ├── OrderItemResponse.java
│   ├── PaymentOrderResponse.java
│   └── PaymentVerificationRequest.java
│
├── entity
│   ├── User.java
│   ├── Role.java
│   ├── Product.java
│   ├── Cart.java
│   ├── CartItem.java
│   ├── Order.java
│   ├── OrderItem.java
│   └── OrderStatus.java
│
├── repository
│   ├── UserRepository.java
│   ├── ProductRepository.java
│   ├── CartRepository.java
│   ├── CartItemRepository.java
│   └── OrderRepository.java
│
├── security
│   ├── JwtAuthenticationFilter.java
│   ├── JwtService.java
│   └── UserDetailsServiceImpl.java
│
├── service
│   └── Service layer implementations
│
├── exception
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   └── ErrorResponse.java
│
└── EcommerceApplication.java
```

---

# 🔑 API Modules

## Auth APIs

* Register User
* Login User (Returns JWT)

## Product APIs

* Create Product (Admin)
* Update Product (Admin)
* Delete Product (Admin)
* Get Products (Pagination)
* Search Products

## Cart APIs

* Add to Cart
* Remove from Cart
* Update Quantity
* Get User Cart

## Order APIs

* Place Order
* Get User Orders
* Get Order Details
* Update Order Status (Admin)

## Payment APIs

* Create Razorpay Order
* Verify Payment

---

# 🔐 Security Flow

1. User logs in
2. Server returns JWT token
3. Client sends token in header

```
Authorization: Bearer <JWT_TOKEN>
```

4. JwtAuthenticationFilter validates token
5. Spring Security authorizes request
6. Controller processes request

---

# 🧠 Production Concepts Implemented

* Clean layered architecture
* DTO request/response separation
* JWT stateless authentication
* Role-based access control (RBAC)
* Payment gateway integration
* Order lifecycle management
* Price snapshotting
* Global exception handling
* Validation
* Pagination & sorting
* Secure password storage

---

# ⚙️ Setup & Run Locally

## 1. Clone Repository

```
git clone https://github.com/Ajju762/Ecommerce_Backend_Using_SpringBoot.git
```

## 2. Configure Database

Update application.properties

```
spring.datasource.url=jdbc:postgresql://localhost:5432/ecommerce
spring.datasource.username=postgres
spring.datasource.password=your_password
```

## 3. Configure JWT

```
jwt.secret=your_secret_key
```

## 4. Configure Razorpay

```
razorpay.key=your_key
razorpay.secret=your_secret
```

## 5. Run Application

```
mvn spring-boot:run
```

---

# 📌 Example Headers

```
Authorization: Bearer JWT_TOKEN
Content-Type: application/json
```

---

# 🚧 Upcoming Improvements

* Redis caching
* Swagger / OpenAPI documentation
* Docker containerization
* Rate limiting
* Email notifications
* Unit & integration tests

---

# 👨‍💻 Author

Ajeet Singh Yadav
Backend Developer | Spring Boot | Java | System Design

---

# ⭐ Support

If you found this project helpful, consider giving it a star ⭐

---

# 📬 Feedback

Suggestions, improvements, and contributions are welcome.

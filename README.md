# Harrison RBAC System

A Microservices-based RBAC (Role-Based Access Control) System.

## Project Structure

- **gateway**: API Gateway (Spring Cloud Gateway)
  - Handles routing, authentication filtering (JWT), and global error handling.
  - Port: 8080
- **sso**: Single Sign-On Service (Spring Boot)
  - Handles user login, registration, and permission management.
  - Port: 8081
- **ui**: Frontend Application (Vue 3 + Vite + Element Plus)
  - Provides the user interface for login and system management.
  - Port: 5173

## Prerequisites

- JDK 21
- Node.js 18+
- MySQL 8.0
- Nacos (for service discovery)

## Getting Started

### 1. Database Setup

Run the `rbac.sql` script in your MySQL database to create the necessary tables and initial data.

### 2. Backend Setup

1.  Start Nacos Server.
2.  Update database configuration in `gateway/src/main/resources/application-dev.yml` and `sso/src/main/resources/application-dev.yml`.
3.  Run `GatewayApplication` in the `gateway` module.
4.  Run `SsoApplication` in the `sso` module.

### 3. Frontend Setup

```bash
cd ui
npm install
npm run dev
```

## Features

- **Authentication**: JWT-based authentication with Gateway filter.
- **RBAC**: Dynamic menu generation based on user roles and permissions.
- **I18n**: Internationalization support (English, Simplified Chinese, Traditional Chinese).
- **UI**: Modern login page with glassmorphism effect.

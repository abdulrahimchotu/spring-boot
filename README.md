# Transport Booking System

A Spring Boot application for managing transport bookings across different modes of transportation (Bus, Train, Airplane).

## Overview

This application provides a comprehensive API for managing transport bookings with the following features:

- User authentication and authorization with JWT
- Multiple user roles (USER, DRIVER, ADMIN)
- Journey management for different transport types
- Booking creation and management
- Secure API endpoints with role-based access control

## Authentication

The application uses JWT (JSON Web Token) for authentication. All protected endpoints require a valid JWT token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

### User Roles

The system supports three user roles:

1. **USER** - Regular users who can view journeys and make bookings
2. **DRIVER** - Transport providers who can add journeys
3. **ADMIN** - Administrators who have full access to all features

## Environment Setup

1. Create a PostgreSQL database
2. Copy `.env.example` to `.env` and update with your configuration:
```properties
# Application
APP_NAME=booking
SERVER_PORT=8005

# JWT Configuration
JWT_SECRET=generate_a_secure_key_using_openssl_rand_-base64_32
JWT_EXPIRATION=3600000

# Database Configuration
DB_URL=jdbc:postgresql://localhost:5432/your_database
DB_USERNAME=your_username
DB_PASSWORD=your_password

# Hikari Pool
HIKARI_POOL_SIZE=10
HIKARI_MIN_IDLE=5
HIKARI_IDLE_TIMEOUT=300000
HIKARI_CONNECTION_TIMEOUT=20000
HIKARI_MAX_LIFETIME=1200000

# Logging
LOG_LEVEL=INFO
```
3. Ensure PostgreSQL is running

## Running the Application

1. Clone the repository
2. Navigate to project directory
3. Run: `./mvnw spring-boot:run`
4. Application will start on port 8005 (or the port specified in your .env file)

## Data Models

### User
- `id`: Integer (Auto-generated)
- `username`: String (unique)
- `password`: String
- `email`: String
- `role`: Enum (USER, DRIVER, ADMIN)

### Journey
- `id`: Integer (Auto-generated)
- `user`: User (Many-to-One) - The driver/provider of the journey
- `source`: String
- `destination`: String
- `date`: LocalDate
- `price`: Integer
- `availableSeats`: Integer
- `type`: Enum (BUS, TRAIN, AIRPLANE)

### Booking
- `id`: Integer (Auto-generated)
- `user`: User (Many-to-One) - The passenger
- `journey`: Journey (Many-to-One)
- `bookingTime`: LocalDateTime
- `status`: Enum (BOOKED, CANCELLED)
- `seatCount`: Integer

## API Endpoints

### Authentication Routes

#### Register a new user
- **URL**: `POST /auth/register`
- **Access**: Public
- **Description**: Register a new user with USER or DRIVER role
- **Request Body**:
  ```json
  {
    "username": "john_doe",
    "password": "password123",
    "email": "john@example.com",
    "role": "USER"  // Can be "USER" or "DRIVER"
  }
  ```
- **Response**: 200 OK with message "Registration successful"
- **Validation**:
  - Username: Required, 3-50 characters
  - Password: Required, minimum 6 characters
  - Email: Required, valid email format
  - Role: Required, must be "USER" or "DRIVER"

#### Login
- **URL**: `POST /auth/login`
- **Access**: Public
- **Description**: Authenticate a user and get a JWT token
- **Request Body**:
  ```json
  {
    "username": "john_doe",
    "password": "password123"
  }
  ```
- **Response**:
  ```json
  {
    "token": "your.jwt.token",
    "username": "john_doe",
    "role": "USER"  // Can be "USER", "DRIVER", or "ADMIN"
  }
  ```
- **Validation**:
  - Username: Required
  - Password: Required

### Journey Routes

#### Get All Journeys
- **URL**: `GET /api/journeys/all`
- **Access**: Authenticated users (USER, ADMIN)
- **Description**: Get a list of all available journeys
- **Response**: Array of journey objects
  ```json
  [
    {
      "id": 1,
      "driverUsername": "driver1",
      "source": "City A",
      "destination": "City B",
      "date": "2024-06-15",
      "price": 1000,
      "availableSeats": 50,
      "type": "BUS"
    },
    // More journeys...
  ]
  ```

#### Add Journey
- **URL**: `POST /api/journeys/add`
- **Access**: ADMIN or DRIVER roles only
- **Description**: Add a new journey
- **Request Body**:
  ```json
  {
    "source": "City A",
    "destination": "City B",
    "date": "2024-06-15",
    "price": 1000,
    "availableSeats": 50,
    "type": "BUS"  // Can be "BUS", "TRAIN", or "AIRPLANE"
  }
  ```
- **Response**: 200 OK with message "Journey added successfully"
- **Validation**:
  - Source: Required, non-empty
  - Destination: Required, non-empty
  - Date: Required, must be present or future date
  - Price: Required, must be greater than 0
  - AvailableSeats: Required, must be greater than 0
  - Type: Required, must be one of "BUS", "TRAIN", or "AIRPLANE"

#### Get Journeys by Type
- **URL**: `GET /api/journeys/{type}`
- **Access**: Authenticated users (USER, DRIVER, ADMIN)
- **Description**: Get journeys filtered by transport type
- **Path Parameter**: `type` - Must be one of: BUS, TRAIN, AIRPLANE
- **Response**: Array of journey objects of the specified type

### Booking Routes

#### Get All Bookings
- **URL**: `GET /api/bookings/`
- **Access**: ADMIN role only
- **Description**: Get a list of all bookings in the system
- **Response**: Array of booking objects
  ```json
  [
    {
      "bookingId": 1,
      "passengerUsername": "john_doe",
      "driverUsername": "driver1",
      "source": "City A",
      "destination": "City B",
      "journeyDate": "2024-06-15",
      "transportType": "BUS",
      "seatCount": 2,
      "bookingTime": "2024-05-20T14:30:45",
      "status": "BOOKED"
    },
    // More bookings...
  ]
  ```

#### Book a Journey
- **URL**: `POST /api/bookings/book`
- **Access**: USER role only
- **Description**: Book seats on a journey
- **Request Body**:
  ```json
  {
    "journeyId": 1,
    "seatCount": 2
  }
  ```
- **Response**: 200 OK with message "Journey booked successfully"
- **Validation**:
  - JourneyId: Required
  - SeatCount: Required, must be positive and at least 1
- **Notes**:
  - The user ID is automatically extracted from the JWT token
  - The booking will fail if there aren't enough available seats

## Error Responses

All API errors follow a consistent format:

```json
{
    "timestamp": "2024-05-20T14:30:45",
    "status": 400,
    "error": "Error Type",
    "message": "Detailed error message",
    "path": "/api/endpoint"
}
```

Common error types:
- 400 Bad Request - Invalid input data
- 401 Unauthorized - Missing or invalid authentication
- 403 Forbidden - Insufficient permissions
- 404 Not Found - Resource not found
- 500 Internal Server Error - Server-side error

## Development

### Prerequisites
- Java 21
- Maven
- PostgreSQL

### Build
```bash
./mvnw clean install
```

### Testing
```bash
./mvnw test
```

### Security Notes

- The application uses JWT for stateless authentication
- Passwords are currently stored in plain text (not recommended for production)
- For production, implement proper password hashing with BCrypt or similar
- Generate a strong JWT secret key for production environments

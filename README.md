# Transport Booking System

A Spring Boot application for managing transport bookings across different modes of transportation (Bus, Train, Airplane).

## Database Setup

1. Create a PostgreSQL database
2. Update `src/main/resources/application.properties` with your database credentials:
```properties
spring.datasource.url=jdbc:postgresql://your-host:5432/your-database
spring.datasource.username=your-username
spring.datasource.password=your-password
```

## Running the Application

1. Clone the repository
2. Navigate to project directory
3. Run: `./mvnw spring-boot:run`
4. Application will start on port 8005

## Data Models

### User
- `id`: Integer (Auto-generated)
- `username`: String (unique)
- `password`: String
- `email`: String
- `isAdmin`: Boolean

### Journey
- `id`: Integer (Auto-generated)
- `source`: String
- `destination`: String
- `date`: LocalDate
- `price`: Integer
- `availableSeats`: Integer
- `type`: Enum (BUS, TRAIN, AIRPLANE)

### Booking
- `id`: Integer (Auto-generated)
- `user`: User (Many-to-One)
- `journey`: Journey (Many-to-One)
- `bookingTime`: LocalDateTime
- `status`: Enum (BOOKED, CANCELLED)
- `seatCount`: Integer

## DTOs

### UserDto
```json
{
    "username": "string (3-50 chars)",
    "password": "string (min 6 chars)",
    "email": "valid email"
}
```

### BookingDto
```json
{
    "userId": "integer",
    "journeyId": "integer",
    "seatCount": "integer (min 1)"
}
```

## API Endpoints

### User Routes
- Register: `POST /register`
  ```json
  {
    "username": "john_doe",
    "password": "password123",
    "email": "john@example.com"
  }
  ```
- Login: `POST /login`
  ```json
  {
    "username": "john_doe",
    "password": "password123",
    "email": "john@example.com"
  }
  ```

### Journey Routes
- Get All Journeys: `GET /journey/all`
- Add Journey: `POST /journey/add`
  ```json
  {
    "source": "City A",
    "destination": "City B",
    "date": "2024-02-20",
    "price": 1000,
    "availableSeats": 50,
    "type": "BUS"
  }
  ```
- Get by Type: `GET /journey/{type}` (type: BUS, TRAIN, AIRPLANE)

### Booking Routes
- Get All Bookings: `GET /booking/`
- Book Journey: `POST /booking/book`
  ```json
  {
    "userId": 1,
    "journeyId": 1,
    "seatCount": 2
  }
  ```
- Cancel Booking: `PUT /booking/cancel/{bookingId}`

## Error Responses
```json
{
    "timestamp": "2024-02-20T10:00:00",
    "status": 400,
    "error": "Error Type",
    "message": "Error Message",
    "path": "/api/endpoint"
}
```

## Development

### Prerequisites
- Java 21
- Maven
- PostgreSQL

### Build
```bash
./mvnw clean install
```

### Test
```bash
./mvnw test
```
# Turf Booking System

A comprehensive web application for managing turf bookings for sports facilities. Built with Spring Boot backend and vanilla HTML/CSS/JavaScript frontend.

## Features

- **Turf Management**: Add, update, and manage different types of sports turfs
- **Booking System**: Book turfs for specific time slots with conflict detection
- **Customer Management**: Track customer bookings and history
- **Real-time Availability**: Check turf availability in real-time
- **Multi-sport Support**: Support for football, cricket, tennis, basketball, and more
- **Responsive Design**: Modern and mobile-friendly user interface

## Technology Stack

### Backend
- **Java 17+**
- **Spring Boot 3.x**
- **Spring Data JPA**
- **H2 Database** (for development)
- **Maven** (build tool)

### Frontend
- **HTML5**
- **CSS3**
- **Vanilla JavaScript**
- **Responsive Design**

## Project Structure

```
turf-booking-system/
├── frontend/
│   ├── css/
│   │   └── style.css
│   ├── js/
│   │   ├── main.js
│   │   └── bookings.js
│   ├── images/
│   ├── index.html
│   ├── bookings.html
│   └── contact.html
├── src/
│   └── main/
│       ├── java/com/turfbooking/
│       │   ├── config/
│       │   │   └── WebConfig.java
│       │   ├── controller/
│       │   │   ├── TurfController.java
│       │   │   └── BookingController.java
│       │   ├── model/
│       │   │   ├── Turf.java
│       │   │   └── Booking.java
│       │   ├── repository/
│       │   │   ├── TurfRepository.java
│       │   │   └── BookingRepository.java
│       │   ├── service/
│       │   │   ├── TurfService.java
│       │   │   └── BookingService.java
│       │   ├── util/
│       │   │   └── DateTimeUtil.java
│       │   └── TurfBookingApplication.java
│       └── resources/
│           ├── static/
│           ├── templates/
│           ├── application.properties
│           └── data.sql
├── pom.xml
└── README.md
```

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd turf-booking-system
   ```

2. **Build the project**
   ```bash
   mvn clean install
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

4. **Access the application**
   - Backend API: http://localhost:8080/api
   - Frontend: http://localhost:8080
   - H2 Database Console: http://localhost:8080/h2-console

### Database Configuration

The application uses H2 in-memory database by default with the following credentials:
- **JDBC URL**: `jdbc:h2:mem:turfbookingdb`
- **Username**: `sa`
- **Password**: `password`

## API Endpoints

### Turf Management
- `GET /api/turfs` - Get all turfs
- `GET /api/turfs/available` - Get available turfs
- `GET /api/turfs/{id}` - Get turf by ID
- `GET /api/turfs/sport/{sportType}` - Get turfs by sport type
- `GET /api/turfs/location/{location}` - Get turfs by location
- `POST /api/turfs` - Create new turf
- `PUT /api/turfs/{id}` - Update turf
- `DELETE /api/turfs/{id}` - Delete turf

### Booking Management
- `GET /api/bookings` - Get all bookings
- `GET /api/bookings/{id}` - Get booking by ID
- `GET /api/bookings/customer/{email}` - Get bookings by customer
- `GET /api/bookings/turf/{turfId}` - Get bookings by turf
- `GET /api/bookings/status/{status}` - Get bookings by status
- `POST /api/bookings` - Create new booking
- `PUT /api/bookings/{id}/status` - Update booking status
- `DELETE /api/bookings/{id}` - Cancel booking
- `GET /api/bookings/availability/{turfId}` - Check availability

## Usage

### Booking a Turf

1. Navigate to the bookings page
2. Select your desired turf from the dropdown
3. Choose date and time
4. Fill in your contact details
5. Submit the booking

### Managing Turfs (Admin)

Use the REST API endpoints to:
- Add new turfs to the system
- Update turf information and availability
- View booking statistics
- Manage customer bookings

## Development

### Adding New Features

1. **Model**: Add new entities in `src/main/java/com/turfbooking/model/`
2. **Repository**: Create repository interfaces in `src/main/java/com/turfbooking/repository/`
3. **Service**: Implement business logic in `src/main/java/com/turfbooking/service/`
4. **Controller**: Add REST endpoints in `src/main/java/com/turfbooking/controller/`

### Frontend Development

- Static files are served from `frontend/` directory
- CSS styles are in `frontend/css/style.css`
- JavaScript functionality is in `frontend/js/`

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support and questions, please contact:
- Email: support@turfbooking.com
- Issues: Create an issue on GitHub

## Future Enhancements

- Payment integration
- Email notifications
- Mobile app
- Advanced reporting
- Multi-tenant support
- Real-time chat support
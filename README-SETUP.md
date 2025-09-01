# Turf Booking System - Local Setup Guide

## Prerequisites

Before running this project, make sure you have the following installed:

1. **Java 17 or higher**
   - Download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/)
   - Verify installation: `java -version`

2. **Maven 3.6 or higher**
   - Download from [Apache Maven](https://maven.apache.org/download.cgi)
   - Verify installation: `mvn -version`

3. **Node.js 18 or higher** (for frontend development)
   - Download from [Node.js](https://nodejs.org/)
   - Verify installation: `node -version`

## Local Development Setup

### Step 1: Clone and Setup
```bash
# If you haven't already, navigate to your project directory
cd turf-booking-system

# Install frontend dependencies
npm install
```

### Step 2: Configure Database
The project is configured to use H2 in-memory database for development. No additional setup required.

For production with MySQL:
1. Install MySQL
2. Create database: `CREATE DATABASE turfbooking;`
3. Update `application.properties` with your MySQL credentials

### Step 3: Run the Application

#### Option A: Run Backend and Frontend Separately (Recommended for Development)

**Terminal 1 - Backend (Spring Boot):**
```bash
mvn spring-boot:run
```

**Terminal 2 - Frontend (Vite Dev Server):**
```bash
npm run dev
```

#### Option B: Run Backend Only (Frontend served by Spring Boot)
```bash
mvn spring-boot:run
```

### Step 4: Access the Application

- **Frontend (Development)**: http://localhost:3000
- **Backend API**: http://localhost:8080/api
- **H2 Database Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:turfbookingdb`
  - Username: `sa`
  - Password: `password`

## Google OAuth Setup

To enable Google login:

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing one
3. Enable Google+ API
4. Create OAuth 2.0 credentials
5. Add authorized redirect URIs:
   - `http://localhost:8080/login/oauth2/code/google`
   - `http://localhost:3000/login/oauth2/code/google` (for dev)
6. Update `application.properties` with your client ID and secret

## Building for Production

### Build Frontend
```bash
npm run build
```

### Build Backend JAR
```bash
mvn clean package
```

### Run Production JAR
```bash
java -jar target/turf-booking-system-0.0.1-SNAPSHOT.jar
```

## Deployment Options

### 1. Traditional VPS/Server
- Upload JAR file to server
- Install Java 17+
- Run with: `java -jar turf-booking-system.jar`

### 2. Docker Deployment
Create `Dockerfile`:
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/turf-booking-system-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### 3. Cloud Platforms
- **Heroku**: Add `Procfile` with `web: java -jar target/turf-booking-system-0.0.1-SNAPSHOT.jar`
- **Railway**: Connect GitHub repo and deploy
- **AWS/GCP/Azure**: Use their Java application services

## Environment Variables for Production

Set these environment variables:
```bash
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:mysql://your-db-host:3306/turfbooking
SPRING_DATASOURCE_USERNAME=your-username
SPRING_DATASOURCE_PASSWORD=your-password
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret
```

## Troubleshooting

### Common Issues:

1. **Port 8080 already in use**
   - Change port in `application.properties`: `server.port=8081`

2. **Database connection issues**
   - Check H2 console at http://localhost:8080/h2-console
   - Verify JDBC URL and credentials

3. **Google OAuth not working**
   - Check redirect URIs in Google Console
   - Verify client ID and secret in `application.properties`

4. **Frontend not loading**
   - Run `npm install` to install dependencies
   - Check if Vite dev server is running on port 3000

### Logs
Check application logs for detailed error information:
```bash
tail -f logs/application.log
```

## API Testing

Use tools like Postman or curl to test API endpoints:

```bash
# Get all turfs
curl http://localhost:8080/api/turfs

# Search turfs
curl "http://localhost:8080/api/turfs/search?city=Mumbai&turfType=FOOTBALL"

# Check authentication
curl http://localhost:8080/api/auth/me
```

## Next Steps

1. Test all functionality locally
2. Set up production database (MySQL/PostgreSQL)
3. Configure Google OAuth for production domain
4. Deploy to your preferred hosting platform
5. Set up domain and SSL certificate

For any issues, check the logs and ensure all dependencies are properly installed.
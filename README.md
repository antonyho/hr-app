# HR Application

A full-stack HR management application built with SpringBoot and Next.js, providing employee profile management and absence request functionality with role-based access control.

## Features

### Authentication
- JWT-based authentication with role-based access control
- Manager and Employee roles with different permissions
- Secure password hashing with BCrypt

### Employee Profile Management
- **Managers**: Full access to all employee profiles with sensitive data
- **Employees**: Can view their own detailed profile and basic info of coworkers
- **Profile Data**: Personal info, contact details, department, position, hire date
- Separate DTOs for basic and detailed access levels

### Absence Request System
- **Employees**: Create, view, and delete their own pending absence requests
- **Managers**: View all requests, approve/reject with comments
- Request statuses: PENDING, APPROVED, REJECTED
- Date validation and business logic enforcement

## Technology Stack

### Backend
- **SpringBoot 3.2.0** - Main application framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Database operations
- **PostgreSQL** - Database
- **JWT (JJWT)** - Token-based authentication
- **Maven** - Build tool
- **JUnit 5 & Mockito** - Testing

### Frontend
- **Next.js 14** - React framework with App Router
- **TypeScript** - Type safety
- **Tailwind CSS** - Styling
- **Axios** - HTTP client
- **js-cookie** - Cookie management

### Infrastructure
- **Docker & Docker Compose** - Containerization
- **PostgreSQL** - Database with UUID primary keys
- **Multi-stage Dockerfiles** - Optimized container builds

## Quick Start

### Prerequisites
- Docker and Docker Compose
- Node.js 18+ (for local frontend development)
- Java 17+ (for local backend development)

### Using Docker Compose (Recommended)

1. **Clone the repository**
   ```bash
   git clone https://github.com/antonyho/hr-app
   cd hr-app
   ```

2. **Start all services**
   ```bash
   docker-compose up --build
   ```

3. **Access the application**
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080
   - Database: localhost:5432

### Demo Accounts
- **Manager**: manager@company.com / password
- **Employee 1**: john.doe@company.com / password  
- **Employee 2**: jane.smith@company.com / password

### Database Setup
The database is automatically initialized with sample data when using Docker Compose.

## API Documentation

### Authentication Endpoints
- `POST /api/auth/login` - User login
- `POST /api/auth/logout` - User logout

### Profile Endpoints
- `GET /api/profiles/me` - Get own profile (detailed)
- `GET /api/profiles/basic` - Get all basic profiles
- `GET /api/profiles/detailed` - Get all detailed profiles (managers only)
- `GET /api/profiles/{id}/basic` - Get basic profile by ID
- `GET /api/profiles/{id}/detailed` - Get detailed profile by ID
- `PUT /api/profiles/{id}` - Update profile

### Absence Request Endpoints
- `GET /api/absence-requests/my` - Get own absence requests
- `GET /api/absence-requests/all` - Get all requests (managers only)
- `GET /api/absence-requests/pending` - Get pending requests (managers only)
- `POST /api/absence-requests` - Create absence request
- `PUT /api/absence-requests/{id}/approve` - Approve/reject request (managers only)
- `DELETE /api/absence-requests/{id}` - Delete own pending request

## Database Schema

### Key Tables
- **users**: Authentication and role information
- **employee_profiles**: Employee details and relationships
- **absence_requests**: Time-off requests with approval workflow
- **feedback**: Coworker feedback system (future feature)

### Key Features
- UUID primary keys for all tables
- Proper foreign key relationships
- Enum types for roles and statuses
- Audit timestamps (created_at, updated_at)

## Testing

### Backend Tests
```bash
cd backend
mvn test
```

Test coverage includes:
- Unit tests for all service classes
- Security component testing (JWT utilities)
- Controller integration tests
- Mock-based testing with comprehensive scenarios

### Test Categories
- **AuthService**: Login, token generation, error handling
- **EmployeeProfileService**: Role-based access, CRUD operations
- **AbsenceRequestService**: Request lifecycle, approval workflow
- **JwtUtil**: Token creation, validation, expiration
- **Controllers**: HTTP endpoints, request/response handling

## Security Features

### Input Validation
- Bean validation with JSR-303 annotations
- SQL injection prevention through JPA
- XSS protection via proper content encoding

### Authentication & Authorization
- JWT tokens with configurable expiration
- Role-based access control at service level
- Secure password hashing with BCrypt
- CORS configuration for cross-origin requests

### Data Access Control
- Managers: Full access to all data
- Employees: Own data + basic coworker info
- Separate DTOs prevent data leakage

## Configuration

### Environment Variables
- `SPRING_DATASOURCE_URL`: Database connection URL
- `SPRING_DATASOURCE_USERNAME`: Database username
- `SPRING_DATASOURCE_PASSWORD`: Database password
- `SPRING_SECURITY_JWT_SECRET`: JWT signing key
- `SPRING_SECURITY_JWT_EXPIRATION`: Token expiration time
- `NEXT_PUBLIC_API_URL`: Backend API URL for frontend

### Docker Configuration
The application uses multi-stage Docker builds for optimal image sizes and includes health checks for service dependency management.

## Architecture

### Backend Architecture
- **Controller Layer**: REST endpoints with validation
- **Service Layer**: Business logic and authorization
- **Repository Layer**: Data access with Spring Data JPA
- **Security Layer**: JWT authentication and method-level security
- **DTO Pattern**: Separate request/response models from entities

### Frontend Architecture  
- **App Router**: Next.js 14 with TypeScript
- **Component Architecture**: Reusable UI components
- **State Management**: Cookie-based auth state
- **API Layer**: Centralized HTTP client with interceptors
- **Responsive Design**: Mobile-first with Tailwind CSS

### Environment Setup Needed for Production Readiness
1. Configure production database credentials
2. Set strong JWT secret key
3. Configure CORS for production domains
4. Set up SSL/TLS certificates
5. Configure logging levels
6. Set up monitoring and health checks

## Troubleshooting

### Logs and Monitoring
- Backend logs: Check Spring Boot console output
- Frontend logs: Browser developer console
- Database logs: PostgreSQL container logs
- Health checks available at `/actuator/health`

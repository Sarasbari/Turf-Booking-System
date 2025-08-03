# Replit.md

## Overview

This is a full-stack **Turf Booking System** web application that allows users to search and book sports facilities. The system includes both user and admin portals with comprehensive booking management, payment processing, and facility administration capabilities.

## User Preferences

Preferred communication style: Simple, everyday language.

## System Architecture

### Frontend Architecture
- **Framework**: React 18 with TypeScript
- **Routing**: Wouter for client-side routing
- **State Management**: TanStack React Query for server state management
- **UI Framework**: Shadcn/ui components built on Radix UI primitives
- **Styling**: Tailwind CSS with custom CSS variables for theming
- **Build Tool**: Vite for development and production builds

### Backend Architecture
- **Runtime**: Node.js with Express.js server
- **Language**: TypeScript with ES modules
- **Database ORM**: Drizzle ORM for type-safe database operations
- **Database**: PostgreSQL (configured for Neon serverless)
- **Authentication**: Replit Auth integration with OpenID Connect
- **Session Management**: Express sessions with PostgreSQL store

### Development Environment
- **Monorepo Structure**: Shared code between client and server
- **Hot Reload**: Vite development server with HMR
- **Type Safety**: Full TypeScript coverage across frontend and backend

## Key Components

### Database Schema
- **Users**: Authentication and profile management with role-based access
- **Turfs**: Sports facility information including amenities, pricing, and images
- **Bookings**: Reservation system with status tracking and payment integration
- **Payments**: Financial transaction records linked to bookings
- **Sessions**: Persistent user session storage for authentication

### Authentication System
- **Provider**: Replit OAuth with OpenID Connect
- **Session Storage**: PostgreSQL-backed sessions with configurable TTL
- **Authorization**: Role-based access control (user/admin)
- **Security**: Secure cookies with HTTP-only flags

### API Structure
- RESTful endpoints for CRUD operations
- Authentication middleware for protected routes
- Error handling with standardized HTTP status codes
- Request/response logging for debugging

### Frontend Components
- **Navigation**: Responsive navigation with role-based menu items
- **Booking Flow**: Multi-step booking process with date/time selection
- **Payment Integration**: Modal-based payment form with multiple payment methods
- **Admin Dashboard**: Comprehensive management interface for turfs and bookings
- **Search & Filtering**: Advanced search capabilities with location and sport type filters

## Data Flow

### User Journey
1. **Authentication**: Users authenticate via Replit OAuth
2. **Browse Turfs**: Search and filter available sports facilities
3. **Booking Process**: Select date/time slots and confirm availability
4. **Payment**: Process payment through integrated payment modal
5. **Management**: View and manage bookings through user dashboard

### Admin Workflow
1. **Facility Management**: Add, edit, and delete turf facilities
2. **Booking Oversight**: View, approve, or cancel user bookings
3. **User Management**: Monitor user accounts and activities
4. **Analytics**: Access booking reports and facility utilization data

### Data Synchronization
- Real-time availability checking for booking conflicts
- Optimistic updates with React Query for improved UX
- Automatic cache invalidation on data mutations

## External Dependencies

### Core Libraries
- **Database**: `@neondatabase/serverless` for PostgreSQL connection
- **ORM**: `drizzle-orm` with `drizzle-zod` for schema validation
- **Authentication**: `openid-client` and `passport` for OAuth integration
- **UI Components**: Comprehensive Radix UI ecosystem for accessible components
- **Forms**: React Hook Form with Zod validation
- **Styling**: Tailwind CSS with class-variance-authority for component variants

### Development Tools
- **Build**: Vite with React plugin and TypeScript support
- **Database Management**: Drizzle Kit for migrations and schema management
- **Development**: TSX for TypeScript execution and hot reloading

## Deployment Strategy

### Production Build
- Frontend assets built with Vite and served statically
- Backend bundled with esbuild for optimal performance
- Environment variable configuration for database and authentication

### Database Setup
- PostgreSQL database provisioned through Neon
- Schema managed through Drizzle migrations
- Connection pooling for scalability

### Authentication Configuration
- Replit OAuth configured with proper redirect URLs
- Session secrets and database URLs managed through environment variables
- Secure cookie configuration for production deployment

### Performance Considerations
- Static asset optimization through Vite
- Database query optimization with Drizzle ORM
- React Query caching for reduced API calls
- Image optimization for turf gallery displays

The application follows modern web development best practices with type safety, responsive design, and comprehensive error handling throughout the user experience.
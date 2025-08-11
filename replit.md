# Melbourne Weather Service

## Overview

A robust Spring Boot HTTP service that provides Melbourne weather data with automatic failover between WeatherStack and OpenWeatherMap APIs. The service implements caching, database persistence, and stale data serving for high availability.

## User Preferences

Preferred communication style: Simple, everyday language.

## System Architecture

### Backend Architecture
- **Framework**: Spring Boot 3.2.0 with Java 17
- **Web Layer**: Spring MVC with REST controllers
- **Service Layer**: Modular weather provider services with failover logic
- **Caching**: Caffeine cache with 3-second TTL
- **Error Handling**: Global exception handler for consistent responses

### Data Storage
- **Database**: H2 in-memory database for development
- **ORM**: Spring Data JPA with Hibernate
- **Entity**: WeatherData entity for persisting weather information
- **Repository**: JPA repository with custom queries for latest data retrieval

### Weather Provider Integration
- **Primary**: WeatherStack API (configurable with API key)
- **Fallback**: OpenWeatherMap API (with provided API key)
- **Failover Logic**: Automatic switch to backup provider when primary fails
- **Stale Data**: Serves cached database data when all providers are down

## API Endpoints

### Main Endpoint
- **GET** `/v1/weather?city=melbourne` - Returns weather data for specified city (defaults to Melbourne)
- **Response Format**: `{"temperature_degrees": 6.85, "wind_speed": 3.204}`

### Health Check
- **GET** `/v1/health` - Service health status

### Database Console
- **H2 Console**: `/h2-console` - Database administration interface

## External Dependencies

### Third-party Services
- **WeatherStack API**: Primary weather data provider
- **OpenWeatherMap API**: Backup weather data provider (with demo key included)

### Development Dependencies
- **Maven**: Build and dependency management
- **Spring Boot Starters**: Web, JPA, Cache, Actuator, Validation
- **H2 Database**: In-memory database for development
- **Caffeine**: High-performance caching library
- **Jackson**: JSON serialization/deserialization
- **Mockito**: Unit testing framework

### Configuration
- **Server Port**: 8000
- **Cache TTL**: 3 seconds
- **API Timeouts**: 5 seconds per provider
- **Database URL**: `jdbc:h2:mem:weatherdb`

## Key Features Implemented

✓ **HTTP Service**: Spring Boot REST API on port 8000
✓ **Dual Provider Support**: WeatherStack (primary) + OpenWeatherMap (failover) 
✓ **Caching**: 3-second TTL using Caffeine cache
✓ **Database Persistence**: H2 database with JPA entities
✓ **Stale Data Serving**: Returns last known good data when APIs fail
✓ **Error Handling**: Comprehensive exception handling and logging
✓ **Unit Tests**: Mockito-based tests for all service layers
✓ **Scalability**: Modular design supporting additional weather providers

## Recent Changes (August 11, 2025)

- ✅ **Complete Implementation**: Delivered fully functional weather service meeting all Zai Code Challenge requirements
- ✅ **API Integration**: Successfully integrated both WeatherStack and OpenWeatherMap APIs with proper failover
- ✅ **Database Setup**: Configured H2 database with weather data persistence
- ✅ **Caching Implementation**: Added Caffeine cache with 3-second TTL
- ✅ **Testing Suite**: Created comprehensive unit tests using Mockito
- ✅ **Service Deployment**: Successfully running on port 8000 with real weather data
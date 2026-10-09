# Microservices Architecture with Discovery Server and Redis

This document describes the implementation of a microservices architecture with Eureka Service Discovery and Redis caching.

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                      API Gateway (8080)                      │
│              - Spring Cloud Gateway (WebMVC)                 │
│              - Client Discovery enabled                      │
└────┬────────┬────────────────┬──────────────────┬────────────┘
     │        │                │                  │
     │        │                │                  │
  lb://    lb://            lb://               lb://
product   order           inventory            discovery
service   service         service              server
  │        │                │                  │
┌─┴──┐  ┌──┴──┐          ┌──┴──┐           ┌──┴──┐
│ PS │  │ OS  │          │ IS  │           │ DS  │
│ 8083│  │8081 │          │8082 │           │8761 │
└────┘  └─┬───┘          └────┘           └─────┘
         │
      Redis
     (6379)
```

## Components

### 1. Discovery Server (Eureka Server) - Port 8761
- **Module**: `discovery-server`
- **Purpose**: Service registry for dynamic service discovery
- **Key Configuration**:
  ```properties
  eureka.instance.hostname=localhost
  eureka.client.register-with-eureka=false
  eureka.client.fetch-registry=false
  ```
- **Features**:
  - Auto-registration of microservices
  - Load balancing support
  - Health check monitoring

### 2. API Gateway - Port 8080
- **Module**: `api-gateway`
- **Purpose**: Single entry point for all client requests
- **Features**:
  - Dynamic routing using service discovery
  - Load balancer integration
  - Spring Cloud Gateway WebMVC for synchronous processing
  - Prefix stripping filters
- **Routes**:
  - `/api/products/**` → product-service
  - `/api/orders/**` → order-service
  - `/api/inventory/**` → inventory-service

### 3. Product Service - Port 8083
- **Database**: MongoDB
- **Features**:
  - Eureka client registration
  - REST API endpoints for product management

### 4. Order Service - Port 8081
- **Database**: MySQL
- **Features**:
  - Eureka client registration
  - **Redis Integration**: Caching layer for order data
  - OpenFeign client for inventory service calls
  - Endpoints:
    - `POST /api/order` - Place order
    - `GET /api/order/{id}` - Get order by ID (cached)
    - `DELETE /api/order/{id}` - Delete order

### 5. Inventory Service - Port 8082
- **Database**: MySQL
- **Features**:
  - Eureka client registration
  - Stock management and validation

## Redis Configuration (Order Service)

Redis is used in the Order Service for caching order data and reducing database load.

### Configuration Properties
```properties
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.timeout=60000ms
spring.data.redis.jedis.pool.max-active=8
spring.data.redis.jedis.pool.max-idle=8
spring.data.redis.jedis.pool.min-idle=0
```

### Features Implemented
1. **RedisTemplate Bean**: Custom configuration for JSON serialization
2. **Order Caching**: 
   - `@Cacheable` on `getOrderById()` - Retrieves from cache if available
   - `@CacheEvict` on `deleteOrder()` - Removes from cache on deletion
   - Manual caching in `placeOrder()` with 1-hour TTL

### Caching Strategy
- **Key Format**: `order:{orderId}`
- **Expiration**: 1 hour
- **Serialization**: JSON (Jackson2)

## Service Registration Flow

1. **Discovery Server starts first** (port 8761)
   - Initializes Eureka Server
   - Becomes available at `http://localhost:8761/eureka`

2. **Microservices register**
   - Each service connects to `http://localhost:8761/eureka`
   - Registers with unique service name
   - Provides health status

3. **API Gateway discovers services**
   - Queries Eureka for available instances
   - Updates routing tables dynamically
   - Uses Spring Cloud LoadBalancer for request distribution

## Dependencies Added

### Discovery Server
- `spring-cloud-starter-netflix-eureka-server`

### All Microservices
- `spring-cloud-starter-netflix-eureka-client`
- `spring-cloud-starter-loadbalancer`

### Order Service (Additional)
- `spring-boot-starter-data-redis`
- `jedis` (Redis Java client)

### API Gateway (Additional)
- `spring-cloud-starter-loadbalancer`

## Getting Started

### Prerequisites
- Java 21+
- Maven 3.8+
- MySQL 8.0+
- MongoDB 5.0+
- Redis 6.0+

### Starting Services

1. **Start Redis** (if not already running)
   ```bash
   redis-server
   ```

2. **Start Discovery Server**
   ```bash
   mvn -pl discovery-server spring-boot:run
   ```

3. **Start Microservices** (in any order)
   ```bash
   # Terminal 1: Product Service
   mvn -pl product-service spring-boot:run
   
   # Terminal 2: Inventory Service
   mvn -pl inventory-service spring-boot:run
   
   # Terminal 3: Order Service
   mvn -pl order-service spring-boot:run
   ```

4. **Start API Gateway**
   ```bash
   mvn -pl api-gateway spring-boot:run
   ```

### Verification

Check Eureka Dashboard: http://localhost:8761/

All services should appear in the registry with status "UP"

### Sample API Calls

```bash
# Place an order (via Gateway)
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"skuCode":"LAPTOP-001","quantity":1,"price":999.99}'

# Get order (cached from Redis)
curl http://localhost:8080/api/orders/1

# Delete order
curl -X DELETE http://localhost:8080/api/orders/1
```

## Configuration Files Updated

1. **Parent POM**: Added discovery-server module
2. **All Service POMs**: Added Eureka client dependencies
3. **Order Service POM**: Added Redis dependencies
4. **API Gateway POM**: Added LoadBalancer dependency
5. **All Application Properties**: Added Eureka configuration
6. **Application Classes**: Added `@EnableDiscoveryClient` annotation
7. **Order Service**: Added `@EnableCaching` annotation

## Benefits of This Architecture

1. **Scalability**: Services can be scaled horizontally
2. **Resilience**: Automatic service discovery and health checks
3. **Performance**: Redis caching reduces database load
4. **Flexibility**: Dynamic routing without hardcoded service URLs
5. **Maintainability**: Centralized configuration and monitoring

## Next Steps (Recommendations)

1. **Add Circuit Breaker**: Implement Resilience4j for fault tolerance
2. **Add Distributed Tracing**: Use Spring Cloud Sleuth + Zipkin
3. **Add Config Server**: Centralize configuration management
4. **Add Message Queue**: Implement Kafka/RabbitMQ for async communication
5. **Add Security**: Implement OAuth2/JWT authentication
6. **Add Monitoring**: Implement Prometheus + Grafana

## Troubleshooting

### Services not registering with Eureka
- Verify Eureka server is running on port 8761
- Check `eureka.client.service-url.defaultZone` in service properties
- Ensure network connectivity between services and Eureka

### Redis connection issues
- Verify Redis is running on localhost:6379
- Check Redis configuration in order-service properties
- Verify Jedis dependency is properly included

### Gateway routing issues
- Check service names match exactly in gateway configuration
- Verify services are registered in Eureka
- Check gateway logs for routing errors

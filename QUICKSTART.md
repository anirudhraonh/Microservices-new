# Quick Start Guide - Microservices with Eureka and Redis

## Prerequisites
- Java 21+
- Maven 3.8+
- MySQL 8.0+ (on ports 3306 and 3307)
- MongoDB 5.0+
- Redis 6.0+

## Project Structure
```
Microservices-parent/
├── discovery-server/          # Eureka Service Registry (Port 8761)
├── api-gateway/               # API Gateway (Port 8080)
├── product-service/           # Product Management (Port 8083, MongoDB)
├── order-service/             # Order Management (Port 8081, MySQL + Redis)
├── inventory-service/         # Inventory Management (Port 8082, MySQL)
├── README-MICROSERVICES.md    # Architecture documentation
└── IMPLEMENTATION-SUMMARY.md  # Implementation details
```

## Service Startup Order

### 1. Start Discovery Server
```bash
cd discovery-server
mvn spring-boot:run
# Listens on http://localhost:8761
# Dashboard: http://localhost:8761/
```

### 2. Start Microservices (in any order)
```bash
# Terminal 1: Product Service
cd product-service
mvn spring-boot:run

# Terminal 2: Inventory Service
cd inventory-service
mvn spring-boot:run

# Terminal 3: Order Service
cd order-service
mvn spring-boot:run
```

### 3. Start API Gateway
```bash
cd api-gateway
mvn spring-boot:run
# Listens on http://localhost:8080
```

## Verify Services

### Eureka Dashboard
```
http://localhost:8761/
```
Check that all services are registered with status "UP":
- DISCOVERY-SERVER
- PRODUCT-SERVICE
- INVENTORY-SERVICE
- ORDER-SERVICE
- API-GATEWAY

## API Endpoints

### Via API Gateway (Recommended)
```bash
# GET Products
curl http://localhost:8080/api/products

# POST Create Order
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"skuCode":"LAPTOP-001","quantity":1,"price":999.99}'

# GET Order (with Redis caching)
curl http://localhost:8080/api/orders/1

# DELETE Order
curl -X DELETE http://localhost:8080/api/orders/1

# GET Inventory
curl http://localhost:8080/api/inventory
```

### Direct Service Calls
```bash
# Order Service (Port 8081)
curl http://localhost:8081/api/order

# Inventory Service (Port 8082)
curl http://localhost:8082/api/inventory

# Product Service (Port 8083)
curl http://localhost:8083/api/products
```

## Redis Monitoring

### View Order Cache
```bash
redis-cli
> GET "order:1"
```

### Monitor All Operations
```bash
redis-cli MONITOR
```

## Key Features

✅ **Service Discovery**: Automatic registration and discovery via Eureka
✅ **Load Balancing**: Spring Cloud LoadBalancer distributes requests
✅ **API Gateway**: Single entry point with route management
✅ **Caching**: Redis caching for orders with automatic eviction
✅ **Microservices**: Independent services with separate databases
✅ **Inter-service Communication**: Order Service calls Inventory Service via Feign

## Troubleshooting

### Services not visible in Eureka
1. Verify Eureka server is running on port 8761
2. Check service logs for connection errors
3. Verify network connectivity

### Redis connection issues
1. Verify Redis is running: `redis-cli ping`
2. Check redis configuration in order-service/application.properties
3. Ensure port 6379 is accessible

### Gateway routing issues
1. Verify services are registered in Eureka dashboard
2. Check service names match exactly in gateway routes
3. Review gateway logs for routing errors

## Configuration Files

### Discovery Server
- `discovery-server/src/main/resources/application.properties`

### Each Service
- `{service}/src/main/resources/application.properties`
- Contains Eureka registration details
- Order Service includes Redis configuration

### API Gateway
- `api-gateway/src/main/resources/application.yml`
- Contains routing rules and Eureka configuration

## Documentation

For detailed architecture and implementation information, see:
- `README-MICROSERVICES.md` - Complete architecture overview
- `IMPLEMENTATION-SUMMARY.md` - What was implemented and how

## Next Steps

1. Run the services following the startup order above
2. Access Eureka dashboard at http://localhost:8761/
3. Test API endpoints via the gateway
4. Monitor Redis cache usage
5. Review service logs for any issues

---

**Status**: ✅ All services compiled and ready to run
**Last Updated**: October 1, 2026

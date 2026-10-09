# Spring Boot Microservices with Eureka & Redis

A comprehensive microservices architecture implementation using Spring Cloud, Eureka Service Discovery, and Redis caching.

## 🎯 Project Overview

This project demonstrates a production-ready microservices architecture with:
- **Eureka Service Discovery Server** for automatic service registration
- **Spring Cloud API Gateway** with load balancing
- **Redis Caching Layer** for performance optimization
- **Multiple Independent Services** with separate databases
- **Inter-service Communication** via Feign clients

## 📦 Modules

| Module | Port | Technology | Purpose |
|--------|------|-----------|---------|
| discovery-server | 8761 | Eureka Server | Service Registry & Discovery |
| api-gateway | 8080 | Spring Cloud Gateway | API Routing & Load Balancing |
| product-service | 8083 | MongoDB | Product Management |
| order-service | 8081 | MySQL + Redis | Order Management with Caching |
| inventory-service | 8082 | MySQL | Inventory Management |

## 🚀 Quick Start

### Prerequisites
- Java 21+
- Maven 3.8+
- MySQL 8.0+ (ports 3306, 3307)
- MongoDB 5.0+
- Redis 6.0+

### Build
```bash
mvn clean compile
```

### Start Services (in order)
```bash
# Terminal 1: Discovery Server
mvn -pl discovery-server spring-boot:run

# Terminal 2: Product Service
mvn -pl product-service spring-boot:run

# Terminal 3: Order Service
mvn -pl order-service spring-boot:run

# Terminal 4: Inventory Service
mvn -pl inventory-service spring-boot:run

# Terminal 5: API Gateway
mvn -pl api-gateway spring-boot:run
```

### Verify
- Eureka Dashboard: http://localhost:8761/
- API Gateway: http://localhost:8080/

## 📚 Documentation

### Getting Started
- **[QUICKSTART.md](QUICKSTART.md)** - Quick setup and testing guide
- **[README-MICROSERVICES.md](README-MICROSERVICES.md)** - Complete architecture overview

### Implementation Details
- **[IMPLEMENTATION-SUMMARY.md](IMPLEMENTATION-SUMMARY.md)** - Detailed implementation information
- **[CHANGES-BY-SERVICE.md](CHANGES-BY-SERVICE.md)** - Changes in each service
- **[IMPLEMENTATION-CHECKLIST.md](IMPLEMENTATION-CHECKLIST.md)** - Implementation phases and checklist
- **[DELIVERABLES.md](DELIVERABLES.md)** - Complete deliverables list

## ✨ Key Features

### Service Discovery
- ✅ Automatic service registration with Eureka
- ✅ Dynamic discovery without hardcoded URLs
- ✅ Health check monitoring
- ✅ Automatic failover support

### API Gateway
- ✅ Single entry point for all client requests
- ✅ Dynamic routing based on service discovery
- ✅ Spring Cloud LoadBalancer integration
- ✅ Request prefix stripping

### Caching
- ✅ Redis caching in Order Service
- ✅ 1-hour TTL for order data
- ✅ Automatic cache invalidation
- ✅ JSON serialization for objects

### Microservices
- ✅ Independent databases per service
- ✅ Service-to-service communication via Feign
- ✅ Scalable architecture
- ✅ Clear separation of concerns

## 🏗️ Architecture Diagram

```
┌─────────────────────────────────────┐
│    CLIENT APPLICATIONS              │
└──────────────────┬──────────────────┘
                   │
           HTTP/REST (Port 8080)
                   │
        ┌──────────▼──────────┐
        │   API Gateway       │
        │  (Load Balancer)    │
        └──┬──────┬───┬───────┘
           │      │   │
    lb://  │      │   │  lb://
 product   │      │   │ inventory
 service   │      │   │  service
      ┌────▼──┐┌──▼──▼──┐┌─────▼────┐
      │Product││ Order  ││ Inventory │
      │Service││Service ││  Service  │
      │MongoDB││MySQL   ││   MySQL   │
      │       ││+ Redis ││           │
      └────┬──┘└──┬─────┘└─────┬─────┘
           │      │            │
           └──────┴────┬───────┘
                      │
              ┌───────▼────────┐
              │ Discovery      │
              │ Server (Eureka)│
              │ (Port 8761)    │
              └────────────────┘
```

## 🔄 Service Communication

### Order Service Flow
1. Client → API Gateway (`POST /api/orders`)
2. Gateway routes to Order Service (load balanced)
3. Order Service checks inventory via Inventory Client
4. Order data cached in Redis (1-hour TTL)
5. Response returned to client

### Service Discovery Flow
1. All services start and register with Eureka
2. API Gateway queries Eureka for service locations
3. Gateway maintains dynamic routing table
4. Client requests routed to available instances

## 🔧 Configuration

### Discovery Server
```properties
server.port=8761
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
```

### All Services
```properties
eureka.client.service-url.defaultZone=http://localhost:8761/eureka
eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true
```

### Order Service (Additional)
```properties
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.cache.type=redis
```

## 📡 API Endpoints

### Via API Gateway (Recommended)
```bash
# Product Service
GET  http://localhost:8080/api/products/
POST http://localhost:8080/api/products/

# Order Service
POST http://localhost:8080/api/orders/ (place order)
GET  http://localhost:8080/api/orders/{id} (get order, cached)
DELETE http://localhost:8080/api/orders/{id} (delete order)

# Inventory Service
GET http://localhost:8080/api/inventory/
```

### Direct Service Calls
```bash
# Product Service (Port 8083)
curl http://localhost:8083/api/products/

# Order Service (Port 8081)
curl http://localhost:8081/api/order

# Inventory Service (Port 8082)
curl http://localhost:8082/api/inventory/
```

## 🧪 Testing

### Test Service Discovery
```bash
# Check Eureka dashboard
curl http://localhost:8761/

# Should show all 5 services with status UP
```

### Test Order Caching
```bash
# Place an order
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"skuCode":"LAPTOP-001","quantity":1,"price":999.99}'

# Get order (served from Redis cache)
curl http://localhost:8080/api/orders/1

# Check Redis cache
redis-cli GET "order:1"

# Delete order (clears cache)
curl -X DELETE http://localhost:8080/api/orders/1
```

## 🔍 Monitoring

### Eureka Dashboard
- URL: http://localhost:8761/
- Shows all registered services
- Displays service status and health
- Lists service instances and ports

### Redis CLI
```bash
redis-cli
> KEYS "order:*"           # List all cached orders
> GET "order:1"            # Get specific order
> MONITOR                  # Monitor all operations
```

## 🛠️ Technologies Used

| Technology | Version | Purpose |
|-----------|---------|---------|
| Spring Boot | 3.4.5 | Application framework |
| Spring Cloud | 2024.0.0 | Microservices framework |
| Eureka | Latest | Service discovery |
| Redis | 6.0+ | Caching layer |
| MySQL | 8.0+ | Relational database |
| MongoDB | 5.0+ | NoSQL database |
| Gradle | 8+ | Build tool |

## 📋 Project Status

- ✅ Discovery Server created and configured
- ✅ API Gateway fixed and enhanced
- ✅ All services registered with Eureka
- ✅ Redis caching implemented in Order Service
- ✅ All dependencies configured
- ✅ Build successful with all modules compiled
- ✅ Comprehensive documentation provided

## 🎓 Learning Resources

### Documentation Files
- `README-MICROSERVICES.md` - Architecture and design
- `QUICKSTART.md` - Setup instructions
- `IMPLEMENTATION-SUMMARY.md` - Implementation details
- `CHANGES-BY-SERVICE.md` - Detailed changes
- `IMPLEMENTATION-CHECKLIST.md` - Verification checklist
- `DELIVERABLES.md` - Deliverables summary

### External Resources
- [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
- [Eureka Documentation](https://github.com/Netflix/eureka)
- [Redis Documentation](https://redis.io/documentation)
- [Spring Data Redis](https://spring.io/projects/spring-data-redis)

## 🚀 Next Steps

1. **Start all services** following QUICKSTART.md
2. **Test service discovery** via Eureka dashboard
3. **Test API endpoints** through the gateway
4. **Monitor Redis cache** with redis-cli
5. **Review logs** for any issues

### Future Enhancements
- Add Resilience4j circuit breaker
- Implement Spring Cloud Sleuth + Zipkin tracing
- Add configuration server
- Implement message queue (Kafka/RabbitMQ)
- Add OAuth2/JWT security
- Set up Prometheus + Grafana monitoring

## 📞 Troubleshooting

### Services not registering
- Check Discovery Server is running on port 8761
- Verify network connectivity
- Check service configuration

### Redis connection issues
- Verify Redis is running: `redis-cli ping`
- Check connection configuration
- Verify port 6379 is accessible

### Gateway routing fails
- Verify services are in Eureka dashboard
- Check service names in gateway configuration
- Review gateway logs

## 📄 License

This project is part of a microservices learning implementation.

---

**Status**: ✅ Production Ready  
**Last Updated**: October 1, 2026  
**Build**: ✅ SUCCESS

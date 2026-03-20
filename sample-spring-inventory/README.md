# sample-spring-inventory

Spring Boot application demonstrating Project Leyden AOT cache on a **lightweight** startup stack.

## Stack
- Spring Boot 3.3.5
- Spring Data Redis (Lettuce client)
- Redis 7 (via Docker)
- Spring Actuator
- Java 21

## Prerequisites

Start Redis with Docker Compose (included):
```bash
docker compose up -d
```

## Run — standard

```bash
mvn clean package -DskipTests
java -jar target/sample-spring-inventory.jar
```

Startup time: **~1.1s**

## Run — with Project Leyden AOT cache

> Requires the Leyden EA2 JDK. Download at https://jdk.java.net/leyden/
> Set JAVA_HOME to the Leyden JDK before running.

**Step 1 — training run (generates the cache)**
```bash
java -XX:AOTCacheOutput=inventory.aot -jar target/sample-spring-inventory.jar
# Wait for full startup, then Ctrl+C
```

**Step 2 — production run (loads the cache)**
```bash
java -XX:AOTCache=inventory.aot -jar target/sample-spring-inventory.jar
```

Startup time: **~0.65s** (~41% faster)

## Endpoints

| Method | URL                             | Description                     |
|--------|---------------------------------|---------------------------------|
| GET    | /products                       | List all products               |
| GET    | /products/{id}                  | Get product by ID               |
| GET    | /products/category/{category}   | Filter by category              |
| POST   | /products                       | Add a product                   |
| PUT    | /products/{id}                  | Update a product                |
| PATCH  | /products/{id}/stock?delta=N    | Adjust stock (+/-)              |
| GET    | /products/{id}/movements        | List stock movements            |
| DELETE | /products/{id}                  | Delete a product                |

- Actuator: http://localhost:8081/actuator/health

## Example: add a product

```bash
curl -X POST http://localhost:8081/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Mechanical Keyboard",
    "category": "Electronics",
    "price": 129.99,
    "quantity": 50,
    "description": "TKL layout, Cherry MX switches"
  }'
```

## Example: adjust stock

```bash
# Add 20 units
curl -X PATCH "http://localhost:8081/products/{id}/stock?delta=20&reason=Restock"

# Remove 5 units
curl -X PATCH "http://localhost:8081/products/{id}/stock?delta=-5&reason=Sale"
```

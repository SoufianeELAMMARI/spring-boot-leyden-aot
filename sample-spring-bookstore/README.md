# sample-spring-bookstore

Spring Boot application demonstrating Project Leyden AOT cache on a **heavy** startup stack.

## Stack
- Spring Boot 3.3.5
- Spring Data JPA + Hibernate
- H2 in-memory database
- Liquibase (schema migration + seed data)
- Springdoc OpenAPI 2.6.0 (Swagger UI)
- Spring Actuator
- Java 21

## Run — standard

```bash
mvn clean package -DskipTests
java -jar target/sample-spring-bookstore.jar
```

Startup time: **~3s**

## Run — with Project Leyden AOT cache

> Requires the Leyden EA2 JDK. Download at https://jdk.java.net/leyden/
> Set JAVA_HOME to the Leyden JDK before running.

**Step 1 — training run (generates the cache)**
```bash
java -XX:AOTCacheOutput=bookstore.aot -jar target/sample-spring-bookstore.jar
# Wait for full startup, then Ctrl+C
```

**Step 2 — production run (loads the cache)**
```bash
java -XX:AOTCache=bookstore.aot -jar target/sample-spring-bookstore.jar
```

Startup time: **~2s** (~33% faster)

## Endpoints

| Method | URL                        | Description           |
|--------|----------------------------|-----------------------|
| GET    | /books                     | List all books        |
| GET    | /books/{id}                | Get book by ID        |
| GET    | /books/isbn/{isbn}         | Get book by ISBN      |
| GET    | /books/genre/{genre}       | Filter by genre       |
| GET    | /books/in-stock            | Books with stock > 0  |
| POST   | /books                     | Add a book            |
| PUT    | /books/{id}                | Update a book         |
| DELETE | /books/{id}                | Delete a book         |
| GET    | /orders                    | List all orders       |
| GET    | /orders/{id}               | Get order by ID       |
| POST   | /orders                    | Place an order        |
| PATCH  | /orders/{id}/status        | Update order status   |
| DELETE | /orders/{id}               | Cancel an order       |

- Swagger UI: http://localhost:8080/swagger-ui.html
- H2 Console:  http://localhost:8080/h2-console
- Actuator:    http://localhost:8080/actuator/health

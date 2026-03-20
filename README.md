# Project Leyden

Faster Spring Boot startup with the OpenJDK AOT cache — no native compilation, no GraalVM, no code changes.

---

## About

This repository accompanies the article *Project Leyden: Supercharging Spring Boot with Redis for Ultra-Fast Performance* and contains two Spring Boot sample applications built to benchmark Project Leyden's AOT cache feature.

Project Leyden is an OpenJDK initiative that dramatically reduces Java startup time by persisting JIT-compiled code and class metadata into a reusable cache file — no native compilation, no GraalVM, no code changes.

```
Standard JVM   ████████████████████████  5.3s
With AOTCache  ███████████████           3.5s   → -33%  (bookstore)

Standard JVM   ████████████             1.1s
With AOTCache  ███████                  0.65s  → -41%  (redis)
```

---

## Repository Structure

```
sample-spring-leyden/
│
├── sample-spring-bookstore/        ← Heavy app (App 1)
│   ├── src/main/java/
│   │   └── leyden/samples/bookstore/
│   │       ├── controller/            # BookController, OrderController
│   │       ├── service/               # BookService, OrderService
│   │       ├── repository/            # Spring Data JPA
│   │       └── model/                 # Book, Order entities
│   ├── src/main/resources/
│   │   ├── script/              # create.sql, data.sql
│   │   └── application.yml
│   └── pom.xml
│
├── sample-spring-inventory/        ← Light app (App 2)
│   ├── src/main/java/
│   │   └── leyden/samples/inventory/
│   │       ├── controller/            # ProductController
│   │       ├── service/               # ProductService
│   │       ├── repository/            # Spring Data Redis
│   │       ├── model/                 # Product, StockMovement
│   │       └── config/                # RedisConfig
│   ├── src/main/resources/
│   │   └── application.yml
│   ├── docker-compose.yml             # Redis 7 via Docker
│   └── pom.xml
│
└── README.md
```

---

## Tech Stack

| Project | Stack                                  | Port | Purpose |
|---------|----------------------------------------|------|---------|
| sample-spring-bookstore | Spring Boot · Postgres · JPA · OpenAPI | 8080 | Heavy startup (ORM + migrations + Swagger) |
| sample-spring-inventory | Spring Boot · Redis · Actuator         | 8081 | Lean startup (pure JVM overhead) |

---

## Quick Start

### Prerequisites

| Tool | Version | Notes |
|------|---------|-------|
| Leyden JDK | EA2 (26-leydenpremain) | [Download here](https://jdk.java.net/leyden) — do not replace your system JDK |
| Maven | 3.9+ | |
| Docker | Latest | Required for App 2 (Redis) |

### Step 1 — Install the Leyden JDK

```bash
# macOS ARM
curl -O https://download.java.net/java/early_access/leyden/1/openjdk-26-leydenpremain+1_macos-aarch64_bin.tar.gz
tar -xzf openjdk-26-leydenpremain+1_macos-aarch64_bin.tar.gz
sudo mv jdk-26-leydenpremain.jdk /opt/leyden-jdk

# Linux x64
curl -O https://download.java.net/java/early_access/leyden/1/openjdk-26-leydenpremain+1_linux-x64_bin.tar.gz
tar -xzf openjdk-26-leydenpremain+1_linux-x64_bin.tar.gz
sudo mv jdk-26-leydenpremain /opt/leyden-jdk
```

> For other OS versions, visit [jdk.java.net/leyden](https://jdk.java.net/leyden)

### Step 2 — Point JAVA_HOME to Leyden (session only)

```bash
export JAVA_HOME=/opt/leyden-jdk/Contents/Home   # macOS
# or
export JAVA_HOME=/opt/leyden-jdk                  # Linux

export PATH=$JAVA_HOME/bin:$PATH

# Verify
java --version
# openjdk 26-leydenpremain 2026-03-17
```

> **Note:** Keep this terminal open for all the following commands. New tabs won't have this set.

---

## App 1 — Bookstore (Heavy Stack)

Stack: Postgres DB  · Hibernate/JPA · Springdoc OpenAPI · Actuator

```bash
cd sample-spring-bookstore
mvn clean package -DskipTests
```

### Measure baseline

```bash
java -jar target/sample-spring-bookstore.jar
# Started BookstoreApplication in ~5.3 seconds
```

### Training run — generate the AOT cache

```bash
java -XX:AOTCacheOutput=bookstore.aot \
     -jar target/sample-spring-bookstore.jar
# Let it fully start, then Ctrl+C
# AOT cache written: bookstore.aot
```

### Production run — load the cache

```bash
java -XX:AOTCache=bookstore.aot \
     -jar target/sample-spring-bookstore.jar
# Started BookstoreApplication in ~3.5 seconds  (-33%)
```

### Results

| Mode | Time | Delta |
|------|------|-------|
| Standard JVM | ~5.3 s | — |
| With AOT cache | ~3.5 s | -33% |

### Endpoints

| Method | URL | Description |
|--------|-----|-------------|
| GET | /books | List all books |
| GET | /books/{id} | Get book by ID |
| GET | /books/genre/{genre} | Filter by genre |
| GET | /books/in-stock | Books with stock > 0 |
| POST | /books | Add a book |
| PUT | /books/{id} | Update a book |
| DELETE | /books/{id} | Delete a book |
| GET | /orders | List all orders |
| POST | /orders | Place an order |
| PATCH | /orders/{id}/status | Update order status |

- Swagger UI: http://localhost:8080/swagger-ui.html
- Actuator: http://localhost:8080/actuator/health

---

## App 2 — Inventory (Lean Stack)

Stack: Spring Data Redis · Lettuce client · Actuator

No migrations, no ORM — pure JVM startup overhead. Leyden shines here.

```bash
# Start Redis
cd sample-spring-inventory
docker compose up -d

# Build
mvn clean package -DskipTests
```

### Measure baseline

```bash
java -jar target/sample-spring-inventory.jar
# Started InventoryApplication in ~1.1 seconds
```

### Training run

```bash
java -XX:AOTCacheOutput=inventory.aot \
     -jar target/sample-spring-inventory.jar
# Let it fully start, then Ctrl+C
# AOT cache written: inventory.aot
```

### Production run

```bash
java -XX:AOTCache=inventory.aot \
     -jar target/sample-spring-inventory.jar
# Started InventoryApplication in ~0.65 seconds  (-41%)
```

### Results

| Mode | Time | Delta |
|------|------|-------|
| Standard JVM | 1.10 s | — |
| With AOT cache | 0.65 s | -41% |

### Endpoints

| Method | URL | Description |
|--------|-----|-------------|
| GET | /products | List all products |
| GET | /products/{id} | Get product by ID |
| GET | /products/category/{category} | Filter by category |
| POST | /products | Add a product |
| PUT | /products/{id} | Update a product |
| PATCH | /products/{id}/stock?delta=N | Adjust stock (+/-) |
| GET | /products/{id}/movements | Stock movement history |
| DELETE | /products/{id} | Delete a product |

- Actuator: http://localhost:8081/actuator/health

### Example — add a product

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

---

## Full Benchmark Summary

| Project | Stack                             | Baseline | With Leyden | Gain |
|---------|-----------------------------------|----------|-------------|------|
| sample-spring-bookstore | Postgres + JPA + OpenAPI | ~5.3 s | ~3.5 s | -33% |
| sample-spring-inventory | Redis only                        | 1.10 s | 0.65 s | -41% |

The lighter the stack, the higher the gain — because Leyden eliminates pure JVM overhead. Apps with heavy initialization work (migrations, ORM) show proportionally smaller gains since that work must always run.

---

## The Leyden Workflow — Cheatsheet

```bash
# 1. Switch to Leyden JDK
export JAVA_HOME=/opt/leyden-jdk/Contents/Home && export PATH=$JAVA_HOME/bin:$PATH

# 2. Build your app
mvn clean package -DskipTests

# 3. Training run — generates the cache
java -XX:AOTCacheOutput=myapp.aot -jar target/myapp.jar
# Wait for "Started ... in X seconds", then Ctrl+C

# 4. Production run — loads the cache
java -XX:AOTCache=myapp.aot -jar target/myapp.jar

# 5. After any rebuild — regenerate cache
mvn clean package -DskipTests
java -XX:AOTCacheOutput=myapp.aot -jar target/myapp.jar  # re-train
```

> **Warning:** The `.aot` file is tied to a specific JAR + JDK combination. Any rebuild invalidates it.

---

## Leyden vs the Alternatives

| | GraalVM Native | CRaC | Project Leyden |
|-|---------------|------|----------------|
| Startup speed | ~50ms | ~100ms | ~650ms |
| Code changes required | Major | Minor | None |
| Build complexity | High | Medium | Minimal |
| Standard JDK | No | No | Soon |
| Production ready | Yes | Yes | Experimental |

---

## Known Limitations

- Cache is not portable — tied to exact JDK + JAR + config. Rebuild = regenerate cache.
- EA JDK only — flags changed between EA1 and EA2 (`-XX:+AOTClassLinking` removed in EA2).
- Multi-profile apps — cache generated in one Spring profile may not be optimal in another.
- Not for production — this is an early-access experiment, not a stable release.
- Wrong JDK = instant crash — using `-XX:AOTCache` on a non-Leyden JVM throws `Unrecognized VM option`.

---

## Further Reading

- [Project Leyden — OpenJDK](https://openjdk.org/projects/leyden/)
- [Leyden EA2 Release Notes](https://jdk.java.net/leyden)
- [Speed up Java Startup with Spring Boot and Project Leyden — Piotr Minkowski](https://piotrminkowski.com/2025/05/07/speed-up-java-startup-with-spring-boot-and-project-leyden/)
- [Speed up Java Startup on Kubernetes with CRaC](https://spring.io/blog/2025/01/23/spring-boot-crac)
- [Spring Boot AOT Documentation](https://docs.spring.io/spring-boot/reference/packaging/aot.html)

---

## License

This project is licensed under the MIT License — see the LICENSE file for details.

---

*Made with curiosity about the JVM*
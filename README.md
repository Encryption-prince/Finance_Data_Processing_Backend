# Finance Dashboard API

A secure, role-based REST API for managing organizational financial records. Built with Spring Boot 4, PostgreSQL and JWT authentication.

---

## Table of Contents

- [Tech Stack](#tech-stack)
- [Roles & Permissions](#roles--permissions)
- [Advanced Features](#advanced-features)
- [Swagger UI](#swagger-ui)

---

## Tech Stack

| Layer        | Technology                          |
|--------------|-------------------------------------|
| Framework    | Spring Boot 4.0.5                   |
| Language     | Java 21                             |
| Database     | PostgreSQL                          |
| ORM          | Spring Data JPA / Hibernate         |
| Security     | Spring Security + JWT (JJWT 0.11.5) |
| Rate Limiting | Bucket4j 8.10.1                    |
| API Docs     | SpringDoc OpenAPI 3 (Swagger UI)    |
| Boilerplate  | Lombok                              |

---


## Roles & Permissions

Three roles control access across the API:

| Role    | Description                                      |
|---------|--------------------------------------------------|
| VIEWER  | Read-only access to financial records            |
| ANALYST | Read access + search + dashboard summary         |
| ADMIN   | Full access including write, delete, user mgmt   |

### Permission Matrix

| Endpoint                        | VIEWER | ANALYST | ADMIN |
|---------------------------------|--------|---------|-------|
| GET /api/records                | ✅     | ✅      | ✅    |
| GET /api/records/{id}           | ✅     | ✅      | ✅    |
| POST /api/records               | ❌     | ❌      | ✅    |
| PUT /api/records/{id}           | ❌     | ❌      | ✅    |
| DELETE /api/records/{id}        | ❌     | ❌      | ✅    |
| GET /api/records/search         | ❌     | ✅      | ✅    |
| GET /api/dashboard/summary      | ❌     | ✅      | ✅    |
| GET /api/users                  | ❌     | ❌      | ✅    |
| POST /api/users                 | ❌     | ❌      | ✅    |
| PUT /api/users/{id}/status      | ❌     | ❌      | ✅    |
| PUT /api/users/{id}/role        | ❌     | ❌      | ✅    |

Permissions are enforced via Spring Security's `@PreAuthorize` annotations at the method level.

---

## Advanced Features

### Soft Deletion

Financial records are never permanently removed from the database. When an admin calls `DELETE /api/records/{id}`, the record's `deleted` flag is set to `true` instead of issuing a SQL `DELETE`.

This is implemented using two Hibernate annotations on the `FinancialRecord` entity:

```java
@SQLDelete(sql = "UPDATE financial_records SET deleted = true WHERE id=?")
@SQLRestriction("deleted = false")
```

This preserves historical data for auditing while keeping the API behavior clean.

---

### Token-Bucket Rate Limiting

All API requests (except Swagger UI docs) are rate-limited using the **Bucket4j** library, which implements the token-bucket algorithm.

Configuration in `RateLimitFilter.java`:

```java
Bandwidth limit = Bandwidth.classic(20, Refill.greedy(20, Duration.ofMinutes(1)));
this.bucket = Bucket.builder().addLimit(limit).build();
```

- The bucket holds a maximum of **20 tokens**.
- Tokens refill at a rate of **20 per minute** (greedy refill — all at once).
- Each incoming request consumes 1 token.
- When the bucket is empty, the server responds with `429 Too Many Requests`.

The filter runs before the security chain via `OncePerRequestFilter`, ensuring rate limiting applies globally regardless of authentication status.

---

## Swagger UI

Interactive API documentation is available at:

```
http://localhost:8080/swagger-ui/index.html
```

---

## Thanks for Reading it through...❤️

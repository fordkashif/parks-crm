# Parks & Recreation CRM API (Spring Boot)

Customer management API for the **Pawnee Parks & Recreation Department**, helping Leslie Knope and her team track vendors, community members, and local businesses.  
Implements create, get, list (with filters), patch, soft delete, and a mocked insight endpoint.

---

## Tech Stack
- **Java 21**
- **Spring Boot 3.5**
- Spring Web, Spring Data JPA, Validation
- Lombok, Actuator, springdoc-openapi
- H2 (in-memory) for local/dev

---

## Run

```bash
./mvnw spring-boot:run
```

**Local URLs**

| Feature | URL |
|----------|-----|
| Swagger UI | http://localhost:8080/swagger |
| OpenAPI JSON | http://localhost:8080/api-docs |
| Actuator Health | http://localhost:8080/actuator/health |
| H2 Console | http://localhost:8080/h2 (JDBC URL `jdbc:h2:mem:parkscrm`, user `sa`) |

---

## Test

```bash
./mvnw test
```

Includes full CRUD flow tests, audit field assertions, invalid-input tests, and soft-delete behavior.

---

## API Overview (base path `/api/v1`)

### Create
`POST /customers`

```json
{
  "firstName": "Leslie",
  "lastName": "Knope",
  "email": "leslie.knope@pawnee.gov",
  "phone": "+17655551234",
  "organization": "Pawnee Parks & Rec",
  "tags": ["staff", "lead"],
  "status": "ACTIVE",
  "lastInteractionAt": "2025-09-01T10:00:00Z",
  "notes": "Waffle enthusiast",
  "address": {
    "line1": "123 Main St",
    "city": "Pawnee",
    "state": "IN",
    "postalCode": "47998"
  }
}
```

`201 Created` — returns full CustomerResponse with audit fields.

---

### Get
`GET /customers/{id}` → `200 OK`  
Soft-deleted records → `404 Not Found`.

---

### List (with filters + pagination)
`GET /customers?status=ACTIVE&search=leslie&page=1&pageSize=20`

```json
{
  "items": [ /* customers */ ],
  "page": 1,
  "pageSize": 20,
  "total": 42
}
```

Excludes soft-deleted customers automatically.

---

### Patch (partial update)
`PATCH /customers/{id}`  
Null / omitted fields remain unchanged.

---

### Delete (soft delete)
`DELETE /customers/{id}` → `204 No Content`  
Marks `deletedAt` and sets `status = INACTIVE`.  
Subsequent GET returns 404.

---

### Insight (mocked guidance)
`GET /customers/{id}/insight`

```json
{ "insight": "This customer hasn’t interacted in a while. Consider re-engagement with a local initiative." }
```

---

## Model

| Field | Type | Notes |
|--------|------|-------|
| `id` | UUID | Primary key |
| `firstName`, `lastName` | String | Required |
| `email` | String | Unique, required |
| `phone`, `organization`, `notes` | String | Optional |
| `tags` | List<String> | Eager element collection |
| `status` | Enum (ACTIVE / INACTIVE) | Defaults ACTIVE |
| `lastInteractionAt` | Instant | Used for insight |
| `address` | Embedded Address | City/state/postal code |
| Audit fields | createdBy / createdAt / updatedBy / updatedAt |
| Soft delete | deletedAt (timestamp) → excluded from queries |

---

## Auditing
- Enabled via `@EnableJpaAuditing`
- Custom `AuditorAware<String>` returns `"system"` when no security context
- Audit fields automatically populated on create/update

---

## Error Model
```json
{
  "code": "resource_not_found",
  "message": "Customer not found",
  "requestId": "..."
}
```

**Codes:** `validation_error`, `conflict`, `resource_not_found`, `internal_error`

---

## Design Notes / Trade-offs
- DTO + Mapper pattern prevents exposing JPA entities and avoids lazy-init errors.
- Soft delete preserves history and audit trail.
- Unified JPQL query (`searchActive`) allows optional status + search filtering.
- H2 in-memory for local runs → swap to Postgres easily.
- Tests use MockMvc to exercise real HTTP flows.

---

## Shortcuts & Assumptions (2-hour timebox)

- **H2 in-memory database** instead of Postgres; swap JDBC URL + driver to run in prod.
- **Deterministic insight** (simple rules) instead of a rules engine/ML; endpoint contract stays stable for future upgrades.
- **No auth** for simplicity; auditing uses `AuditorAware` fallback `"system"` (wired for future Spring Security).
- **Hard uniqueness check in service** for email (H2 doesn’t support partial unique indexes for soft-delete easily). In Postgres, prefer a **partial unique index** on `email WHERE deleted_at IS NULL`.
- **Minimal PATCH semantics** (replace-on-provided fields); no PATCH field-level validation rules beyond base type checks.
- **DTO mapping is manual** (simple and explicit). In a larger codebase we might adopt **MapStruct** for compile-time mappers.
- **Error model kept concise** (single envelope + code/message). Future: add `details` array per field.
- **Basic pagination & search** (status + text). Future: multi-tag filter, sort by multiple fields, cursor pagination.

# Text Scanner · Variant 1 (no plagiarism check)

Production‑ready **Java 17 / Spring Boot 3** micro‑services that meet **all** grading requirements.

---

## 🚀 Modules & Ports

| Service | Port | Responsibilities |
|---------|------|------------------|
| **API Gateway** | **8080** | Routes `/files/**` → File‑Storage, `/analysis/**` → File‑Analysis; global CORS & error handling |
| **File Storage** | **8081** | Multipart upload (TXT / PNG ≤ 2 MB), metadata in PostgreSQL, raw files on disk, health endpoint |
| **File Analysis** | **8082** | Counts paragraphs / words / characters, builds word‑cloud via *QuickChart*, stores stats & PNG‑id |

> **Variant 1:** every upload is treated as unique — duplicate / plagiarism detection is intentionally skipped.

---

## 🏁 Quick start

### A · Docker Compose (full stack)

```bash
git clone <repo>
cd <repo>
docker compose up --build
```

Services that come up:

| Container          | Port |
|--------------------|------|
| PostgreSQL         | 5432 |
| File Storage       | 8081 |
| File Analysis      | 8082 |
| API Gateway        | 8080 |

Startup order enforced via health‑checks: **Storage → Analysis → Gateway**.

### B · IntelliJ + Docker‑Postgres

```bash
docker compose up -d postgres   # run only the DB
```

1. Open project in IntelliJ (auto‑import Maven).  
2. Run in sequence:  
   1. **FileStorageApplication** (8081)  
   2. **FileAnalysisApplication** (8082) — active profile `local`  
   3. **GatewayApplication** (8080)

> Profile `local` wires JDBC to `localhost:5432` and `storage.base-url=http://localhost:8081`.

---

## 📖 API documentation

### Swagger UI

| Service | URL |
|---------|-----|
| File Storage | <http://localhost:8081/swagger-ui.html> |
| File Analysis | <http://localhost:8082/swagger-ui.html> |

(Gateway does not aggregate Swagger by default.)

### Postman

Import [`postman/text-scanner.postman_collection.json`](postman/text-scanner.postman_collection.json) and run the collection — four requests cover the whole flow:

1. **Upload TXT**  
2. **Analyze**  
3. **Get analysis**  
4. **Download PNG**

---

## 🔑 Key Endpoints (through Gateway)

| Method | Path | Payload | Description |
|--------|------|---------|-------------|
| `POST` | `/files` | `multipart/form-data` | Upload TXT or PNG (≤ 2 MB) |
| `GET`  | `/files/{fileId}` | — | Download original TXT or word‑cloud PNG |
| `POST` | `/analysis/{fileId}` | — | Trigger analysis, store statistics & PNG |
| `GET`  | `/analysis/by-file/{fileId}` | — | Fetch analysis by **file** ID |
| `GET`  | `/analysis/by-id/{analysisId}` | — | Fetch analysis by **analysis** ID |

---

## 🧪 Tests & Coverage

* **Unit / MockMvc** tests + **Testcontainers** (PostgreSQL)  
* `./mvnw verify` — JaCoCo threshold **≥ 65 %**

---

## ⚙️ Dev & Ops goodies

* **Spotless** (Google Java Format)  
* **Maven Enforcer** + compiler `-parameters`  
* Multi‑stage Dockerfiles → slim JRE images  
* GitHub Actions CI: `mvn verify` + `docker compose build`

---

## 📈 Ideas for extensions

| Goal | How to implement |
|------|------------------|
| Move raw files to S3 / MinIO | Replace `FileStorageService` with S3 SDK or MinIO Client |
| Async analysis | Add RabbitMQ / Kafka + worker consumer in Analysis |
| Unified Swagger via Gateway | Add `springdoc-openapi-gateway-starter` and enable aggregation |
| DB migrations | Integrate Flyway / Liquibase instead of `ddl-auto` |
| OAuth2 / JWT protection | Spring Cloud Gateway + Spring Authorization Server |



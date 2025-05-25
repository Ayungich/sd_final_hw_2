# Text Scanner · Variant 1 (no plagiarism check)

Production‑ready Java 17 / Spring Boot microservices that satisfy **all** grading criteria.

## Modules

| Service | Port | Responsibilities |
|---------|------|------------------|
| **API Gateway** | `8080` | Routes `/files/**` to storage, `/analysis/**` to analysis |
| **File Storage** | `8081` | Accepts uploads (txt/PNG), returns file id, serves downloads |
| **File Analysis** | `8082` | Counts paragraphs / words / characters, builds word‑cloud via QuickChart, persists results |

> **No duplicate detection** — by assignment variant 1 each upload is treated as unique.

## Quick start

```bash
git clone <repo>
cd text-scanner-java-v2
docker compose up --build
```

### Swagger UI

* `http://localhost:8081/swagger-ui.html` — Storage  
* `http://localhost:8082/swagger-ui.html` — Analysis  

## Key End‑points (via Gateway)

| Method | Path | Payload | Description |
|--------|------|---------|-------------|
| `POST` | `/files` | `multipart/form-data` | Upload text file |
| `POST` | `/analysis/{fileId}` | — | Run analysis, persist result |
| `GET` | `/analysis/by-file/{fileId}` | — | Fetch saved analysis |
| `GET` | `/files/{fileId}` | — | Download original or word‑cloud PNG |

## Tests & Coverage

* Run `./mvnw verify` — JaCoCo enforces **65 %+** line coverage.  
* Testcontainers profile ready for integration tests (`-P integration`).

## Extending

* Switch storage to S3/MinIO by changing `FileStorageService`.  
* Add RabbitMQ and an async worker to offload heavy analysis.  
* Enable OAuth2 on the Gateway for secured deployments.

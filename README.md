# Spring Monorepo (Gateway + API CQRS + Database + Batch + Messaging + Config Server)

Includes:
- Dapr components: Kafka pub/sub + Redis state store
- Spring Cloud Gateway routes using Dapr service invocation
- CQRS bus w/ pipeline behaviors
- Event-sourcing primitives + snapshot primitives + multitenant primitives
- OpenAPI/Swagger via springdoc + Gradle generate task

## OpenAPI / Swagger (API service)
When API is running:
- Swagger UI: `/swagger-ui.html`
- OpenAPI JSON: `/v3/api-docs`
- OpenAPI YAML: `/v3/api-docs.yaml`

Build-time generation:
- `make openapi` -> `services/api/build/openapi/openapi.json`

## Docker
Start everything:
- `make compose-up`
Stop:
- `make compose-down`

If your Docker Compose doesn't support `include:`, run with multiple files:
`docker compose -f docker-compose.infra.yml -f services/gateway/docker-compose.yml -f services/api/docker-compose.yml -f services/database/docker-compose.yml -f services/batch/docker-compose.yml -f services/messaging/docker-compose.yml up -d --build`

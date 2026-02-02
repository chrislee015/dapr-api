.DEFAULT_GOAL := help
GRADLE ?= ./gradlew

help:
	@echo "Monorepo commands:"
	@echo "  make build                 Build everything"
	@echo "  make test                  Run tests"
	@echo "  make clean                 Clean all"
	@echo "  make run-api               Run API service"
	@echo "  make run-gateway           Run Gateway"
	@echo "  make run-database          Run Database service"
	@echo "  make run-batch             Run Batch service"
	@echo "  make run-messaging         Run Messaging service"
	@echo "  make openapi               Generate OpenAPI JSON (API)"
	@echo "  make compose-up            Docker compose up (all)"
	@echo "  make compose-down          Docker compose down (all)"
	@echo "  make compose-logs          Tail logs"
	@echo "  make compose-ps            Show containers"

build:
	$(GRADLE) build

test:
	$(GRADLE) test

clean:
	$(GRADLE) clean

run-api:
	$(GRADLE) :services:api:bootRun

run-gateway:
	$(GRADLE) :services:gateway:bootRun

run-database:
	$(GRADLE) :services:database:bootRun

run-batch:
	$(GRADLE) :services:batch:bootRun

run-messaging:
	$(GRADLE) :services:messaging:bootRun

openapi:
	$(GRADLE) :services:api:generateOpenApiDocs

compose-up:
	docker compose up -d --build

compose-down:
	docker compose down -v

compose-logs:
	docker compose logs -f --tail=200

compose-ps:
	docker compose ps

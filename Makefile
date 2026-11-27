DC ?= docker compose
APP := ticketing-app
export HOST_UID ?= $(shell id -u)
MVN := mvn -B -Duser.home=/var/maven

.PHONY: help init up up-build down reset restart test logs shell db-shell image

help:
	@echo "make init      - Build va khoi dong lan dau"
	@echo "make up        - Khoi dong"
	@echo "make up-build  - Khoi dong va build lai image"
	@echo "make restart   - Khoi dong lai app sau khi sua code"
	@echo "make down      - Dung"
	@echo "make reset     - Dung va XOA toan bo du lieu"
	@echo "make test      - Chay test tren database ticketing_test"
	@echo "make logs      - Xem log app"
	@echo "make shell     - Mo shell trong container app"
	@echo "make db-shell  - Mo psql"
	@echo "make image     - Build image runtime"

init: up-build

up:
	$(DC) up -d

up-build:
	$(DC) up --build -d

down:
	$(DC) down

reset:
	$(DC) down -v

restart:
	$(DC) restart $(APP)

# Container va target rieng de khong ghi de class cua app dang chay
test:
	$(DC) run --rm -T -v /workspace/target $(APP) $(MVN) test

logs:
	$(DC) logs -f $(APP)

shell:
	$(DC) exec $(APP) bash

db-shell:
	$(DC) exec ticketing-postgres psql -U ticketing -d ticketing

image:
	docker build -f docker/Dockerfile --target runtime -t ticketing:local .

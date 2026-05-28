# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Context

Personal study project — a REST API for managing wallets, cards, and invoices with installment payment support. Interactions should focus on discussing solutions and helping the user understand and build; code suggestions should be provided for the user to review and implement as needed.

## Commands

```bash
# Run the application
./mvnw spring-boot:run

# Build
./mvnw clean package

# Run tests
./mvnw test

# Run a single test
./mvnw test -Dtest=ClassName#methodName
```

## Infrastructure

Start local dependencies before running the application:

```bash
cd infra && docker compose up -d
```

This starts:
- MySQL 8.0 on port 3306 (`invoice_db`, user: `invoice`, password: `invoice`)
- LocalStack on port 4566 (SNS/SQS simulation)

Schema is at `infra/database.sql`.

## Architecture

**Domain:** Wallets contain Cards; Cards have CardEntries (transactions); CardEntries are split into Installments and grouped into Invoices.

**Layers:**
- `controllers/` → REST endpoints; all require `SELECTED-USER` header
- `services/` → business logic
- `repositories/` → Spring Data JPA repositories
- `entities/` → JPA entities
- `dtos/requests/`, `dtos/responses/`, `dtos/messages/` → input/output/messaging
- `connectors/` → AWS SNS integration (`SnsConnector`)
- `commons/` → `DateHandler` (timezone-aware, uses America/Recife), `InstallmentHandler`

**Message flow:** `CardEntryService` publishes to SNS after DB commit (`@TransactionalEventListener(AFTER_COMMIT)`). `InvoiceCardEntryConsumerApplication` is a separate Spring entry point that listens to the SQS queue `invoice-card_entry-consumer` and processes concluded card entries.

**Invoice due date strategies** (configured per wallet via `InvoiceConfiguration`):
- `FIXED_DAY` — fixed day of month with optional month offset
- `RULE` — N days after closing date

Minimum 3-day gap between closing and due date is enforced.

**Pessimistic locking:** `WalletRepository.findByWalletKeyForUpdate()` is used when updating wallet limits during card entry processing to avoid concurrent modifications.

**Installment rounding:** `InstallmentHandler` uses `RoundingMode.DOWN`; the remainder goes to the first installment.

## Logging

Use SLF4J (`LoggerFactory.getLogger(ClassName.class)`) in all classes that need logging.

Log message format: `ClassName.methodName - <description>`. Include relevant identifiers (keys, statuses) as SLF4J placeholders `{}`.

Rules:
- **Public methods:** log at the start and at the end (before return)
- **Private methods:** log at the start only
- **Every flow change** (if/else/while branches): add a `log.info` inside each branch signaling which path was taken
- **Mapped exceptions (catch):** use `log.warn` with the exception message
- **Unexpected exceptions (catch):** use `log.error("...", e)` to include the full traceback before rethrowing

## Conventions (from README)

**Branch naming:** `type/description` (e.g., `feat/wallet-creation`)

**Commit messages:** `type(scope): description` (e.g., `feat(wallet): add creation endpoint`)

Types: `feat`, `fix`, `refactor`, `chore`, `docs`, `test`

# Spring Invoice Service

REST API for managing wallets, cards, and invoices with installment support.

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.9+
- MySQL 8+

### Running locally

```bash
./mvnw spring-boot:run
```

---

## Branch Naming

```
<type>/<short-description>
```

| Type | When to use |
|------|-------------|
| `feat` | New feature |
| `fix` | Bug fix |
| `refactor` | Code change that is not a fix or feature |
| `chore` | Build, config, tooling, dependencies |
| `docs` | Documentation only |

**Examples**

```
feat/card-entry-processing
fix/invoice-due-date-overflow
refactor/installment-handler
chore/update-dependencies
```

---

## Commit Messages

```
<type>(<scope>): <short description>
```

- **type** — same types as branch naming
- **scope** — affected module or layer (optional but recommended): `wallet`, `card`, `invoice`, `card-entry`, `infra`, etc.
- **description** — imperative, lowercase, no period at the end

**Examples**

```
feat(card-entry): add installment amount calculation
fix(invoice): correct due date month overflow for fixed day type
refactor(card-entry): replace manual month arithmetic with YearMonth
chore(infra): add card_entry_data json column to database schema
docs: add README with branch and commit conventions
```

### Rules

- Keep the subject line under 72 characters
- Use the body to explain **why**, not what (when needed)
- One logical change per commit

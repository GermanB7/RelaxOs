# Sprint 1 Profile, Scenarios, Expenses

## Scope

- Local single-user profile through `/api/v1/me/profile`.
- Scenario create, list, read, update, and duplicate flows.
- Scenario expenses create, list, update, and delete flows.
- Expense category seed data through Flyway.
- Backend monthly summary using simple frequency normalization.
- Frontend forms connected to real APIs with TanStack Query, React Hook Form, and Zod.

## Monthly Expense Normalization

- `MONTHLY`: amount
- `WEEKLY`: amount * 4
- `YEARLY`: amount / 12
- `ONE_TIME`: 0

This is not an independence score, risk engine, or recommendation engine.

## Non-Goals

- No score engine.
- No risk engine.
- No recommendation engine.
- No AI.
- No advanced authentication.
- No charts or advanced scenario comparison.

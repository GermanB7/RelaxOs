# TranquiloOS Portfolio Case Study

## 1. Project Overview

TranquiloOS is a personal independence system for people planning independent living.

The MVP helps a user model move-out scenarios, calculate readiness, detect risks, receive recommendations, plan home setup purchases, activate operating modes, evaluate transport choices, and solve low-effort meal decisions.

The product is intentionally decision-first. It does not try to become a bank clone, generic budget tracker, or lifestyle dashboard.

## 2. Problem Statement

Moving out is a multi-variable decision:

- Rent can look affordable while emergency coverage is too low.
- Transport can quietly destroy monthly margin.
- Home setup purchases can happen in the wrong order.
- Food delivery can become a stress response.
- People often need a next action, not another table of numbers.

The core product question is:

What decision should the user make next to increase independence safely?

## 3. Solution Design

The MVP uses a structured decision flow:

1. Model one or more scenarios.
2. Add realistic recurring expenses.
3. Calculate an independence score.
4. Detect risks and explain them.
5. Generate recommendations.
6. Compare scenarios.
7. Plan home setup purchases.
8. Activate an adaptive operating mode.
9. Evaluate transport options.
10. Suggest meals that fit the current mode and constraints.
11. Return to a command center that shows status and next action.

The system stores decision events so the product has memory of meaningful choices such as compared scenarios, selected scenario, transport evaluation, mode activation, bought items, and postponed purchases.

## 4. Architecture Breakdown

Backend:

- Spring Boot modular monolith.
- PostgreSQL persistence.
- Flyway migrations.
- JWT authentication.
- Application services for use cases.
- Domain services for deterministic decision logic.
- Controllers kept thin.

Frontend:

- React + Vite.
- Feature-based structure.
- TanStack Query for server state.
- API client with JWT handling.
- Mobile-first screens for repeated use.

Database:

- Relational tables for core entities.
- BigDecimal/NUMERIC for money.
- JSONB only for snapshots, metadata, audit, and decision context.

## 5. Key Technical Challenges

Decision logic ownership:

The frontend must not calculate score, recommendations, comparison winners, or transport fit. Those rules live in backend services so behavior stays consistent across UI, API, and future clients.

Recommendation consistency:

Recommendations need to be deterministic, deduplicated, actionable, and tied to the user's current scenario, score, active mode, home setup, and transport state.

Scenario comparison:

The comparison engine cannot simply choose the highest score. It must consider savings, emergency coverage, negative margin, stale score data, and critical risks.

Transport modeling without maps:

The transport MVP intentionally avoids maps and live prices. It focuses on manual decision inputs: monthly cost, trips, time, comfort, safety, flexibility, and conditions to switch.

Admin without Django:

The internal admin panel provides practical catalog/settings maintenance without adding Django or a separate CMS. Spring Boot remains the source of truth and every admin mutation is audited.

## 6. Trade-offs

Modular monolith instead of microservices:

This keeps deployment, transactions, ownership validation, and local development simple. The domains are related enough that service boundaries would be premature.

Rules before AI:

The MVP prioritizes explainability. A user should understand why a scenario is risky or why a recommendation exists.

Manual transport engine:

Manual inputs keep the feature reliable and private. Live maps/prices can come later if the product needs them.

Small admin MVP:

The admin panel manages catalogs, settings, copy, import/export, and audit. It deliberately avoids workflow builders or a full CMS.

## 7. What I Would Improve Next

- Add role-based admin authorization.
- Add browser-level E2E tests for the full demo flow.
- Capture screenshots and a short walkthrough video.
- Add richer settings integration into scoring thresholds.
- Add decision outcome tracking: did the selected scenario become more stable over time?
- Improve import previews with row-level validation before commit.


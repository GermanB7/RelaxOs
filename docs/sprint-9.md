# Sprint 9 - Auth Simple + Personal Data Protection

## Scope

Sprint 9 adds private MVP authentication:

- Register.
- Login.
- `auth/me`.
- BCrypt password hashing.
- HMAC-SHA256 JWT.
- Protected data endpoints.
- `CurrentUserProvider` based on `SecurityContext`.
- Basic ownership checks preserved by user-scoped services.

Not included:

- OAuth.
- Refresh tokens.
- Roles.
- Forgot password.
- Email verification.
- 2FA.
- Admin features.

## Database

Migration:

```text
V8__auth_hardening.sql
```

Changes:

- `app_user.status`
- `app_user.last_login_at`
- `app_user.auth_provider`
- `idx_app_user_email_status`

The dev local user seed may still exist, but runtime auth no longer depends on it. In production-like use, create the initial user through registration.

## Endpoints

Public:

```text
POST /api/v1/auth/register
POST /api/v1/auth/login
GET /api/v1/system/status
GET /actuator/health
/swagger-ui/**
/v3/api-docs/**
```

Protected:

```text
GET /api/v1/auth/me
/api/v1/me/**
/api/v1/scenarios/**
/api/v1/expense-categories
/api/v1/recommendations/**
/api/v1/decisions/**
/api/v1/home/**
/api/v1/modes/**
/api/v1/meals/**
/api/v1/dashboard
```

## Security Rules

- Passwords are stored with BCrypt.
- Password hashes are never returned.
- JWT includes user id, email, issued-at, and expiration.
- JWT secret comes from `JWT_SECRET`.
- No tokens or passwords should be logged.
- Missing/invalid token returns `401`.

## Ownership Checks

Existing services continue to filter by authenticated user id:

- Profile uses current user id.
- Scenarios are queried by `id + userId`.
- Scenario-scoped expenses/score/recommendations go through scenario ownership or user-owned rows.
- Recommendation actions use `recommendationId + userId`.
- Home setup items verify `userId`.
- Modes use current user id.
- Dashboard aggregates only current user data.

## Manual QA

1. Start stack:
   ```bash
   docker compose up --build
   ```

2. Verify public endpoint:
   ```bash
   curl http://localhost:8080/api/v1/system/status
   ```

3. Verify protected endpoint rejects missing token:
   ```bash
   curl -i http://localhost:8080/api/v1/dashboard
   ```

4. Register:
   ```bash
   curl -X POST http://localhost:8080/api/v1/auth/register \
     -H "Content-Type: application/json" \
     -d '{"email":"user@example.com","password":"strong-password","displayName":"Juan","city":"Bogota","currency":"COP"}'
   ```

5. Login:
   ```bash
   curl -X POST http://localhost:8080/api/v1/auth/login \
     -H "Content-Type: application/json" \
     -d '{"email":"user@example.com","password":"strong-password"}'
   ```

6. Use Bearer token:
   ```bash
   curl http://localhost:8080/api/v1/dashboard \
     -H "Authorization: Bearer TOKEN_HERE"
   ```

7. Frontend flow:
   - Open `/register`.
   - Create user.
   - Create scenario.
   - Logout.
   - Login.
   - Confirm data remains.

## Not Now

- No refresh token table.
- No roles.
- No OAuth.
- No reset password.
- No email verification.
- No 2FA.

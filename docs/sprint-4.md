# Sprint 4 — Home Setup Roadmap Implementation

## Overview

Sprint 4 introduces the **Home Setup Roadmap** feature, enabling users to build a personalized home setup plan based on tiers of necessity. The module provides:

- A base catalog of 20+ essential home setup items organized by tier
- Personal roadmap initialization from the catalog
- Status management (PENDING, BOUGHT, POSTPONED, WISHLIST, SKIPPED)
- Custom item creation
- Priority calculation based on tier, impact, and urgency
- Recommendation engine extension for home purchase guidance

## Scope

**Included:**
- Basic home setup catalog with 20 items across TIER_1 to TIER_4
- User roadmaps with CRUD operations
- Status change tracking via decision_events
- Priority calculation (MVP)
- 2 recommendation rules (Tier 1 essentials, postpone non-essentials)

**Excluded:**
- Smart home automation
- Marketplace integration
- Real price quotations
- AI-based recommendations
- Advanced admin panel
- Meal planner
- Modes
- Transport engine

## Database Schema

### Tables Created

**purchase_catalog_item**
- Base catalog of reference items
- Active/inactive toggle for admins
- Price ranges (min/max) for guidance
- Impact and urgency levels for priority calculation

**user_purchase_item**
- Personal items linked to user + optional scenario
- Tracks catalog_item_id (null for custom items)
- Statuses: PENDING, BOUGHT, POSTPONED, WISHLIST, SKIPPED
- Timestamps for audit trail

### Indexes

```sql
idx_purchase_catalog_active_tier
idx_user_purchase_user_status
idx_user_purchase_scenario_status
idx_user_purchase_unique_catalog_per_scope
```

## Endpoints

### GET /api/v1/home/catalog

Lists purchase catalog with optional filters.

**Query Parameters:**
- `tier`: Filter by TIER_1, TIER_2, TIER_3, TIER_4
- `category`: Filter by category (Dormir, Cocina, etc.)

**Response:**
```json
[
  {
    "id": 1,
    "code": "mattress",
    "name": "Colchón",
    "category": "Dormir",
    "tier": "TIER_1",
    "estimatedMinPrice": 600000,
    "estimatedMaxPrice": 1200000,
    "impactLevel": "HIGH",
    "urgencyLevel": "HIGH",
    ...
  }
]
```

### POST /api/v1/home/roadmap/initialize

Initializes roadmap from active catalog items.

**Request:**
```json
{
  "scenarioId": 1
}
```

**Behavior:**
- Creates user_purchase_item for all active catalog items not yet existing for user+scenario
- If scenarioId is null, initializes global user roadmap
- Does not duplicate items
- Returns 201 Created

### GET /api/v1/home/roadmap

Lists user's personal purchase items.

**Query Parameters:**
- `scenarioId`: Filter by scenario (optional)
- `status`: Filter by status
- `tier`: Filter by tier
- `category`: Filter by category

**Ordering:** priority ASC, tier, status, name

### GET /api/v1/home/roadmap/summary

Returns statistics dashboard.

**Response:**
```json
{
  "totalItems": 20,
  "pendingItems": 14,
  "boughtItems": 3,
  "postponedItems": 2,
  "wishlistItems": 1,
  "tier1Total": 15,
  "tier1Bought": 3,
  "tier1CompletionPercentage": 20,
  "estimatedPendingCost": 2500000,
  "nextBestPurchase": {
    "id": 10,
    "name": "Colchón",
    "tier": "TIER_1",
    "category": "Dormir",
    "priority": 1
  }
}
```

### POST /api/v1/home/roadmap/items

Creates custom purchase item.

**Request:**
```json
{
  "scenarioId": 1,
  "name": "Cortina blackout",
  "category": "Dormir",
  "tier": "TIER_2",
  "estimatedPrice": 200000,
  "priority": 40,
  "link": "optional",
  "notes": "optional"
}
```

### PUT /api/v1/home/roadmap/items/{id}

Updates item details (name, category, tier, prices, priority, link, notes).

### PATCH /api/v1/home/roadmap/items/{id}/status

Changes item status and creates decision_event.

**Request:**
```json
{
  "status": "BOUGHT",
  "actualPrice": 850000,
  "reason": "Bought during discount"
}
```

**Behavior:**
- Updates status
- Sets purchasedAt when status = BOUGHT
- Creates decision_event with context

### DELETE /api/v1/home/roadmap/items/{id}

Deletes custom items only. Catalog items must use SKIPPED status.

## Priority Calculation (MVP)

### Formula

```
priority = base_priority + urgency_adjustment + impact_adjustment
clamped to [1, 100]
```

### Base Priority by Tier

- TIER_1: 10
- TIER_2: 30
- TIER_3: 60
- TIER_4: 90

### Adjustments

- Urgency HIGH: -5, MEDIUM: 0, LOW: +5
- Impact HIGH: -5, MEDIUM: 0, LOW: +5

### Custom Items

If no priority provided, calculated as:
- TIER_1: 20
- TIER_2: 40
- TIER_3: 70
- TIER_4: 90

## Recommendation Rules

### BUY_TIER_1_HOME_SETUP_RULE

**Triggers when:**
- Roadmap initialized (has TIER_1 items)
- Pending TIER_1 items exist
- Score ≥ 41
- No emergency fund crisis

**Recommendation:**
```
Type: HOUSEHOLD_SETUP
Severity: MEDIUM
Priority: 7
Title: "Buy Tier 1 home essentials first"
Message: "Your home setup still has essential pending items. Prioritize functional basics before comfort, decoration, or smart-home purchases."
Action: OPEN_HOME_ROADMAP
```

### POSTPONE_NON_ESSENTIAL_PURCHASES_RULE

**Triggers when:**
- Score < 61 OR emergency fund < 1 month
- Non-essential items (TIER_2+) exist with PENDING or WISHLIST status

**Recommendation:**
```
Type: HOUSEHOLD_SETUP
Severity: HIGH
Priority: 8
Title: "Postpone non-essential purchases"
Message: "Good purchase, bad moment. Keep comfort and smart-home items in wishlist until the emergency fund and monthly margin are safer."
Action: OPEN_HOME_ROADMAP
```

## Frontend Implementation

### Feature Structure

```
frontend/src/features/home-setup/
  api/
    homeSetupApi.ts          # API client
  hooks/
    useHomeSetup.ts          # React Query hooks
  components/
    HomeSetupSummaryCard.tsx
    PurchaseItemCard.tsx
    PurchaseItemStatusBadge.tsx
    PurchaseItemForm.tsx
    PurchaseTierSection.tsx
    HomeSetupFilters.tsx
  pages/
    HomeSetupPage.tsx        # Main page
```

### Pages

**GET /home-setup**
- Initialize button (if empty)
- Summary statistics
- Filters (status, tier, category)
- Items grouped by tier
- Add custom item form
- Edit/delete operations
- Status change interface

**Dashboard Update**
- Compact home-setup summary
- Tier 1 completion percentage
- Next best purchase

**Recommendations Page**
- Displays home setup recommendations
- actionLabel shows next action

### React Query Setup

**Queries:**
- `purchase-catalog`
- `home-roadmap`
- `home-setup-summary`

**Mutations:**
- `initializeRoadmap`
- `createCustomPurchaseItem`
- `updatePurchaseItem`
- `updatePurchaseItemStatus`
- `deleteCustomPurchaseItem`

## Testing

### Unit Tests Created

**PurchasePriorityCalculatorTest**
- TIER_1 high impact high urgency → priority 1
- TIER_4 low impact low urgency → priority 100
- Priority clamping [1, 100]
- Default priorities for custom items

### Manual QA Checklist

- [ ] V5 migration runs from clean DB
- [ ] Catalog seed data exists (20 items)
- [ ] GET /api/v1/home/catalog returns all items
- [ ] POST /api/v1/home/roadmap/initialize creates items
- [ ] GET /api/v1/home/roadmap lists user items
- [ ] PATCH status change creates decision_event
- [ ] Frontend builds without errors
- [ ] /home-setup page renders
- [ ] Initialize roadmap button works
- [ ] Can mark items BOUGHT, POSTPONED, WISHLIST
- [ ] Can add custom item
- [ ] Can edit item
- [ ] Dashboard shows summary
- [ ] Recommendations include home setup rules
- [ ] Docker full-stack build succeeds

## QA Flow (Manual)

1. **Setup Docker**
   ```bash
   docker compose down -v
   docker compose up --build
   ```

2. **Create Scenario**
   - POST /api/v1/scenarios
   - Set monthly_income=3000000

3. **Add Expenses**
   - POST /api/v1/scenarios/{id}/expenses x5 items
   - Ensure total < monthly_income

4. **Calculate Score**
   - POST /api/v1/scoring/calculate?scenarioId={id}

5. **Initialize Home Roadmap**
   - POST /api/v1/home/roadmap/initialize?scenarioId={id}
   - Verify 20 items created

6. **Test Status Changes**
   - PATCH items to BOUGHT (5 items)
   - PATCH items to POSTPONED (3 items)
   - Verify summary updated

7. **Add Custom Item**
   - POST /api/v1/home/roadmap/items
   - Verify appears in roadmap

8. **Recalculate Recommendations**
   - POST /api/v1/recommendations/recalculate?scenarioId={id}
   - Verify home-setup rules triggered

9. **Test Frontend**
   - http://localhost:5173/home-setup
   - Initialize roadmap
   - View summary
   - Filter items
   - Mark as BOUGHT
   - Add custom item

## Known Limitations

1. **No Real Prices**: Ranges are estimates only
2. **No Marketplace**: Links are user-provided, not validated
3. **No Smart Recommendations**: Rules are threshold-based, not ML
4. **No Admin Panel**: Catalog only managed via migration scripts
5. **No Scenarios Cleanup**: Deleted items remain in history

## Future Enhancements (Sprint 5+)

- Advanced priority engine (ROI, payback period)
- Affordability checks (block purchases if margin too low)
- Real price API integration
- Marketplace links
- Budget alerts for tier groups
- Home setup templates by region
- Shared roadmaps
- Mobile app MVP

## Notes

- All prices in COP (Colombian Pesos)
- Dates use ISO 8601 format
- User timezone not considered; all timestamps UTC
- No concurrent roadmap initialization protection yet
- Decision events created for all status changes (audit trail)

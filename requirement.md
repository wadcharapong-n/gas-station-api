# Gas Station Web Application — Software Requirements Specification

**Version:** 1.1.0 **Date:** 2026-03-20 **Status:** Draft **Change:** v1.1.0 — Scope updated from single station to multi-station chain

---

## Table of Contents

1. [Project Overview](https://claude.ai/chat/34a8d909-acb2-4e5a-9934-a04d1a33378a#1-project-overview)
2. [Stakeholders & Users](https://claude.ai/chat/34a8d909-acb2-4e5a-9934-a04d1a33378a#2-stakeholders--users)
3. [System Architecture](https://claude.ai/chat/34a8d909-acb2-4e5a-9934-a04d1a33378a#3-system-architecture)
4. [Third-Party API Integrations](https://claude.ai/chat/34a8d909-acb2-4e5a-9934-a04d1a33378a#4-third-party-api-integrations)
5. [Functional Requirements](https://claude.ai/chat/34a8d909-acb2-4e5a-9934-a04d1a33378a#5-functional-requirements)
- 5.1 [Public-Facing Web Application](https://claude.ai/chat/34a8d909-acb2-4e5a-9934-a04d1a33378a#51-public-facing-web-application)
- 5.2 [Back-Office Management Portal](https://claude.ai/chat/34a8d909-acb2-4e5a-9934-a04d1a33378a#52-back-office-management-portal)
6. [Non-Functional Requirements](https://claude.ai/chat/34a8d909-acb2-4e5a-9934-a04d1a33378a#6-non-functional-requirements)
7. [Data Models](https://claude.ai/chat/34a8d909-acb2-4e5a-9934-a04d1a33378a#7-data-models)
8. [Tech Stack](https://claude.ai/chat/34a8d909-acb2-4e5a-9934-a04d1a33378a#8-tech-stack)
9. [Pages & Navigation Structure](https://claude.ai/chat/34a8d909-acb2-4e5a-9934-a04d1a33378a#9-pages--navigation-structure)
10. [Milestones & Delivery Phases](https://claude.ai/chat/34a8d909-acb2-4e5a-9934-a04d1a33378a#10-milestones--delivery-phases)
11. [Assumptions & Constraints](https://claude.ai/chat/34a8d909-acb2-4e5a-9934-a04d1a33378a#11-assumptions--constraints)
12. [Open Questions](https://claude.ai/chat/34a8d909-acb2-4e5a-9934-a04d1a33378a#12-open-questions)

---

## 1. Project Overview

### 1.1 Purpose

This document defines the requirements for a **Gas Station Information & Management System** — a React-based web application consisting of two parts:

- **Public-Facing Web App** — displays real-time information for all stations in the network (fuel prices, fuel availability, locations) to anyone visiting the site, with the ability to browse and compare stations.
- **Back-Office Management Portal** — allows Corporate Admins to manage all stations centrally, and Station Managers to manage their assigned station(s) individually, monitor fuel levels, update prices, and review operational reports.

### 1.2 Scope


| In Scope                                                 | Out of Scope                       |
| -------------------------------------------------------- | ---------------------------------- |
| Multi-station chain / franchise (multiple locations)     | Hardware POS / pump integration    |
| Fuel price display per station (via third-party API)     | Online payment / fuel pre-purchase |
| Fuel availability display per station (volume in litres) | Mobile native app (iOS / Android)  |
| Google Maps integration (all stations on one map)        | Loyalty / rewards programme        |
| Station Manager back-office (per-station view)           | Customer accounts / login          |
| Corporate Admin back-office (cross-station view)         | Third-party franchise billing      |
| Data CRUD for each station's info                        | —                                  |


---

## 2. Stakeholders & Users

### 2.1 User Roles


| Role                | Access Level                           | Description                                                                                               |
| ------------------- | -------------------------------------- | --------------------------------------------------------------------------------------------------------- |
| **Visitor**         | Read-only (public)                     | Any person browsing the public site to check fuel prices, availability, or find a station in the network  |
| **Station Manager** | Back-office — assigned station(s) only | Manages data, monitors API feeds, and reviews reports for their specific assigned station(s)              |
| **Corporate Admin** | Back-office — all stations             | Full access across all stations; manages station roster, assigns managers, and views network-wide reports |


### 2.2 Stakeholder Needs

**Visitor needs:**

- Browse all stations in the network on a single map.
- Filter or search stations by location or fuel type availability.
- See current fuel prices for any station without signing in.
- See how much fuel (in litres) is available per fuel type at a selected station.
- Get directions to a chosen station.
- View each station's operating hours and contact details.

**Station Manager needs:**

- Log in securely and see only the station(s) they are assigned to.
- View a dashboard summarising live fuel levels and prices for their station.
- Override or manually update displayed prices and fuel availability when APIs are unavailable.
- Edit their station's profile information (name, address, hours, contact).
- View historical logs of price changes and fuel level changes for their station.

**Corporate Admin needs:**

- View a network-wide dashboard across all stations.
- Add, edit, activate, or deactivate stations in the network.
- Assign Station Managers to specific stations.
- Compare fuel prices and availability across all stations.
- Configure global API settings and monitor API health for the entire network.
- Export cross-station reports (prices, fuel levels, activity logs).

---

## 3. System Architecture

```
┌──────────────────────────────────────────────────────────────┐
│                      React Frontend                           │
│                                                               │
│  ┌─────────────────────┐   ┌───────────────────────────────┐ │
│  │  Public Web App     │   │    Back-Office Portal         │ │
│  │  (unauthenticated)  │   │                               │ │
│  │  • Station listing  │   │  ┌─────────────────────────┐  │ │
│  │  • Station detail   │   │  │ Station Manager View    │  │ │
│  │  • Map (all         │   │  │ (assigned stations only)│  │ │
│  │    stations)        │   │  └─────────────────────────┘  │ │
│  │  • Price board      │   │  ┌─────────────────────────┐  │ │
│  │  • Fuel levels      │   │  │ Corporate Admin View    │  │ │
│  └──────────┬──────────┘   │  │ (all stations)          │  │ │
│             │               │  └─────────────────────────┘  │ │
│             │               └──────────────┬────────────────┘ │
└─────────────┼──────────────────────────────┼──────────────────┘
              │                              │
              ▼                              ▼
┌─────────────────────────────────────────────────────────────┐
│                     Backend API (REST)                       │
│              (Node.js / Express or NestJS)                  │
│   • Multi-tenant station data routing                       │
│   • Role-based access control (RBAC)                        │
└──────────┬─────────────────────────────┬────────────────────┘
           │                             │
           ▼                             ▼
┌──────────────────┐       ┌─────────────────────────────────┐
│    Database      │       │        Third-Party APIs          │
│  • Stations      │       │  • Fuel Price API (per station)  │
│  • FuelTypes     │       │  • Fuel Availability API         │
│  • FuelPrices    │       │    (per station)                 │
│  • FuelLevels    │       │  • Google Maps API               │
│  • Users         │       └─────────────────────────────────┘
│  • StationUsers  │
│    (assignments) │
└──────────────────┘

```

---

## 4. Third-Party API Integrations

### 4.1 Fuel Price API


| Item                 | Detail                                                                                                        |
| -------------------- | ------------------------------------------------------------------------------------------------------------- |
| **Purpose**          | Retrieve current market fuel prices per fuel type (e.g., Diesel, Gasohol 91, Gasohol 95, E20, E85)            |
| **Scope**            | Each station may have its own API endpoint or station ID parameter; the backend fans out requests per station |
| **Data consumed**    | Station ID, price per litre, fuel type, timestamp of last update                                              |
| **Update frequency** | Poll every 15–30 minutes per station (configurable globally, overridable per station)                         |
| **Fallback**         | Display last known price with a "last updated" timestamp; Manager can enter a manual override per station     |
| **Display location** | Public station detail page (price board), Back-office dashboard (per-station and network view)                |


### 4.2 Fuel Availability API (Volume in Litres)


| Item                 | Detail                                                                                                |
| -------------------- | ----------------------------------------------------------------------------------------------------- |
| **Purpose**          | Retrieve real-time fuel tank levels (litres remaining) per fuel type, per station                     |
| **Scope**            | Each station has its own sensor/feed identified by a station ID parameter                             |
| **Data consumed**    | Station ID, fuel type, current volume (litres), tank capacity (litres), percentage full               |
| **Update frequency** | Poll every 5–10 minutes per station (configurable)                                                    |
| **Fallback**         | Show "Data unavailable" with last known reading; Manager can enter a manual override per station      |
| **Display location** | Public station detail page, Back-office dashboard with per-station alerts                             |
| **Alert thresholds** | Visual warning when any tank at any station drops below a configurable threshold (e.g., 20% capacity) |


### 4.3 Google Maps / Geolocation API


| Item               | Detail                                                                                                    |
| ------------------ | --------------------------------------------------------------------------------------------------------- |
| **Purpose**        | Display all stations on a network map and provide per-station directions                                  |
| **Data consumed**  | Each station's lat/long (stored in DB), rendered via Google Maps JavaScript API                           |
| **Features**       | Network map (all stations as markers), station detail map, street view link, "Get Directions" button      |
| **Station finder** | Visitor can click a map marker to open the station's detail page                                          |
| **Configuration**  | Google Maps API key stored as an environment variable; managed by Corporate Admin in back-office settings |


### 4.4 API Configuration & Health Monitoring (Back-Office)

- All third-party API base URLs, API keys, and polling intervals are configurable from the back-office **Settings** page.
- A live **API Health** panel shows the status (Connected / Error / Degraded) of each integration, last successful response time, and error details if applicable.

---

## 5. Functional Requirements

### 5.1 Public-Facing Web Application

#### FR-P01 — Station Listing / Home Page

- Display a searchable, filterable list of all active stations in the network.
- Each station card shows: name, address, distance from visitor's location (if geolocation permitted), open/closed status, and a summary of available fuel types.
- A **network map** shows all stations as markers; clicking a marker navigates to the station's detail page.
- Visitor can filter stations by: fuel type available, open now, or proximity.

#### FR-P02 — Station Detail Page

- Display the selected station's name, logo, tagline, and short description.
- Show current operating status (**Open** / **Closed**) based on configured hours, and today's hours.
- Show all sections below (FR-P03 to FR-P05) scoped to this station.
- URL format: `/stations/:stationId` (SEO-friendly slug optional).

#### FR-P03 — Fuel Price Board (per station)

- Display a real-time price board showing each available fuel type and its current price (THB/litre).
- Show the timestamp of the last price update.
- If the API is unavailable, show the last known price with a clear "prices may not be current" notice.

#### FR-P04 — Fuel Availability Display (per station)

- Display a visual indicator (e.g., gauge or progress bar) per fuel type showing current volume in litres and percentage of tank capacity remaining.
- Fuel types with very low availability (below the configured threshold) are highlighted visually (amber/red).
- If data is unavailable, display a fallback message.

#### FR-P05 — Station Location & Map

- Embed an interactive Google Map showing the selected station's exact location.
- Display the station's full address.
- Include a **Get Directions** button that opens Google Maps navigation on the user's device.

#### FR-P06 — Station Info & Contact

- Display phone number, email (if available), and any social media links.
- Display a list of services offered (e.g., car wash, convenience store, air pump) — managed via back-office.

---

### 5.2 Back-Office Management Portal

#### FR-B01 — Authentication & Role-Based Access Control (RBAC)

- All back-office users log in with email and password.
- Session expires after a configurable idle timeout (default: 60 minutes).
- Password reset via email is supported.
- No self-registration; accounts are created by a Corporate Admin.
- **Station Manager** can only view and edit stations they are assigned to.
- **Corporate Admin** has unrestricted access to all stations and user management.

#### FR-B02 — Corporate Admin Dashboard (Network Overview)

- Summary cards showing network-wide metrics: total stations, stations with low-fuel alerts, stations currently open/closed.
- A network map with station markers colour-coded by alert status (normal / warning / critical).
- A sortable/filterable table of all stations showing latest fuel prices and levels at a glance.
- Cross-station alert feed: list of all active low-fuel warnings across the network.

#### FR-B03 — Station Manager Dashboard (Single Station View)

- Summary cards showing: current fuel price per type, current fuel level per type (litres + %), API health status, active alerts.
- A recent activity feed showing the last 10 data changes or API events for the assigned station.
- Station Managers with multiple assigned stations see a station switcher at the top of the dashboard.

#### FR-B04 — Station Management (Corporate Admin)

- **Station List:** View all stations; add new station; activate / deactivate a station.
- **Station Editor:** Edit any station's profile (name, logo, address, GPS coordinates, hours, contact, services).
- **Manager Assignment:** Assign one or more Station Managers to each station; remove assignments.

#### FR-B05 — Fuel Price Management (per station)

- View current prices fetched from the Fuel Price API for the selected station.
- **Manual Override:** Enter a custom price for any fuel type at any station.
- View price history log per station (date, time, price, source: API or Manual, changed by).
- Export price history per station or across all stations as CSV.

#### FR-B06 — Fuel Availability Management (per station)

- View current tank levels fetched from the Fuel Availability API for the selected station.
- **Manual Override:** Enter a current volume (litres) for any fuel type at the selected station.
- Configure the low-fuel alert threshold per fuel type per station.
- View fuel level history log per station.
- Export fuel level history as CSV.

#### FR-B07 — API Settings & Health (Corporate Admin)

- Configure global API endpoints, API keys, and polling intervals for each third-party integration.
- Optionally override API settings per station (e.g., if each station uses a different endpoint).
- View live API health status per station with last-polled timestamp and error messages.
- Manually trigger a data refresh for any station or for all stations.
- Toggle integrations on or off globally or per station.

#### FR-B08 — User Management (Corporate Admin only)

- Create, edit, and deactivate Manager and Admin accounts.
- Assign Managers to one or more stations.
- View login activity log per user.

---

## 6. Non-Functional Requirements

### 6.1 Performance

- Public pages must achieve a **Largest Contentful Paint (LCP) of ≤ 2.5 seconds** on a standard 4G connection.
- API polling must not block page rendering; all live data fetched asynchronously.
- Back-office dashboard must refresh live data without full page reload.

### 6.2 Reliability & Availability

- The application must display meaningful fallback content when any third-party API is unavailable — no blank or broken sections.
- Target uptime: **99.5%** (excluding third-party API downtime).

### 6.3 Security

- Back-office routes are protected; unauthenticated users are redirected to the login page.
- API keys are stored server-side as environment variables — never exposed to the client.
- All data transmitted over HTTPS.
- Implement CSRF protection on all state-changing back-office API calls.
- Passwords stored using bcrypt (or equivalent) hashing.

### 6.4 Usability

- Public site is fully responsive (mobile, tablet, desktop).
- Back-office portal is responsive down to tablet (768 px minimum); full mobile support is a nice-to-have.
- Public site must be accessible to **WCAG 2.1 Level AA** standards.

### 6.5 Internationalisation

- Initial language: **Thai (TH)** with English (EN) as a secondary option.
- Currency displayed in **Thai Baht (THB)** with the ฿ symbol.
- Dates displayed in the user's local timezone (Asia/Bangkok default).

---

## 7. Data Models

### 7.1 Station


| Field            | Type       | Description                                      |
| ---------------- | ---------- | ------------------------------------------------ |
| `id`             | UUID       | Primary key                                      |
| `name`           | string     | Station display name                             |
| `slug`           | string     | URL-friendly identifier (unique)                 |
| `logo_url`       | string     | URL of uploaded logo image                       |
| `tagline`        | string     | Short marketing tagline                          |
| `description`    | text       | Longer station description                       |
| `address`        | string     | Full street address                              |
| `latitude`       | float      | GPS latitude                                     |
| `longitude`      | float      | GPS longitude                                    |
| `phone`          | string     | Contact phone number                             |
| `email`          | string     | Contact email                                    |
| `social_links`   | JSON       | Map of platform → URL                            |
| `services`       | JSON array | List of services offered                         |
| `hours`          | JSON       | Operating hours per day                          |
| `is_active`      | boolean    | Whether station is visible publicly              |
| `api_station_id` | string     | Station identifier used in third-party API calls |
| `created_at`     | timestamp  | When station was added                           |
| `updated_at`     | timestamp  | Last modification time                           |


### 7.2 FuelType


| Field                  | Type    | Description                                 |
| ---------------------- | ------- | ------------------------------------------- |
| `id`                   | UUID    | Primary key                                 |
| `station_id`           | UUID    | Foreign key → Station                       |
| `name`                 | string  | E.g., "Gasohol 95", "Diesel B7"             |
| `code`                 | string  | Short code for API mapping                  |
| `tank_capacity_litres` | float   | Physical tank capacity at this station      |
| `low_threshold_litres` | float   | Alert threshold in litres                   |
| `is_active`            | boolean | Whether this fuel type is currently offered |


### 7.3 FuelPrice


| Field             | Type      | Description                          |
| ----------------- | --------- | ------------------------------------ |
| `id`              | UUID      | Primary key                          |
| `station_id`      | UUID      | Foreign key → Station                |
| `fuel_type_id`    | UUID      | Foreign key → FuelType               |
| `price_per_litre` | decimal   | Current price                        |
| `source`          | enum      | `api` or `manual`                    |
| `override_active` | boolean   | True if manual override is in effect |
| `recorded_at`     | timestamp | When this price was recorded         |
| `recorded_by`     | UUID      | User ID (null if API-sourced)        |


### 7.4 FuelLevel


| Field             | Type      | Description                          |
| ----------------- | --------- | ------------------------------------ |
| `id`              | UUID      | Primary key                          |
| `station_id`      | UUID      | Foreign key → Station                |
| `fuel_type_id`    | UUID      | Foreign key → FuelType               |
| `volume_litres`   | float     | Current volume in litres             |
| `source`          | enum      | `api` or `manual`                    |
| `override_active` | boolean   | True if manual override is in effect |
| `recorded_at`     | timestamp | When this reading was recorded       |
| `recorded_by`     | UUID      | User ID (null if API-sourced)        |


### 7.5 User


| Field           | Type      | Description                          |
| --------------- | --------- | ------------------------------------ |
| `id`            | UUID      | Primary key                          |
| `name`          | string    | Display name                         |
| `email`         | string    | Login email (unique)                 |
| `password_hash` | string    | Bcrypt hash                          |
| `role`          | enum      | `station_manager`, `corporate_admin` |
| `is_active`     | boolean   | Account status                       |
| `last_login_at` | timestamp | Last successful login                |
| `created_at`    | timestamp | Account creation time                |


### 7.6 StationUser (Manager Assignment)


| Field         | Type      | Description                                |
| ------------- | --------- | ------------------------------------------ |
| `id`          | UUID      | Primary key                                |
| `station_id`  | UUID      | Foreign key → Station                      |
| `user_id`     | UUID      | Foreign key → User                         |
| `assigned_at` | timestamp | When the assignment was made               |
| `assigned_by` | UUID      | Corporate Admin who created the assignment |


---

## 8. Tech Stack


| Layer                  | Technology                                               | Notes                                       |
| ---------------------- | -------------------------------------------------------- | ------------------------------------------- |
| **Frontend**           | React 18+ (Vite)                                         | Component-based SPA                         |
| **Routing**            | React Router v6                                          | Public vs back-office route groups          |
| **State Management**   | Zustand or Redux Toolkit                                 | Global state for live data                  |
| **Styling**            | Tailwind CSS                                             | Utility-first; responsive out of the box    |
| **HTTP Client**        | Axios                                                    | API calls with interceptors for auth tokens |
| **Maps**               | Google Maps JavaScript API / `@react-google-maps/api`    | Map embed and directions                    |
| **Charts**             | Recharts                                                 | Fuel level history graphs in back-office    |
| **Auth**               | JWT (access + refresh tokens)                            | Stored in httpOnly cookies                  |
| **Backend**            | Node.js + Express (or NestJS)                            | REST API; proxies third-party API calls     |
| **Database**           | PostgreSQL                                               | Relational data; hosted on managed service  |
| **Environment Config** | `.env` files + CI/CD secrets                             | API keys never committed to source control  |
| **Deployment**         | Vercel / Netlify (frontend) + Railway / Render (backend) | Or unified via Docker Compose on a VPS      |


---

## 9. Pages & Navigation Structure

### 9.1 Public Web App

```
/                          → Station listing + network map (all stations)
/stations/:stationId       → Station detail (prices, levels, map, contact)
/stations/:stationId/map   → Full-screen map & directions for selected station

```

### 9.2 Back-Office Portal

```
/admin/login                         → Login page (all roles)

── Corporate Admin ──────────────────────────────────────────
/admin/dashboard                     → Network-wide overview (all stations)
/admin/stations                      → Station list: add / edit / deactivate
/admin/stations/:stationId           → Edit individual station profile
/admin/stations/:stationId/prices    → Price management for a station
/admin/stations/:stationId/fuel      → Fuel level management for a station
/admin/users                         → User management + station assignments
/admin/settings                      → Global API configuration + health monitor

── Station Manager ──────────────────────────────────────────
/admin/dashboard                     → Dashboard scoped to assigned station(s)
/admin/my-station/prices             → Price management (assigned station only)
/admin/my-station/fuel               → Fuel level management (assigned station only)
/admin/my-station/profile            → Edit profile (assigned station only)

```

---

## 10. Milestones & Delivery Phases

### Phase 1 — Foundation (Weeks 1–3)

- Project scaffolding (React + Vite, routing, Tailwind)
- Backend API skeleton + database schema (multi-station, StationUser assignment table)
- Authentication + RBAC (login, JWT, role-scoped protected routes)
- Station CRUD — Corporate Admin can add, edit, deactivate stations

### Phase 2 — Third-Party Integrations (Weeks 4–6)

- Google Maps integration — network map (all stations) + per-station detail map
- Fuel Price API integration per station (polling, display, fallback)
- Fuel Availability API integration per station (polling, display, fallback, alerts)
- API health monitoring panel — per station and global view

### Phase 3 — Back-Office Features (Weeks 7–9)

- Corporate Admin dashboard (network-wide overview, alert map)
- Station Manager dashboard (single-station view, station switcher)
- Manual overrides for prices and fuel levels per station
- History logs + CSV export (per station and cross-station)
- Threshold configuration per station
- User management + station assignment (Corporate Admin)

### Phase 4 — Public UI Polish & QA (Weeks 10–11)

- Station listing page + network map with markers
- Responsive design refinement (mobile-first)
- Accessibility audit (WCAG 2.1 AA)
- Thai / English language support
- Performance optimisation (LCP target)
- End-to-end testing (multi-station scenarios)

### Phase 5 — Deployment & Handover (Week 12)

- Production environment setup
- CI/CD pipeline configuration
- Environment variable and secret management
- Deployment documentation
- Corporate Admin and Station Manager onboarding & training

---

## 11. Assumptions & Constraints

- The Fuel Price API and Fuel Availability API will be provided by the client; this document assumes both expose a REST/JSON interface that accepts a station identifier parameter. Exact endpoint schemas will be documented in a separate API Integration Specification once providers are confirmed.
- Google Maps API billing is the client's responsibility; a valid billing-enabled Google Cloud project is required before Phase 2.
- The system supports multiple physical stations under a single network/brand. Cross-brand multi-tenancy is out of scope.
- Internet connectivity at each station is stable enough to support periodic API polling; offline / disconnected mode is out of scope beyond the fallback display behaviour described above.
- The number of stations in the network at launch is expected to be manageable (< 100); if the network scales to hundreds of stations, polling architecture may need to be revisited (e.g., webhook-based updates).
- Initial deployment infrastructure (hosting provider, domain, SSL certificate) will be decided with the client before Phase 5.

---

## 12. Open Questions


| #   | Question                                                                                                                         | Owner  | Target Date    |
| --- | -------------------------------------------------------------------------------------------------------------------------------- | ------ | -------------- |
| 1   | Which Fuel Price API provider will be used? Does it support per-station queries via a station ID?                                | Client | Before Phase 2 |
| 2   | Which Fuel Availability API provider will be used? Does it push data (webhooks) or require per-station polling?                  | Client | Before Phase 2 |
| 3   | How many stations will be in the network at launch? What is the expected maximum?                                                | Client | Before Phase 1 |
| 4   | What are the fuel types offered? Are they the same across all stations, or does each station have a different set?               | Client | Before Phase 1 |
| 5   | Is Thai the only language required, or is English also needed on the public site?                                                | Client | Before Phase 4 |
| 6   | Should price and fuel history data be retained indefinitely, or is there an archival/deletion policy?                            | Client | Before Phase 3 |
| 7   | Do Station Managers or Corporate Admins need email/SMS notifications for low-fuel alerts?                                        | Client | Before Phase 3 |
| 8   | Should each station have its own URL/subdomain (e.g., `station-name.brand.com`), or all under a single domain with path routing? | Client | Before Phase 1 |
| 9   | What is the preferred hosting environment (cloud provider, self-hosted VPS, managed PaaS)?                                       | Client | Before Phase 5 |


---

*This document is a living specification. All requirements should be reviewed and signed off by the client before the start of each phase. Changes after sign-off are subject to scope assessment.*
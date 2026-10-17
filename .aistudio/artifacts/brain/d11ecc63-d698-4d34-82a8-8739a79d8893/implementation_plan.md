# Medicine Lifecycle Management System (PBL-3MRL) - Android Customer App

A complete, production-grade Android application built with Kotlin and Jetpack Compose for the **Medicine Lifecycle Management System (PBL-3MRL)**, featuring real-time Supabase sync with offline resilience, AI prescription scanning, near-expiry eco-clearance marketplace, reverse logistics disposal, animated GPS delivery dispatch map, and tactile Canvas scratch cards.

## User Review & Critical Decisions

> [!IMPORTANT]
> The application architecture and design systems are aligned with your confirmed choices:
> - **Evaluator Switcher Bar**: Fixed floating presenter dock above the Android frame with instant simulation triggers (`Pack Order`, `Out for Delivery`, `Mark Delivered`, `+ Demo Scratch Card`, and `Supabase Sync Status`).
> - **Prescription AI Verification**: Instant AI laser scan simulation with auto-extracted Schedule-H medicines and verified badge.
> - **Live GPS Map Visualization**: High-fidelity custom animated vector road map with pharmacy origin, customer destination, animated route dashes, and a moving courier motorcycle marker.
> - **Eco-Deals & Sustainability**: 50% discount clearance for near-expiry batches (15–30 days) and reverse logistics return for expired medicine strips in exchange for Eco-Karma points and scratch cards.
> - **Supabase Realtime Sync**: Connected to `https://oxzrzmwixhfcssspofpk.supabase.co` (`sb_publishable_D7z-EryNFkDzsFV8DPYcuA_5tfcTkun`) with instant 1-tap demo auth ("Continue as Demo User - Chethan") and local Room database fallback.

- **Confirmed Decision 1**: Fixed presenter bar docked at the top allowing evaluators to simulate chemist and rider state transitions instantly without leaving the app.
- **Confirmed Decision 2**: Pure white (`#FFFFFF`) and clean medical slate (`#F8FAFC`) theme accented with Medical Blue (`#0284C7`), Eco-Emerald (`#10B981`), and Expiry Amber (`#F59E0B`).
- **Confirmed Decision 3**: Complete 4-tab bottom navigation with live badge counts: Home, Eco-Deals, Track, and Rewards.

---

## 1. Overview & Core Concept

The **PBL-3MRL End-User Customer App** digitizes the entire pharmaceutical lifecycle from safe retail fulfillment to near-expiry clearance and green reverse-logistics disposal:
- **Fast Medicine Delivery**: Hyperlocal 11-minute delivery in Mangaluru with smart Schedule-H Rx eligibility verification.
- **Pharmaceutical Waste Prevention (Eco-Deals)**: 50% off near-expiry batches with transparent expiry countdowns and pharmaceutical waste diverted metrics.
- **Circular Reverse Logistics**: Home pickup scheduling for expired or unused medicine strips, preventing water-table contamination in exchange for Eco-Karma points.
- **Gamified Rewards & Wallet**: Instant MedWallet cashback (`₹`), Eco-Karma points, and interactive scratch cards.

---

## 2. User Experience & Visual Design

```
┌────────────────────────────────────────────────────────────────────────┐
│ TOP EVALUATOR BAR: [Pack Order] [Rider En Route] [Delivered] [+ Card]  │
│ [🟢 Supabase: Live Sync]                                                │
├────────────────────────────────────────────────────────────────────────┤
│ ANDROID PHONE FRAME:                                                   │
│ ┌────────────────────────────────────────────────────────────────────┐ │
│ │ 📍 Mangaluru • ⚡ 11 Mins            [ ₹120 MedWallet ] [ 👤 User ] │ │
│ │ [ 🔍 Search Brand or Generic Salt... ]                             │ │
│ │ CHIPS: [ All ] [ Fever & Pain ] [ Antibiotics ] [ Vitamins ] [ ... ]│ │
│ ├────────────────────────────────────────────────────────────────────┤ │
│ │ 🩺 AI PRESCRIPTION SCANNER: [ Upload Rx for Instant Verification ] │ │
│ ├────────────────────────────────────────────────────────────────────┤ │
│ │ MEDICINE CATALOG:                                                  │ │
│ │ ┌─────────────────────────┐  ┌───────────────────────────────────┐ │ │
│ │ │ Augmentin 625 Duo (Rx)  │  │ Dolo 650 (Paracetamol)            │ │ │
│ │ │ ₹185.00  • In Stock 24  │  │ ₹32.00  • In Stock 150            │ │ │
│ │ │ [ + Add to Bag ]        │  │ [ + Add to Bag ]                  │ │ │
│ │ └─────────────────────────┘  └───────────────────────────────────┘ │ │
│ ├────────────────────────────────────────────────────────────────────┤ │
│ │ FLOATING CART PILL: [ 2 Items • ₹217 (Saved ₹35) • Checkout -> ]  │ │
│ ├────────────────────────────────────────────────────────────────────┤ │
│ │ BOTTOM NAV: [ 🏠 Home ]  [ 🌿 Eco-Deals ]  [ 📍 Track ]  [ 🎁 Rewards ] │
│ └────────────────────────────────────────────────────────────────────┘ │
└────────────────────────────────────────────────────────────────────────┘
```

### Visual Themes & Design Tokens
- **Background**: Medical Clean White (`#FFFFFF`) and Soft Slate (`#F8FAFC`).
- **Surfaces & Cards**: Card Pure White with 1dp border `#E2E8F0` and subtle elevation.
- **Primary Accent**: Medical Blue (`#0284C7`) and Deep Navy (`#0F172A`).
- **Eco & Savings Accent**: Emerald Green (`#10B981`) for near-expiry discounts and Eco-Karma.
- **Alert Accent**: Expiry Amber (`#F59E0B`) for batch expiry countdown badges.
- **Prescription Tag**: Violet (`#8B5CF6`) pill indicating Schedule-H Rx requirement.

---

## 3. Detailed Tab & Feature Specifications

### Top Evaluator Switcher Bar
- Docked above the main Android frame with presenter buttons:
  - `[Simulate Chemist: Pack Active Order]` (moves order from `pending` to `ready_for_pickup`)
  - `[Simulate Rider: Out for Delivery]` (moves order to `out_for_delivery`, animates motorcycle on map)
  - `[Simulate Rider: Mark Delivered]` (moves order to `delivered`, triggers celebration)
  - `[+ Instant Demo Scratch Card]` (awards bonus mystery card directly into wallet)
  - `[Supabase Status Indicator]` (toggles/displays live sync vs. robust local Room fallback)

### Tab 1: Home, Catalog & AI Prescription Scanner
- **Header**: Delivery location ("Mangaluru • ⚡ 11 Mins"), MedWallet balance chip (`₹120`), and Demo User ("Chethan K.").
- **Search & Filter**: Search by brand name (e.g., *Dolo 650*, *Augmentin*, *Azithral*) or generic salt (*Paracetamol*, *Amoxicillin + Clavulanate*).
- **12+ Realistic Medicines**: Comprehensive dataset with Brand, Generic Salt, Manufacturer, Price, Stock, and `requires_rx` flag.
- **Smart Rx Scanner Modal**:
  - Bottom sheet with "Sample Doctor Prescription" preset or file selection.
  - Animated laser scanner beam (2-second scanning animation) extracting medicines and unlocking Schedule-H items for checkout.

### Tab 2: Eco-Deals & Reverse Logistics
- **Near-Expiry Clearance Marketplace**: Batches expiring in 15–30 days offered at **50% OFF**.
  - Badge: *"Expires in 22 Days • Safe for Immediate Course • 50% OFF"*.
  - Live metric card: Pharmaceutical chemical waste prevented (in grams/tablets).
- **Reverse Logistics Disposal**: Form to schedule a free doorstep rider pickup for expired/unused blister strips.
  - Submitting schedules pickup, awards **+50 Eco-Karma Points**, and unlocks a **Bonus Scratch Card**.

### Slide-Up Cart & Android UPI Checkout
- **Floating Cart Dock**: Displays total items, total price, and eco-savings.
- **Checkout Sheet**: Item list, won coupon selector, MedWallet balance toggle (`₹`), bill breakdown, and Android UPI selector (Google Pay, PhonePe, Paytm, COD).
- **Pay & Place Order**: Plays payment verification tick animation, writes order to Supabase/Room, unlocks a fresh mystery scratch card, and triggers the scratch modal.

### Tab 3: Live Order Tracking & Interactive Dispatch Map
- **Interactive Simulated GPS Map**: Compose Canvas drawing streets, pharmacy store pin, customer home pin, animated dashed route path, and an interpolating rider motorcycle marker.
- **4-Stage Stepper**:
  1. *Order Placed* (`pending`)
  2. *Verified & Packed* (`ready_for_pickup`)
  3. *Out for Delivery* (`out_for_delivery`)
  4. *Delivered* (`delivered`)
- **Rider Card**: "Ramesh K • 4.9★ • Cold-Chain Certified", mock call button, and delivery verification `OTP: 4829`.

### Tab 4: Interactive Canvas Scratch Cards & MedWallet
- **MedWallet Header**: Animated counters for Cashback Balance (`₹`) and Eco-Karma Points.
- **Scratch Card Grid**: Unscratched mystery cards with shimmering gradients and scratched card history.
- **Interactive Scratch-Off Modal**:
  - `280px x 280px` hardware-accelerated Compose Canvas with gesture erasing (32px radius circle).
  - Calculates transparent percentage; auto-reveals at >40% with confetti burst, sound effect, and immediate wallet/coupon credit.

---

## 4. Technical Architecture & Component Flow

```
┌────────────────────────────────────────────────────────────────────────┐
│                        EVALUATOR PRESENTER BAR                         │
│  [Pack Order]  [Rider Out]  [Delivered]  [+ Scratch Card]  [Sync Status]│
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
┌───────────────────────────────────▼────────────────────────────────────┐
│                        MAIN ANDROID INTERFACE                          │
│  MainActivity ─── Scaffold with Bottom Navigation                      │
│   ├── Tab 1: HomeScreen (Catalog, Search, Rx Scanner Sheet)            │
│   ├── Tab 2: EcoDealsScreen (50% Near-Expiry & Reverse Logistics)      │
│   ├── Tab 3: LiveTrackingScreen (Canvas GPS Map & 4-Stage Stepper)     │
│   └── Tab 4: RewardsScreen (MedWallet & Canvas Scratch Cards)          │
│   ├── FloatingCartPill & CheckoutSheet (UPI Payment & Coupon Applier)  │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
┌───────────────────────────────────▼────────────────────────────────────┐
│                       BUSINESS LOGIC (VIEWMODEL)                       │
│  MedicineStoreViewModel                                                │
│   ├── Catalog & Prescription Verification Manager                      │
│   ├── EcoDeals & Reverse Logistics Pickup Controller                   │
│   ├── Order Dispatch & GPS Interpolation Engine                        │
│   └── MedWallet, Scratch Engine & Audio Tone Synthesizer               │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
┌───────────────────────────────────▼────────────────────────────────────┐
│                          PERSISTENCE LAYER                             │
│  StoreRepository                                                       │
│   ├── Remote: Supabase REST API (orders, realtime status changes)      │
│   └── Local: Room Database (medicines, orders, coupons, wallet, cards) │
└────────────────────────────────────────────────────────────────────────┘
```

### Key Sequences
1. **Prescription Verification**: Tapping "Upload Rx" launches modal -> selects sample prescription -> laser scan animates -> verifies Rx eligibility -> unlocks Schedule-H medicines in cart.
2. **Checkout to Tracking**: Checkout with UPI -> order created in Supabase & Room -> awards scratch card -> evaluator bar can simulate chemist packing (`ready_for_pickup`) and rider transit (`out_for_delivery`), smoothly updating the live map.
3. **Scratch & MedWallet Credit**: User drags finger on canvas scratcher -> exceeds 40% -> confetti fireworks trigger -> cash credited directly to MedWallet balance (`₹`) with animated counter.

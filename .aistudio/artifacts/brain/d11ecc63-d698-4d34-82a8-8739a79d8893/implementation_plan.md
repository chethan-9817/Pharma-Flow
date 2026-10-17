# ScratchMart: Wishlist, Promo Codes, Live Order Tracking & Audio-Visual Enhancements

An expansion plan introducing a product wishlist, manual promo code redemption, live order delivery tracking timelines, multi-metallic scratch foil styles (Holographic Prism, Gold Leaf, Cyber Neon), and native audio sound effects for scratching, prize reveals, and checkout.

## User Review & Critical Decisions

> [!IMPORTANT]
> The enhancement scope is aligned with your confirmed choices:
> - **Wishlist & Filtering**: Save favorites with heart animations and instant "Wishlist" filter chip in the Shop.
> - **Promo Code Engine**: Manual promo code input in the cart with instant coupon validation (e.g. `LUCKY2026`, `CYBERDROP`, `FREESHIP`).
> - **Live Order Tracking**: Interactive multi-stage shipment timeline (Order Placed → Processing → Shipped → Out for Delivery → Delivered) with simulated delivery updates and driver details.
> - **Multi-Foil Visuals**: Holographic Rainbow Prism, Burnished Gold, and Cyber Neon foils with animated shimmer on scratch cards.
> - **Native Sound & Audio**: Tactile sound effects using Android audio synthesis for scratch drag, celebratory reveal fanfare, and checkout chime (with sound toggle).

- **Confirmed Decision 1**: Add Wishlist state to Room database and ViewModel with animated heart icons and a quick "Wishlist" filter chip.
- **Confirmed Decision 2**: Expand Checkout Cart with manual promo code text field alongside scratch coupons.
- **Confirmed Decision 3**: Add Order Details & Live Tracking modal sheet showing real-time delivery status stages.
- **Confirmed Decision 4**: Implement `SoundManager` utilizing Android `ToneGenerator` / audio system for low-latency sound effects on scratch, win, and purchase.

---

## 1. Overview & Core Concept

This update transforms ScratchMart into a comprehensive shopping and rewards experience. Shoppers can save wishlisted products, type secret promo codes at checkout, track the live delivery progress of their orders with visual transit checkpoints, scratch cards using vibrant Holographic, Gold, and Neon metallic foil styles, and experience tactile sound effects on every gesture.

---

## 2. User Experience & Visual Design

```
┌────────────────────────────────────────────────────────┐
│                      SCRATCHMART                       │
│  [🔍 Search...]  [❤️ Wishlist: 3]  [🔊 Sound: ON]  [🛒]│
├────────────────────────────────────────────────────────┤
│  CATEGORY CHIPS: [All] [❤️ Wishlist] [Audio] [Tech]... │
│  ┌─────────────────────────┐  ┌──────────────────────┐ │
│  │ CyberChronos Watch  ❤️  │  │ NovaMech RGB     ❤️  │ │
│  │ $149.99  ⭐ 4.9         │  │ $74.50  ⭐ 4.7       │ │
│  │ [Add to Cart]           │  │ [Add to Cart]        │ │
│  └─────────────────────────┘  └──────────────────────┘ │
├────────────────────────────────────────────────────────┤
│  ORDER TRACKING MODAL:                                 │
│  ORD-83921 • Estimated Delivery Today                 │
│  ● Placed ➔ ● Packed ➔ ● In Transit ➔ ○ Delivered     │
├────────────────────────────────────────────────────────┤
│  SCRATCH FOIL SELECTOR: [Holographic] [Gold] [Neon]   │
└────────────────────────────────────────────────────────┘
```

### Visual & Audio Styling
- **Foil Aesthetics**:
  - *Holographic Prism*: Dynamic rainbow angular sweep gradient with shimmer particles.
  - *Burnished Gold*: Deep amber and 24k gold leaf texture with metallic light reflection.
  - *Cyber Neon*: Electric cyan and ultraviolet neon circuitry under glowing foil.
- **Audio Feedback**:
  - *Scratch Tone*: High-frequency subtle brush tone on drag gestures.
  - *Reveal Fanfare*: Ascending celebration arpeggio when prize reaches 45%+ threshold.
  - *Checkout Chime*: Cash register success tone on purchase completion.
  - *Sound Toggle*: Quick mute button in the top bar.

---

## 3. Key Product Decisions & Trade-Offs

| Decision | Chosen Approach | Rationale | Alternatives Considered |
| :--- | :--- | :--- | :--- |
| **Audio Engine** | Android `ToneGenerator` + synthesized audio tones | Ultra-lightweight, zero external sound asset dependencies, zero file loading latency. | Bundled MP3 assets (increases APK size, risk of missing codec support). |
| **Wishlist Persistence** | Room Entity `wishlist_items` + reactive Flow | Retains favorites across app launches without blocking the UI thread. | In-memory only state (loses favorites on app restart). |
| **Order Tracking** | Deterministic multi-stage status model based on order timestamp | Instant realistic simulation without requiring complex external delivery API keys. | Mock external webhook (unreliable offline). |

---

## 4. Technical Architecture & Component Flow

```
┌──────────────────────────────────────────────────────────────────┐
│                         PRESENTATION                             │
│  MainActivity ─── Scaffold                                       │
│   ├── TopBar (Wishlist Counter, Sound Toggle, Cards & Cart)      │
│   ├── ShopScreen (Wishlist Filter Chip, Heart Buttons)           │
│   ├── ScratchCardCanvas (Holographic / Gold / Neon Multi-Foil)   │
│   ├── CartSheet (Manual Promo Code Input + Won Coupon Picker)    │
│   └── OrdersScreen ─── OrderTrackingSheet (Timeline Visualizer)  │
└─────────────────────────────────┬────────────────────────────────┘
                                  │
┌─────────────────────────────────▼────────────────────────────────┐
│                       BUSINESS LOGIC                             │
│  StoreViewModel                                                  │
│   ├── WishlistManager (toggle, query, check)                     │
│   ├── PromoCodeValidator (manual codes: LUCKY2026, CYBERDROP)    │
│   ├── OrderTimelineEngine (step calculation, carrier info)       │
│   └── SoundManager (ToneGenerator audio feedback)                │
└─────────────────────────────────┬────────────────────────────────┘
                                  │
┌─────────────────────────────────▼────────────────────────────────┐
│                         PERSISTENCE                              │
│  AppDatabase (Room)                                              │
│   ├── ProductDao & WishlistDao                                   │
│   ├── ScratchCardDao & CouponDao                                 │
│   └── OrderDao (with tracking status & delivery stages)          │
└──────────────────────────────────────────────────────────────────┘
```

### Key Sequences
1. **Hearting Product**: Tapping heart button toggles wishlist in Room, triggers a spring scale bounce, updates the top bar wishlist badge, and filters view when "❤️ Wishlist" category is selected.
2. **Promo Code Redemption**: Cart displays "Enter Promo Code". Submitting `LUCKY2026` or `FREESHIP` immediately applies discount, updates the subtotal, and plays success audio.
3. **Live Order Tracking**: Tapping "Track Order" on any order receipt launches a bottom sheet timeline with progress indicator, carrier details, and real-time transit updates.
4. **Enhanced Scratch Experience**: Scratch cards render with metallic shaders, responsive audio scratching sound, and celebratory arpeggio chime on reveal.

# NGO Medicine Donation & Community Karma Rewards Hub

Transform the consumer product selling feature into a socially impactful **"Donate to Nearby NGOs"** workflow, allowing users to donate surplus, unexpired, and sealed medicines to vetted healthcare charities, free clinics, and orphanages, while awarding tiered **Eco-Karma Points** and **Scratch Card Rewards**.

---

## User Review Required

> [!IMPORTANT]
> The consumer C2C "Sell" functionality is being completely replaced by **Medicine Donation to Verified NGOs**. Users will no longer list products for personal cash payout; instead, every donation contributes to local charitable care and awards donor Karma Points + instant gift Scratch Cards.

1. **Nearby NGOs Roster**: Default vetted local NGOs in Mangaluru (Snehalaya Charitable Care, Rotary Free Dispensary, Karuna Elder Care Clinic, Prerana Pediatric Aid) with live distance indicators (1.2 km - 4.5 km) and urgent medicine requirements.
2. **Rewards Formula**:
   - Small donation (1-2 strips/bottles): **+100 Karma Points** + 1 Scratch Card.
   - Standard donation (3-5 strips/bottles): **+200 Karma Points** + 1 Scratch Card.
   - Bulk donation (6+ strips/bottles): **+350 Karma Points** + 2 Scratch Cards.
3. **Navigation & Tab Labels**: Bottom navigation tab 3 updated from "Sell & Return" to **"Donate & Return"** (`DONATE_RETURN`), with heart-hand / volunteer activism icon (`Icons.Default.VolunteerActivism`).

---

## Proposed Changes

### Data Layer & Persistence

#### `app/src/main/java/com/example/data/model/Models.kt`
- Replace `UserProductListing` or add `MedicineDonation` entity:
  - `id: String`
  - `medicineName: String`
  - `brandName: String`
  - `dosageOrUnits: String` (e.g., "2 Strips (20 Tabs)", "100ml Unopened Syrup")
  - `batchNumber: String`
  - `expiryDate: String`
  - `recipientNgoName: String`
  - `recipientNgoLocation: String`
  - `category: String`
  - `karmaPointsEarned: Int`
  - `packagePhotoUrl: String?`
  - `status: String` ("PLEDGED", "PICKUP_SCHEDULED", "VERIFIED_DELIVERED")
  - `createdAt: Long`
- Create `NgoPartner` data model:
  - `id: String`, `name: String`, `tagline: String`, `distanceKm: Double`, `urgencyBadge: String`, `neededCategories: List<String>`, `avatarColorHex: String`

#### `app/src/main/java/com/example/data/local/Daos.kt` & `AppDatabase.kt`
- Add `MedicineDonationDao` with queries for:
  - `getAllDonations(): Flow<List<MedicineDonation>>`
  - `insertDonation(donation: MedicineDonation)`
  - `updateDonationStatus(id: String, status: String)`
  - `deleteDonation(id: String)`
- Update `AppDatabase` entities and increment database version (with `fallbackToDestructiveMigration`).

#### `app/src/main/java/com/example/data/repository/StoreRepository.kt`
- Seed initial NGO partners:
  - **Snehalaya Charitable Trust & Healthcare** (1.4 km • Urgent: Fever, Antibiotics)
  - **Rotary Community Free Dispensary** (2.8 km • Urgent: Diabetes, Hypertension)
  - **Karuna Elder Care & Palliative Clinic** (3.5 km • Urgent: Pain Relief, Vitamins)
  - **Prerana Child & Pediatric Welfare** (4.1 km • Urgent: Syrups, Multivitamins)
- Add donation flows and helper functions:
  - `createMedicineDonation(...)`: stores donation record, awards calculated Karma Points to donor, unlocks a scratch card in `scratchCardsDao`, and logs reverse courier handoff.
  - `simulateVerifyDonation(id: String)`: marks donation verified by receiving NGO.

---

### UI & ViewModel Layer

#### `app/src/main/java/com/example/ui/viewmodel/StoreViewModel.kt`
- Expose `donations: StateFlow<List<MedicineDonation>>`.
- Add `createMedicineDonation(name, brand, units, batch, expiry, ngo, photoUrl, onComplete)`:
  - Computes Karma points based on volume.
  - Awards points to `_ecoKarmaPoints`.
  - Automatically generates and inserts a new unlocked `ScratchCard` reward into the user's Rewards wallet.
  - Plays celebratory sound.
- Add `simulateNgoVerification(donationId)`.

#### `app/src/main/java/com/example/ui/components/NavigationBars.kt`
- Update `AppDestination.SELL_RETURN` to `AppDestination.DONATE_RETURN`.
- Label: `"Donate & Return"`.
- Icon: `Icons.Default.VolunteerActivism` (filled / outlined).
- Badge count: Active pledged donations awaiting NGO delivery.

#### `app/src/main/java/com/example/ui/screens/DonateAndReturnScreen.kt` (replaces `SellAndReturnScreen.kt`)
- **Header**: Community Medicine Donation Drive banner with total lives impacted and karma earned.
- **Top Tabs**:
  1. **"Donate to NGO"**:
     - NGO Selector Carousel / Cards showing local NGO distance, trust rating, and urgently needed medicine categories.
     - Medicine Details form: Medicine name, brand, dosage/pack size, batch number, expiry date.
     - Package Condition selector: Factory sealed blister / unopened box.
     - Live Camera Photo snapshot with verification badge.
     - **Dynamic Reward Preview**: Shows exact Karma Points and Scratch Card to be awarded upon submission.
     - Action button: `"Pledge Donation & Claim +200 Karma Points"`.
  2. **"My Donations"**:
     - List of past and active medicine donations with status timeline (Pledged → Pickup → Delivered to NGO).
     - NGO impact appreciation note with donor certificate badge.
  3. **"Safe Disposal"**:
     - Reverse logistics pickup for expired medicine safe incineration (prevents groundwater pollution).

#### `app/src/main/java/com/example/ui/components/EvaluatorPresenterBar.kt` & `MainActivity.kt`
- Update top Evaluator Presenter Bar button from "Simulate Sale Approval" to "Simulate NGO Verified".
- Connect `AppDestination.DONATE_RETURN` to `DonateAndReturnScreen`.

---

## Verification Plan

### Automated Compilation Check
- Run `compile_applet` to ensure zero compilation or type errors.

### Manual CUJ Verification
1. Open the "Donate & Return" tab from the bottom navigation.
2. Verify nearby NGOs are shown (Snehalaya, Rotary, Karuna, Prerana) with distances and urgent tags.
3. Fill out the donation form (e.g. Paracetamol / Dolo 650 or Multivitamins), select NGO, upload/confirm photo.
4. Tap "Pledge Donation" -> Verify:
   - Success sound and celebration modal trigger.
   - Eco-Karma points increase immediately.
   - A new Scratch Card appears in the "Rewards" tab.
   - The donation appears under "My Donations" tab with real-time status tracking.

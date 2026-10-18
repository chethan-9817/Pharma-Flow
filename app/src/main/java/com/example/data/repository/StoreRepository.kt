package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.Medicine
import com.example.data.model.Order
import com.example.data.model.ReversePickupRequest
import com.example.data.model.ScratchCard
import com.example.data.model.UserProductListing
import com.example.data.model.WishlistItem
import com.example.data.remote.SupabaseClient
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import kotlin.random.Random

class StoreRepository(private val database: AppDatabase) {

    val allMedicines: Flow<List<Medicine>> = database.medicineDao().getAllMedicines()
    val nearExpiryMedicines: Flow<List<Medicine>> = database.medicineDao().getNearExpiryMedicines()
    val allOrders: Flow<List<Order>> = database.orderDao().getAllOrders()
    val latestOrder: Flow<Order?> = database.orderDao().getLatestOrder()
    val reversePickups: Flow<List<ReversePickupRequest>> = database.reversePickupDao().getAllPickups()
    val allScratchCards: Flow<List<ScratchCard>> = database.scratchCardDao().getAllScratchCards()
    val unscratchedCardCount: Flow<Int> = database.scratchCardDao().getUnscratchedCount()
    val wishlistIds: Flow<List<String>> = database.wishlistDao().getAllWishlistIds()
    val userListings: Flow<List<UserProductListing>> = database.userProductListingDao().getAllListings()

    suspend fun initializeDatabase() {
        if (database.medicineDao().getMedicineCount() == 0) {
            database.medicineDao().insertMedicines(getSeedMedicines())

            // Seed initial scratch cards
            database.scratchCardDao().insertCards(
                listOf(
                    ScratchCard(
                        id = "card_1",
                        title = "MedWallet Cashback Scratcher",
                        subtitle = "Scratch to claim ₹50 Instant Cashback",
                        prizeType = "CASHBACK",
                        prizeValue = 50.0,
                        couponCode = "CASHBACK50"
                    ),
                    ScratchCard(
                        id = "card_2",
                        title = "Eco-Karma Green Voucher",
                        subtitle = "Scratch to unlock 100 Eco-Karma Points",
                        prizeType = "ECO_POINTS",
                        prizeValue = 100.0,
                        couponCode = "ECOKARMA100"
                    ),
                    ScratchCard(
                        id = "card_3",
                        title = "Super Saver Pharmacy Coupon",
                        subtitle = "Flat 25% Off Next Medicine Order",
                        prizeType = "DISCOUNT_PERCENT",
                        prizeValue = 25.0,
                        couponCode = "MED25"
                    )
                )
            )

            // Seed initial active order for evaluator testing
            val seedOrder = Order(
                id = "order_mang_101",
                orderNumber = "MED-9402",
                customerName = "Chethan K.",
                items = "1x Dolo 650, 1x Becosules Z",
                totalAmount = 80.0,
                ecoSavings = 16.0,
                status = "ready_for_pickup",
                currentStage = 2,
                riderName = "Ramesh K.",
                riderPhone = "+91 98765 43210",
                otp = "4829",
                createdAt = System.currentTimeMillis() - 600000L
            )
            database.orderDao().insertOrder(seedOrder)
            database.wishlistDao().addToWishlist(WishlistItem("med_1"))

            // Seed initial verified user listings for Sell & Return circular hub
            database.userProductListingDao().insertListing(
                UserProductListing(
                    id = "listing_demo_1",
                    productName = "Minimalist SPF 50 Sunscreen (50g)",
                    brandName = "Minimalist",
                    category = "Skincare & Sunscreen",
                    dosageOrVolume = "50g Fluid Gel",
                    batchNumber = "MNL-SN2024",
                    expiryDate = "11/2027",
                    originalMrp = 399.0,
                    listingPrice = 280.0,
                    packageCondition = "Factory Sealed Box",
                    verificationStatus = "APPROVED",
                    sellerName = "Chethan K.",
                    pickupAddress = "Flat 402, Kadri Hills, Mangaluru",
                    estimatedPayout = 252.0
                )
            )
            database.userProductListingDao().insertListing(
                UserProductListing(
                    id = "listing_demo_2",
                    productName = "Augmentin 625 Duo (10 Tablets)",
                    brandName = "GlaxoSmithKline",
                    category = "Tablets",
                    dosageOrVolume = "10 Tablets Strip",
                    batchNumber = "GSK-AG882",
                    expiryDate = "08/2026",
                    originalMrp = 205.0,
                    listingPrice = 140.0,
                    packageCondition = "Intact Sealed Blister",
                    verificationStatus = "PENDING_REVIEW",
                    sellerName = "Chethan K.",
                    pickupAddress = "Flat 402, Kadri Hills, Mangaluru",
                    estimatedPayout = 126.0
                )
            )
        }
    }

    suspend fun createUserListing(
        productName: String,
        brandName: String,
        category: String,
        dosageOrVolume: String,
        batchNumber: String,
        expiryDate: String,
        originalMrp: Double,
        listingPrice: Double,
        packageCondition: String,
        photoUrl: String? = null
    ): UserProductListing {
        val listing = UserProductListing(
            id = UUID.randomUUID().toString(),
            productName = productName,
            brandName = brandName,
            category = category,
            dosageOrVolume = dosageOrVolume,
            batchNumber = batchNumber,
            expiryDate = expiryDate,
            originalMrp = originalMrp,
            listingPrice = listingPrice,
            packageCondition = packageCondition,
            photoUrl = photoUrl,
            verificationStatus = "PENDING_REVIEW",
            sellerName = "Chethan K.",
            pickupAddress = "Flat 402, Kadri Hills, Mangaluru",
            estimatedPayout = listingPrice * 0.90,
            createdAt = System.currentTimeMillis()
        )
        database.userProductListingDao().insertListing(listing)
        return listing
    }

    suspend fun approveUserListing(id: String) {
        database.userProductListingDao().approveListing(id)
    }

    suspend fun deleteUserListing(id: String) {
        database.userProductListingDao().deleteListing(id)
    }

    suspend fun toggleWishlist(medicineId: String, isCurrentlyWishlisted: Boolean) {
        if (isCurrentlyWishlisted) {
            database.wishlistDao().removeFromWishlist(medicineId)
        } else {
            database.wishlistDao().addToWishlist(WishlistItem(medicineId))
        }
    }

    suspend fun markCardScratched(card: ScratchCard) {
        val updated = card.copy(isScratched = true, scratchedPercentage = 100f)
        database.scratchCardDao().updateCard(updated)
    }

    suspend fun addScratchCard(title: String, subtitle: String, prizeType: String, prizeValue: Double, code: String): ScratchCard {
        val card = ScratchCard(
            id = UUID.randomUUID().toString(),
            title = title,
            subtitle = subtitle,
            prizeType = prizeType,
            prizeValue = prizeValue,
            couponCode = code,
            isScratched = false,
            scratchedPercentage = 0f
        )
        database.scratchCardDao().insertCard(card)
        return card
    }

    suspend fun placeOrder(
        items: String,
        totalAmount: Double,
        ecoSavings: Double,
        prescriptionUrl: String?
    ): Order {
        val order = Order(
            id = UUID.randomUUID().toString(),
            orderNumber = "MED-${Random.nextInt(1000, 9999)}",
            customerName = "Chethan K.",
            items = items,
            totalAmount = totalAmount,
            ecoSavings = ecoSavings,
            prescriptionUrl = prescriptionUrl,
            status = "pending",
            riderName = "Ramesh K.",
            riderPhone = "+91 98765 43210",
            otp = "${Random.nextInt(1000, 9999)}",
            currentStage = 1,
            createdAt = System.currentTimeMillis()
        )

        database.orderDao().insertOrder(order)
        SupabaseClient.insertOrderToSupabase(order)

        // Award reward scratch card
        addScratchCard(
            title = "Order Reward Scratcher",
            subtitle = "Earned from #${order.orderNumber}! Reveal cashback or points",
            prizeType = if (Random.nextBoolean()) "CASHBACK" else "ECO_POINTS",
            prizeValue = if (Random.nextBoolean()) 35.0 else 75.0,
            code = "REWARD${Random.nextInt(100, 999)}"
        )

        return order
    }

    suspend fun updateOrderStatus(orderId: String, status: String, stage: Int) {
        database.orderDao().updateOrderStatus(orderId, status, stage)
    }

    suspend fun scheduleReverseDisposal(
        stripCount: Int,
        medicineNames: String,
        address: String,
        slot: String
    ): ReversePickupRequest {
        val request = ReversePickupRequest(
            id = UUID.randomUUID().toString(),
            stripCount = stripCount,
            medicineNames = medicineNames,
            pickupAddress = address,
            pickupSlot = slot,
            earnedKarmaPoints = 50,
            status = "Scheduled"
        )
        database.reversePickupDao().insertPickup(request)

        // Award bonus scratch card for reverse logistics disposal
        addScratchCard(
            title = "Eco-Karma Green Scratcher",
            subtitle = "Safe medicine return reward! +50 Karma Points",
            prizeType = "ECO_POINTS",
            prizeValue = 50.0,
            code = "GREEN${Random.nextInt(100, 999)}"
        )

        return request
    }

    private fun getSeedMedicines(): List<Medicine> {
        return listOf(
            Medicine(
                id = "med_1",
                brandName = "Dolo 650",
                genericSalt = "Paracetamol 650mg",
                manufacturer = "Micro Labs Ltd.",
                price = 32.00,
                mrp = 34.00,
                stock = 150,
                requiresRx = false,
                isNearExpiry = false,
                dosageForm = "Tablet",
                category = "Fever & Pain"
            ),
            Medicine(
                id = "med_2",
                brandName = "Augmentin 625 Duo",
                genericSalt = "Amoxicillin (500mg) + Clavulanic Acid (125mg)",
                manufacturer = "GlaxoSmithKline Pharmaceuticals",
                price = 185.00,
                mrp = 205.00,
                stock = 45,
                requiresRx = true,
                isNearExpiry = false,
                dosageForm = "Tablet",
                category = "Antibiotics"
            ),
            Medicine(
                id = "med_3",
                brandName = "Azithral 500",
                genericSalt = "Azithromycin 500mg",
                manufacturer = "Alembic Pharmaceuticals",
                price = 115.00,
                mrp = 132.00,
                stock = 60,
                requiresRx = true,
                isNearExpiry = false,
                dosageForm = "Tablet",
                category = "Antibiotics"
            ),
            Medicine(
                id = "med_4",
                brandName = "Becosules Z",
                genericSalt = "B-Complex Forte + Vitamin C + Zinc",
                manufacturer = "Pfizer Ltd.",
                price = 48.00,
                mrp = 55.00,
                stock = 180,
                requiresRx = false,
                isNearExpiry = false,
                dosageForm = "Capsule",
                category = "Vitamins"
            ),
            Medicine(
                id = "med_5",
                brandName = "Glycomet GP 1",
                genericSalt = "Glimepiride 1mg + Metformin 500mg",
                manufacturer = "USV Pvt. Ltd.",
                price = 124.00,
                mrp = 145.00,
                stock = 75,
                requiresRx = true,
                isNearExpiry = false,
                dosageForm = "Tablet",
                category = "Diabetes"
            ),
            Medicine(
                id = "med_6",
                brandName = "Pan-D",
                genericSalt = "Pantoprazole 40mg + Domperidone 30mg",
                manufacturer = "Alkem Laboratories",
                price = 142.00,
                mrp = 165.00,
                stock = 90,
                requiresRx = true,
                isNearExpiry = false,
                dosageForm = "Capsule",
                category = "Gastric"
            ),
            Medicine(
                id = "med_7",
                brandName = "Allegra 120mg",
                genericSalt = "Fexofenadine Hydrochloride 120mg",
                manufacturer = "Sanofi India Ltd.",
                price = 165.00,
                mrp = 190.00,
                stock = 40,
                requiresRx = false,
                isNearExpiry = false,
                dosageForm = "Tablet",
                category = "Fever & Pain"
            ),
            Medicine(
                id = "med_8",
                brandName = "Shelcal 500",
                genericSalt = "Calcium 500mg + Vitamin D3 250 IU",
                manufacturer = "Torrent Pharmaceuticals",
                price = 88.00,
                mrp = 105.00,
                stock = 120,
                requiresRx = false,
                isNearExpiry = false,
                dosageForm = "Tablet",
                category = "Vitamins"
            ),
            Medicine(
                id = "med_9",
                brandName = "Telma 40",
                genericSalt = "Telmisartan 40mg",
                manufacturer = "Glenmark Pharmaceuticals",
                price = 130.00,
                mrp = 152.00,
                stock = 85,
                requiresRx = true,
                isNearExpiry = false,
                dosageForm = "Tablet",
                category = "Cardio"
            ),
            Medicine(
                id = "med_10",
                brandName = "Volini Pain Relief Gel",
                genericSalt = "Diclofenac Diethylamine + Linseed Oil",
                manufacturer = "Sun Pharma",
                price = 75.00,
                mrp = 90.00,
                stock = 65,
                requiresRx = false,
                isNearExpiry = false,
                dosageForm = "Gel",
                category = "First Aid"
            ),
            Medicine(
                id = "med_11",
                brandName = "Betadine 10% Ointment",
                genericSalt = "Povidone Iodine 10% w/w",
                manufacturer = "Win-Medicare Ltd.",
                price = 65.00,
                mrp = 78.00,
                stock = 110,
                requiresRx = false,
                isNearExpiry = false,
                dosageForm = "Ointment",
                category = "First Aid"
            ),
            Medicine(
                id = "med_12",
                brandName = "Neurobion Forte",
                genericSalt = "Vitamin B-Complex + B12",
                manufacturer = "Procter & Gamble Hygiene",
                price = 38.00,
                mrp = 42.00,
                stock = 200,
                requiresRx = false,
                isNearExpiry = false,
                dosageForm = "Tablet",
                category = "Vitamins"
            ),
            // Premium Dermatological Sunscreens
            Medicine(
                id = "sun_1",
                brandName = "The Derma Co 1% Hyaluronic Sunscreen Aqua Gel",
                genericSalt = "Hyaluronic Acid 1% + Vitamin E (SPF 50 PA++++)",
                manufacturer = "Honasa Consumer Ltd. • Dermatologist Tested",
                price = 449.00,
                mrp = 499.00,
                stock = 75,
                requiresRx = false,
                isNearExpiry = false,
                dosageForm = "Aqua Gel",
                category = "Skincare & Sunscreen",
                batchNumber = "TDC-HG2024"
            ),
            Medicine(
                id = "sun_2",
                brandName = "Minimalist Sunscreen SPF 50 Multi-Vitamin",
                genericSalt = "Niacinamide + Vitamin B5 + Vitamin F (Broad Spectrum PA++++)",
                manufacturer = "Uprising Science Pvt. Ltd. • Non-Comedogenic",
                price = 379.00,
                mrp = 399.00,
                stock = 90,
                requiresRx = false,
                isNearExpiry = false,
                dosageForm = "Fluid Gel",
                category = "Skincare & Sunscreen",
                batchNumber = "MNL-MV50"
            ),
            Medicine(
                id = "sun_3",
                brandName = "Dermatouch Undamaged Ultra Matte Silicone Sunscreen",
                genericSalt = "Zinc Oxide + Titanium Dioxide (SPF 50 PA+++ Matte Finish)",
                manufacturer = "Dermatouch Skin Science • Sweat & Water Resistant",
                price = 465.00,
                mrp = 499.00,
                stock = 50,
                requiresRx = false,
                isNearExpiry = false,
                dosageForm = "Silicone Gel",
                category = "Skincare & Sunscreen",
                batchNumber = "DMT-MS50"
            ),
            // High-Demand Clinical Tablets
            Medicine(
                id = "tab_1",
                brandName = "Dolo 650",
                genericSalt = "Paracetamol (Acetaminophen) 650mg",
                manufacturer = "Micro Labs Ltd.",
                price = 30.00,
                mrp = 34.00,
                stock = 250,
                requiresRx = false,
                isNearExpiry = false,
                dosageForm = "Tablet",
                category = "Fever & Pain",
                batchNumber = "DLO-650"
            ),
            Medicine(
                id = "tab_2",
                brandName = "Augmentin 625 Duo",
                genericSalt = "Amoxycillin 500mg + Potassium Clavulanate 125mg",
                manufacturer = "GlaxoSmithKline Pharmaceuticals",
                price = 185.00,
                mrp = 205.00,
                stock = 80,
                requiresRx = true,
                isNearExpiry = false,
                dosageForm = "Tablet",
                category = "Antibiotics",
                batchNumber = "AUG-625"
            ),
            Medicine(
                id = "tab_3",
                brandName = "Montair-LC",
                genericSalt = "Montelukast Sodium 10mg + Levocetirizine 5mg",
                manufacturer = "Cipla Ltd.",
                price = 192.00,
                mrp = 210.00,
                stock = 110,
                requiresRx = true,
                isNearExpiry = false,
                dosageForm = "Tablet",
                category = "Respiratory",
                batchNumber = "MLC-105"
            ),
            Medicine(
                id = "tab_4",
                brandName = "Thyronorm 50mcg",
                genericSalt = "Thyroxine Sodium 50mcg",
                manufacturer = "Abbott India Ltd.",
                price = 158.00,
                mrp = 175.00,
                stock = 95,
                requiresRx = true,
                isNearExpiry = false,
                dosageForm = "Tablet",
                category = "Diabetes",
                batchNumber = "THY-50"
            ),
            Medicine(
                id = "tab_5",
                brandName = "Zincovit",
                genericSalt = "Multivitamins + Multiminerals + Grape Seed Extract",
                manufacturer = "Apex Laboratories Pvt. Ltd.",
                price = 102.00,
                mrp = 115.00,
                stock = 140,
                requiresRx = false,
                isNearExpiry = false,
                dosageForm = "Tablet",
                category = "Vitamins",
                batchNumber = "ZNC-100"
            ),
            // Near-Expiry 50% Eco-Clearance Batches
            Medicine(
                id = "eco_1",
                brandName = "Amoxicillin 250mg (Eco-Clearance)",
                genericSalt = "Amoxicillin Trihydrate 250mg",
                manufacturer = "Cipla Ltd. • BATCH-EXP22",
                price = 45.00,
                mrp = 90.00, // 50% discount
                stock = 18,
                requiresRx = true,
                isNearExpiry = true,
                expiryDaysLeft = 22,
                dosageForm = "Capsule",
                category = "Antibiotics",
                wastePreventedGrams = 42
            ),
            Medicine(
                id = "eco_2",
                brandName = "Celin 500 Chewable (Eco-Clearance)",
                genericSalt = "Vitamin C (Ascorbic Acid) 500mg",
                manufacturer = "GlaxoSmithKline • BATCH-EXP18",
                price = 19.00,
                mrp = 38.00, // 50% discount
                stock = 25,
                requiresRx = false,
                isNearExpiry = true,
                expiryDaysLeft = 18,
                dosageForm = "Tablet",
                category = "Vitamins",
                wastePreventedGrams = 30
            ),
            Medicine(
                id = "eco_3",
                brandName = "Metformin 500 SR (Eco-Clearance)",
                genericSalt = "Metformin Hydrochloride 500mg",
                manufacturer = "Sun Pharma • BATCH-EXP26",
                price = 28.00,
                mrp = 56.00, // 50% discount
                stock = 14,
                requiresRx = true,
                isNearExpiry = true,
                expiryDaysLeft = 26,
                dosageForm = "Tablet",
                category = "Diabetes",
                wastePreventedGrams = 55
            )
        )
    }
}

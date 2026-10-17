package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.CardTier
import com.example.data.model.Coupon
import com.example.data.model.DiscountType
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.data.model.Quest
import com.example.data.model.ScratchCard
import com.example.data.model.WishlistItem
import com.example.data.remote.SupabaseClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID
import kotlin.random.Random

class StoreRepository(private val database: AppDatabase) {

    val allProducts: Flow<List<Product>> = database.productDao().getAllProducts()
    val allScratchCards: Flow<List<ScratchCard>> = database.scratchCardDao().getAllScratchCards()
    val unscratchedCardCount: Flow<Int> = database.scratchCardDao().getUnscratchedCount()
    val allCoupons: Flow<List<Coupon>> = database.couponDao().getAllCoupons()
    val availableCoupons: Flow<List<Coupon>> = database.couponDao().getAvailableCoupons()
    val allOrders: Flow<List<Order>> = database.orderDao().getAllOrders()
    val wishlistIds: Flow<List<String>> = database.wishlistDao().getAllWishlistIds()
    val wishlistCount: Flow<Int> = database.wishlistDao().getWishlistCount()

    private val _quests = MutableStateFlow(getInitialQuests())
    val quests: StateFlow<List<Quest>> = _quests.asStateFlow()

    suspend fun initializeDatabase() {
        if (database.productDao().getProductCount() == 0) {
            // Try fetching from Supabase first
            val remoteProducts = SupabaseClient.fetchProductsFromSupabase()
            if (!remoteProducts.isNullOrEmpty()) {
                database.productDao().insertProducts(remoteProducts)
            } else {
                database.productDao().insertProducts(getSeedProducts())
            }

            // Seed initial welcome scratch cards
            database.scratchCardDao().insertCards(
                listOf(
                    createScratchCard(
                        tier = CardTier.GOLD,
                        title = "Golden Welcome Scratcher",
                        subtitle = "Scratch to unlock up to 35% OFF!",
                        type = DiscountType.PERCENTAGE,
                        value = 35.0,
                        code = "WELCOME35"
                    ),
                    createScratchCard(
                        tier = CardTier.SILVER,
                        title = "Silver Quick Saver",
                        subtitle = "Scratch to claim $15 flat discount",
                        type = DiscountType.FLAT_AMOUNT,
                        value = 15.0,
                        code = "SILVER15"
                    ),
                    createScratchCard(
                        tier = CardTier.DIAMOND,
                        title = "Jackpot VIP Mystery",
                        subtitle = "Mega 50% OFF your entire tech haul!",
                        type = DiscountType.PERCENTAGE,
                        value = 50.0,
                        code = "JACKPOT50"
                    )
                )
            )

            // Seed initial sample order so tracking screen is immediately functional
            database.orderDao().insertOrder(
                Order(
                    id = "seed_order_1",
                    orderNumber = "ORD-84920",
                    itemsSummary = "1x AeroPulse Pro ANC Earbuds, 1x SpectraBeam MagSafe Power Bank",
                    subtotal = 129.98,
                    discount = 35.0,
                    total = 94.98,
                    appliedCouponCode = "WELCOME35",
                    rewardCardTier = CardTier.GOLD,
                    timestamp = System.currentTimeMillis() - 86400000L,
                    status = "Out for Delivery",
                    currentStage = 3,
                    carrier = "CyberDrop Express",
                    trackingCode = "CDX-77491"
                )
            )

            // Seed 1 wishlisted product
            database.wishlistDao().addToWishlist(WishlistItem("prod_2"))
        }
    }

    suspend fun toggleWishlist(productId: String, isCurrentlyWishlisted: Boolean) {
        if (isCurrentlyWishlisted) {
            database.wishlistDao().removeFromWishlist(productId)
        } else {
            database.wishlistDao().addToWishlist(WishlistItem(productId))
        }
    }

    suspend fun markCardScratched(card: ScratchCard) {
        val updated = card.copy(isScratched = true, scratchedPercentage = 100f)
        database.scratchCardDao().updateCard(updated)

        // Generate and add corresponding coupon to wallet
        val couponDesc = when (card.discountType) {
            DiscountType.PERCENTAGE -> "${card.discountValue.toInt()}% OFF any order"
            DiscountType.FLAT_AMOUNT -> "$${card.discountValue.toInt()} OFF purchase"
            DiscountType.FREE_SHIPPING -> "Free Express Priority Shipping"
        }
        val coupon = Coupon(
            code = card.couponCode,
            description = couponDesc,
            discountType = card.discountType,
            discountValue = card.discountValue,
            minOrder = if (card.tier == CardTier.DIAMOND) 60.0 else 25.0
        )
        database.couponDao().insertCoupon(coupon)

        // Increment quest progress for scratching
        updateQuestProgress("quest_scratch_first", 1)
    }

    suspend fun createAndAwardScratchCard(tier: CardTier, reason: String): ScratchCard {
        val discountValue = when (tier) {
            CardTier.SILVER -> Random.nextInt(10, 25).toDouble()
            CardTier.GOLD -> Random.nextInt(25, 45).toDouble()
            CardTier.DIAMOND -> Random.nextInt(50, 75).toDouble()
        }
        val code = "${tier.name.take(3)}${Random.nextInt(100, 999)}"
        val card = createScratchCard(
            tier = tier,
            title = "${tier.displayName} Card",
            subtitle = "Earned from $reason! Up to ${discountValue.toInt()}% savings",
            type = DiscountType.PERCENTAGE,
            value = discountValue,
            code = code
        )
        database.scratchCardDao().insertCard(card)
        return card
    }

    suspend fun placeOrder(
        itemsSummary: String,
        subtotal: Double,
        discount: Double,
        total: Double,
        appliedCoupon: Coupon?
    ): Order {
        val rewardTier = when {
            total >= 150.0 -> CardTier.DIAMOND
            total >= 60.0 -> CardTier.GOLD
            else -> CardTier.SILVER
        }

        val order = Order(
            id = UUID.randomUUID().toString(),
            orderNumber = "ORD-${Random.nextInt(10000, 99999)}",
            itemsSummary = itemsSummary,
            subtotal = subtotal,
            discount = discount,
            total = total,
            appliedCouponCode = appliedCoupon?.code,
            rewardCardTier = rewardTier,
            status = "Processing",
            currentStage = 1,
            carrier = "CyberDrop Priority",
            trackingCode = "CDX-${Random.nextInt(10000, 99999)}"
        )

        database.orderDao().insertOrder(order)

        if (appliedCoupon != null) {
            database.couponDao().updateCoupon(appliedCoupon.copy(isUsed = true))
        }

        // Award reward scratch card for placing order
        createAndAwardScratchCard(rewardTier, "Order ${order.orderNumber}")

        // Update quest progress
        updateQuestProgress("quest_order_place", 1)
        if (total >= 50.0) {
            updateQuestProgress("quest_order_big", 1)
        }

        return order
    }

    fun updateQuestProgress(questId: String, increment: Int) {
        val currentList = _quests.value
        _quests.value = currentList.map { quest ->
            if (quest.id == questId && !quest.isClaimed) {
                val newProg = (quest.currentProgress + increment).coerceAtMost(quest.targetProgress)
                quest.copy(currentProgress = newProg)
            } else {
                quest
            }
        }
    }

    suspend fun claimQuest(questId: String): CardTier? {
        val currentList = _quests.value
        val quest = currentList.find { it.id == questId } ?: return null
        if (quest.isCompleted && !quest.isClaimed) {
            _quests.value = currentList.map {
                if (it.id == questId) it.copy(isClaimed = true) else it
            }
            createAndAwardScratchCard(quest.rewardTier, "Quest '${quest.title}'")
            return quest.rewardTier
        }
        return null
    }

    private fun createScratchCard(
        tier: CardTier,
        title: String,
        subtitle: String,
        type: DiscountType,
        value: Double,
        code: String
    ): ScratchCard {
        return ScratchCard(
            id = UUID.randomUUID().toString(),
            tier = tier,
            title = title,
            subtitle = subtitle,
            discountType = type,
            discountValue = value,
            couponCode = code,
            isScratched = false,
            scratchedPercentage = 0f
        )
    }

    private fun getInitialQuests(): List<Quest> {
        return listOf(
            Quest(
                id = "quest_scratch_first",
                title = "Fortune Hunter",
                description = "Scratch off your first mystery reward card",
                currentProgress = 0,
                targetProgress = 1,
                isClaimed = false,
                rewardTier = CardTier.GOLD
            ),
            Quest(
                id = "quest_browse_items",
                title = "Window Shopper",
                description = "Explore products in 3 different departments",
                currentProgress = 1,
                targetProgress = 3,
                isClaimed = false,
                rewardTier = CardTier.SILVER
            ),
            Quest(
                id = "quest_order_place",
                title = "Smart Shopper",
                description = "Complete any checkout purchase",
                currentProgress = 0,
                targetProgress = 1,
                isClaimed = false,
                rewardTier = CardTier.GOLD
            ),
            Quest(
                id = "quest_order_big",
                title = "High Roller",
                description = "Place a haul order of $50 or more",
                currentProgress = 0,
                targetProgress = 1,
                isClaimed = false,
                rewardTier = CardTier.DIAMOND
            )
        )
    }

    private fun getSeedProducts(): List<Product> {
        return listOf(
            Product(
                id = "prod_1",
                title = "AeroPulse Pro ANC Earbuds",
                description = "Studio-grade active noise cancellation with 48h spatial audio battery life.",
                category = "Audio",
                price = 89.99,
                originalPrice = 129.99,
                rating = 4.8f,
                reviewCount = 284,
                badge = "HOT",
                iconKey = "headphones"
            ),
            Product(
                id = "prod_2",
                title = "CyberChronos Smartwatch Ultra",
                description = "Titanium bezel, ECG biosensors, always-on AMOLED with 14-day stamina.",
                category = "Wearables",
                price = 149.99,
                originalPrice = 199.99,
                rating = 4.9f,
                reviewCount = 512,
                badge = "POPULAR",
                iconKey = "watch"
            ),
            Product(
                id = "prod_3",
                title = "NovaMech RGB 75% Keyboard",
                description = "Hot-swappable tactile switches, gasket mounted, wireless tri-mode connection.",
                category = "Gaming",
                price = 74.50,
                originalPrice = 99.00,
                rating = 4.7f,
                reviewCount = 189,
                badge = "TRENDING",
                iconKey = "keyboard"
            ),
            Product(
                id = "prod_4",
                title = "AeroFalcon 4K Mini Drone",
                description = "3-axis gimbal, obstacle avoidance, GPS return, ultra-compact foldable wings.",
                category = "Tech",
                price = 229.00,
                originalPrice = 299.99,
                rating = 4.6f,
                reviewCount = 94,
                badge = "FLASH DEAL",
                iconKey = "drone"
            ),
            Product(
                id = "prod_5",
                title = "RetroBit Pixel Handheld Arcade",
                description = "Over 10,000 classic retro titles on a crisp 3.5\" IPS screen with stereo sound.",
                category = "Gaming",
                price = 45.00,
                originalPrice = 65.00,
                rating = 4.8f,
                reviewCount = 430,
                badge = "BESTSELLER",
                iconKey = "gamepad"
            ),
            Product(
                id = "prod_6",
                title = "SpectraBeam MagSafe Power Bank",
                description = "10,000mAh with transparent cyber casing and dynamic wattage readout display.",
                category = "Tech",
                price = 39.99,
                originalPrice = 54.99,
                rating = 4.7f,
                reviewCount = 156,
                badge = "",
                iconKey = "battery"
            ),
            Product(
                id = "prod_7",
                title = "Vortex 4K Action Cam 60FPS",
                description = "Waterproof rugged housing, dual touchscreens, horizon lock stabilization.",
                category = "Tech",
                price = 119.00,
                originalPrice = 169.00,
                rating = 4.5f,
                reviewCount = 88,
                badge = "NEW",
                iconKey = "camera"
            ),
            Product(
                id = "prod_8",
                title = "NeonDrift Gaming Mouse Pro",
                description = "Ultralight 52g honeycomb chassis, 26,000 DPI optical sensor, PTFE skates.",
                category = "Gaming",
                price = 34.99,
                originalPrice = 49.99,
                rating = 4.6f,
                reviewCount = 220,
                badge = "",
                iconKey = "mouse"
            ),
            Product(
                id = "prod_9",
                title = "SonicBoom 360 Waterproof Speaker",
                description = "Deep punchy bass, party link broadcast mode, IPX7 floatable submersible build.",
                category = "Audio",
                price = 59.99,
                originalPrice = 79.99,
                rating = 4.7f,
                reviewCount = 310,
                badge = "DEAL",
                iconKey = "speaker"
            )
        )
    }
}

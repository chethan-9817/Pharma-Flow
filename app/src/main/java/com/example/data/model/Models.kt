package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val category: String,
    val price: Double,
    val originalPrice: Double,
    val rating: Float,
    val reviewCount: Int,
    val badge: String = "",
    val iconKey: String = "gadget"
)

enum class CardTier(val displayName: String, val minDiscount: Int, val maxDiscount: Int) {
    SILVER("Silver Mystery", 10, 20),
    GOLD("Gold Mystery", 25, 40),
    DIAMOND("Diamond Jackpot", 50, 70)
}

enum class FoilStyle(val displayName: String) {
    HOLOGRAPHIC("Holographic Prism"),
    GOLD_LEAF("24K Gold Leaf"),
    CYBER_NEON("Cyber Neon")
}

enum class DiscountType {
    PERCENTAGE,
    FLAT_AMOUNT,
    FREE_SHIPPING
}

@Entity(tableName = "scratch_cards")
data class ScratchCard(
    @PrimaryKey val id: String,
    val tier: CardTier,
    val title: String,
    val subtitle: String,
    val discountType: DiscountType,
    val discountValue: Double,
    val couponCode: String,
    val isScratched: Boolean = false,
    val scratchedPercentage: Float = 0f,
    val unlockedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "coupons")
data class Coupon(
    @PrimaryKey val code: String,
    val description: String,
    val discountType: DiscountType,
    val discountValue: Double,
    val minOrder: Double = 0.0,
    val isUsed: Boolean = false,
    val claimedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "wishlist")
data class WishlistItem(
    @PrimaryKey val productId: String,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey val id: String,
    val orderNumber: String,
    val itemsSummary: String,
    val subtotal: Double,
    val discount: Double,
    val total: Double,
    val appliedCouponCode: String? = null,
    val rewardCardTier: CardTier,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "In Transit",
    val currentStage: Int = 3, // 1: Confirmed, 2: Packed, 3: Out for Delivery, 4: Delivered
    val carrier: String = "CyberDrop Express",
    val trackingCode: String = "CDX-89324"
)

data class CartItem(
    val product: Product,
    val quantity: Int = 1
)

data class Quest(
    val id: String,
    val title: String,
    val description: String,
    val currentProgress: Int,
    val targetProgress: Int,
    val isClaimed: Boolean,
    val rewardTier: CardTier
) {
    val isCompleted: Boolean get() = currentProgress >= targetProgress
}

package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicines")
data class Medicine(
    @PrimaryKey val id: String,
    val brandName: String,
    val genericSalt: String,
    val manufacturer: String,
    val price: Double,
    val mrp: Double,
    val stock: Int,
    val requiresRx: Boolean = false,
    val isNearExpiry: Boolean = false,
    val expiryDaysLeft: Int = 365,
    val dosageForm: String = "Tablet",
    val category: String = "Fever & Pain",
    val batchNumber: String = "BATCH-A1",
    val wastePreventedGrams: Int = 0
)

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey val id: String,
    val orderNumber: String,
    val customerName: String = "Chethan K.",
    val items: String,
    val totalAmount: Double,
    val ecoSavings: Double = 0.0,
    val prescriptionUrl: String? = null,
    val status: String = "pending", // pending, ready_for_pickup, out_for_delivery, delivered
    val riderName: String = "Ramesh K.",
    val riderPhone: String = "+91 98765 43210",
    val otp: String = "4829",
    val currentStage: Int = 1, // 1: Placed, 2: Packed, 3: En Route, 4: Delivered
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "reverse_pickups")
data class ReversePickupRequest(
    @PrimaryKey val id: String,
    val stripCount: Int,
    val medicineNames: String,
    val pickupAddress: String,
    val pickupSlot: String,
    val earnedKarmaPoints: Int = 50,
    val status: String = "Scheduled",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "scratch_cards")
data class ScratchCard(
    @PrimaryKey val id: String,
    val title: String,
    val subtitle: String,
    val prizeType: String, // CASHBACK, ECO_POINTS, DISCOUNT_PERCENT, FREE_DELIVERY
    val prizeValue: Double,
    val couponCode: String,
    val isScratched: Boolean = false,
    val scratchedPercentage: Float = 0f,
    val unlockedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "wishlist")
data class WishlistItem(
    @PrimaryKey val medicineId: String,
    val addedAt: Long = System.currentTimeMillis()
)

data class CartItem(
    val medicine: Medicine,
    val quantity: Int = 1
)

data class PrescriptionScanResult(
    val isVerified: Boolean = false,
    val doctorName: String = "Dr. Rajesh Shenoy, MD",
    val clinicName: String = "City Multi-Specialty Clinic, Mangaluru",
    val date: String = "Oct 16, 2026",
    val extractedMedicines: List<String> = emptyList(),
    val verifiedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_listings")
data class UserProductListing(
    @PrimaryKey val id: String,
    val productName: String,
    val brandName: String,
    val category: String, // Skincare & Sunscreen, Tablets, General
    val dosageOrVolume: String,
    val batchNumber: String,
    val expiryDate: String,
    val originalMrp: Double,
    val listingPrice: Double,
    val packageCondition: String = "Factory Sealed Box",
    val photoUrl: String? = null,
    val verificationStatus: String = "PENDING_REVIEW", // PENDING_REVIEW, APPROVED, SOLD, REJECTED
    val sellerName: String = "Chethan K.",
    val pickupAddress: String = "Flat 402, Kadri Hills, Mangaluru",
    val estimatedPayout: Double = listingPrice * 0.90, // 90% direct payout
    val createdAt: Long = System.currentTimeMillis()
)

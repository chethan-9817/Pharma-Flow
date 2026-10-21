package com.example.data.firebase

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class FirebaseUserProfile(
    @DocumentId val userId: String = "",
    val email: String = "",
    val displayName: String = "",
    val karmaPoints: Long = 0L,
    val scratchCardsCount: Long = 0L,
    val createdAt: String = "",
    val updatedAt: String = ""
)

@IgnoreExtraProperties
data class FirebaseMedicineDonation(
    @DocumentId val id: String = "",
    val userId: String = "",
    val medicineName: String = "",
    val brandName: String = "",
    val batchNumber: String = "",
    val expiryDate: String = "",
    val quantity: String = "",
    val packageCondition: String = "",
    val ngoId: String = "",
    val ngoName: String = "",
    val status: String = "PENDING",
    val karmaPointsAwarded: Long = 0L,
    val scratchCardAwarded: Boolean = false,
    val createdAt: String = ""
)

@IgnoreExtraProperties
data class FirebaseUserListing(
    @DocumentId val id: String = "",
    val userId: String = "",
    val medicineName: String = "",
    val genericSalt: String = "",
    val batchNumber: String = "",
    val expiryDate: String = "",
    val packCondition: String = "",
    val askingPrice: Double = 0.0,
    val status: String = "ACTIVE",
    val imageUrl: String = "",
    val createdAt: String = ""
)

@IgnoreExtraProperties
data class FirebaseUserOrder(
    @DocumentId val id: String = "",
    val userId: String = "",
    val itemCount: Long = 0L,
    val subtotal: Double = 0.0,
    val discount: Double = 0.0,
    val total: Double = 0.0,
    val status: String = "PLACED",
    val createdAt: String = ""
)

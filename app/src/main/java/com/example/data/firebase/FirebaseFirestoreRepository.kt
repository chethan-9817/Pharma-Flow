package com.example.data.firebase

import android.content.Context
import com.example.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirebaseFirestoreRepository(
    private val db: FirebaseFirestore
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    // User Profile
    fun observeUserProfile(userId: String): Flow<FirebaseUserProfile?> {
        return db.collection("users").document(userId)
            .snapshots()
            .map { snapshot ->
                if (snapshot.exists()) {
                    snapshot.toObject(FirebaseUserProfile::class.java)
                } else {
                    null
                }
            }
    }

    suspend fun saveUserProfile(profile: FirebaseUserProfile) {
        require(profile.userId.isNotBlank()) { "userId must not be blank" }
        db.collection("users").document(profile.userId)
            .set(profile)
            .await()
    }

    // Medicine Donations to NGOs
    fun observeDonations(userId: String): Flow<List<FirebaseMedicineDonation>> {
        return db.collection("users").document(userId)
            .collection("donations")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(FirebaseMedicineDonation::class.java)
            }
    }

    suspend fun saveDonation(userId: String, donation: FirebaseMedicineDonation) {
        require(userId.isNotBlank()) { "userId must not be blank" }
        require(donation.id.isNotBlank()) { "donation id must not be blank" }
        db.collection("users").document(userId)
            .collection("donations").document(donation.id)
            .set(donation)
            .await()
    }

    // User Listings
    fun observeListings(userId: String): Flow<List<FirebaseUserListing>> {
        return db.collection("users").document(userId)
            .collection("listings")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(FirebaseUserListing::class.java)
            }
    }

    suspend fun saveListing(userId: String, listing: FirebaseUserListing) {
        require(userId.isNotBlank()) { "userId must not be blank" }
        require(listing.id.isNotBlank()) { "listing id must not be blank" }
        db.collection("users").document(userId)
            .collection("listings").document(listing.id)
            .set(listing)
            .await()
    }

    // Orders
    fun observeOrders(userId: String): Flow<List<FirebaseUserOrder>> {
        return db.collection("users").document(userId)
            .collection("orders")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(FirebaseUserOrder::class.java)
            }
    }

    suspend fun saveOrder(userId: String, order: FirebaseUserOrder) {
        require(userId.isNotBlank()) { "userId must not be blank" }
        require(order.id.isNotBlank()) { "order id must not be blank" }
        db.collection("users").document(userId)
            .collection("orders").document(order.id)
            .set(order)
            .await()
    }
}

package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Medicine
import com.example.data.model.Order
import com.example.data.model.ReversePickupRequest
import com.example.data.model.ScratchCard
import com.example.data.model.WishlistItem
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicineDao {
    @Query("SELECT * FROM medicines ORDER BY brandName ASC")
    fun getAllMedicines(): Flow<List<Medicine>>

    @Query("SELECT * FROM medicines WHERE isNearExpiry = 1 ORDER BY expiryDaysLeft ASC")
    fun getNearExpiryMedicines(): Flow<List<Medicine>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicines(medicines: List<Medicine>)

    @Query("SELECT COUNT(*) FROM medicines")
    suspend fun getMedicineCount(): Int
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<Order>>

    @Query("SELECT * FROM orders ORDER BY createdAt DESC LIMIT 1")
    fun getLatestOrder(): Flow<Order?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order)

    @Update
    suspend fun updateOrder(order: Order)

    @Query("UPDATE orders SET status = :status, currentStage = :stage WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: String, status: String, stage: Int)
}

@Dao
interface ReversePickupDao {
    @Query("SELECT * FROM reverse_pickups ORDER BY createdAt DESC")
    fun getAllPickups(): Flow<List<ReversePickupRequest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPickup(pickup: ReversePickupRequest)
}

@Dao
interface ScratchCardDao {
    @Query("SELECT * FROM scratch_cards ORDER BY isScratched ASC, unlockedAt DESC")
    fun getAllScratchCards(): Flow<List<ScratchCard>>

    @Query("SELECT * FROM scratch_cards WHERE isScratched = 0")
    fun getAvailableCards(): Flow<List<ScratchCard>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: ScratchCard)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCards(cards: List<ScratchCard>)

    @Update
    suspend fun updateCard(card: ScratchCard)

    @Query("SELECT COUNT(*) FROM scratch_cards WHERE isScratched = 0")
    fun getUnscratchedCount(): Flow<Int>
}

@Dao
interface WishlistDao {
    @Query("SELECT medicineId FROM wishlist ORDER BY addedAt DESC")
    fun getAllWishlistIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToWishlist(item: WishlistItem)

    @Query("DELETE FROM wishlist WHERE medicineId = :medicineId")
    suspend fun removeFromWishlist(medicineId: String)
}

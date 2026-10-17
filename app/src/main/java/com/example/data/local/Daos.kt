package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Coupon
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.data.model.ScratchCard
import com.example.data.model.WishlistItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY title ASC")
    fun getAllProducts(): Flow<List<Product>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<Product>)

    @Query("SELECT COUNT(*) FROM products")
    suspend fun getProductCount(): Int
}

@Dao
interface WishlistDao {
    @Query("SELECT productId FROM wishlist ORDER BY addedAt DESC")
    fun getAllWishlistIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToWishlist(item: WishlistItem)

    @Query("DELETE FROM wishlist WHERE productId = :productId")
    suspend fun removeFromWishlist(productId: String)

    @Query("SELECT COUNT(*) FROM wishlist")
    fun getWishlistCount(): Flow<Int>
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
interface CouponDao {
    @Query("SELECT * FROM coupons ORDER BY isUsed ASC, claimedAt DESC")
    fun getAllCoupons(): Flow<List<Coupon>>

    @Query("SELECT * FROM coupons WHERE isUsed = 0")
    fun getAvailableCoupons(): Flow<List<Coupon>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoupon(coupon: Coupon)

    @Update
    suspend fun updateCoupon(coupon: Coupon)
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY timestamp DESC")
    fun getAllOrders(): Flow<List<Order>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order)

    @Update
    suspend fun updateOrder(order: Order)
}

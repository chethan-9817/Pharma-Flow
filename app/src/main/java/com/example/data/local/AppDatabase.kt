package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.Coupon
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.data.model.ScratchCard
import com.example.data.model.WishlistItem

@Database(
    entities = [
        Product::class,
        ScratchCard::class,
        Coupon::class,
        Order::class,
        WishlistItem::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun wishlistDao(): WishlistDao
    abstract fun scratchCardDao(): ScratchCardDao
    abstract fun couponDao(): CouponDao
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "scratchmart.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}

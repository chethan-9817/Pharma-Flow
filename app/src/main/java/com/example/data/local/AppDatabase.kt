package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.Medicine
import com.example.data.model.Order
import com.example.data.model.ReversePickupRequest
import com.example.data.model.ScratchCard
import com.example.data.model.UserProductListing
import com.example.data.model.WishlistItem

@Database(
    entities = [
        Medicine::class,
        Order::class,
        ReversePickupRequest::class,
        ScratchCard::class,
        WishlistItem::class,
        UserProductListing::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun medicineDao(): MedicineDao
    abstract fun orderDao(): OrderDao
    abstract fun reversePickupDao(): ReversePickupDao
    abstract fun scratchCardDao(): ScratchCardDao
    abstract fun wishlistDao(): WishlistDao
    abstract fun userProductListingDao(): UserProductListingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "medlifecycle.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}

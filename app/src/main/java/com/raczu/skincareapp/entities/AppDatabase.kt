package com.raczu.skincareapp.entities

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.raczu.skincareapp.daos.ProductDao
import com.raczu.skincareapp.daos.RoutineDao
import com.raczu.skincareapp.daos.RoutineNotificationDao

@Database(
    entities = [
        Routine::class,
        Product::class,
        RoutineProductCrossRef::class,
        RoutineNotification::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun routineDao(): RoutineDao
    abstract fun productDao(): ProductDao
    abstract fun routineNotificationDao(): RoutineNotificationDao

    companion object {
        private const val DATABASE_NAME = "skin-care-db"
        @Volatile
        private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instance = it }
            }
        }
    }
}
package com.example.spendwise.data.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.spendwise.data.dao.MonthlyBalanceDao
import com.example.spendwise.data.dao.MonthlySummaryDao
import com.example.spendwise.data.dao.TransactionDao
import com.example.spendwise.data.entity.BalanceHistoryEntity
import com.example.spendwise.data.entity.MonthlyBalanceEntity
import com.example.spendwise.data.entity.MonthlySummaryEntity
import com.example.spendwise.data.entity.TransactionEntity
import com.example.spendwise.viewmodel.TransactionTypeConverter

@Database(
    entities = [TransactionEntity::class, MonthlySummaryEntity::class, MonthlyBalanceEntity::class, BalanceHistoryEntity::class],
    version = 8, // incremented version
)
@TypeConverters(TransactionTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun monthlySummaryDao(): MonthlySummaryDao

    abstract fun monthlyBalanceDao(): MonthlyBalanceDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "spendwise_db"
                )
                    // optional: .fallbackToDestructiveMigration() if you don't care about old data
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
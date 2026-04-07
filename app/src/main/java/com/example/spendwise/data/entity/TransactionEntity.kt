package com.example.spendwise.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.spendwise.viewmodel.TransactionType

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val amount: Double,
    val type: TransactionType,
    val date: String,
    val notes: String
)
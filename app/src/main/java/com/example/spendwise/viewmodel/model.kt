package com.example.spendwise.viewmodel

data class Transaction(
    val id: Int = 0,
    val title: String,
    val amount: Double,
    val type: TransactionType,
    val date: String,
    val notes: String = ""
)

enum class TransactionType {
    INCOME, EXPENSE
}

data class MonthlySummary(
    val month: String,
    val income: Double,
    val expenses: Double
)

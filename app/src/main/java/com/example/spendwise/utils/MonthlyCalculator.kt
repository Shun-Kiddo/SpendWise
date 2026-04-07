package com.example.spendwise.utils

import com.example.spendwise.data.entity.MonthlySummaryEntity
import com.example.spendwise.viewmodel.Transaction
import com.example.spendwise.viewmodel.TransactionType
import java.text.SimpleDateFormat
import java.util.*

object MonthlySummaryCalculator {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    fun calculateMonthlySummaries(
        transactions: List<Transaction>
    ): List<MonthlySummaryEntity> {

        if (transactions.isEmpty()) return emptyList()

        val grouped = transactions.groupBy { tx ->
            val date = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                .parse(tx.date) ?: Date()

            val cal = Calendar.getInstance().apply { time = date }

            "${cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())}-${cal.get(Calendar.YEAR)}"
        }

        return grouped.map { (month, txList) ->

            val income = txList
                .filter { it.type == TransactionType.INCOME }
                .sumOf { it.amount }

            val expense = txList
                .filter { it.type == TransactionType.EXPENSE }
                .sumOf { it.amount }

            MonthlySummaryEntity(month, income, expense)
        }
    }
}
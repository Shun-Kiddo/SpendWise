package com.example.spendwise.data.repository

import com.example.spendwise.data.dao.MonthlyBalanceDao
import com.example.spendwise.data.dao.TransactionDao
import com.example.spendwise.data.entity.toEntity
import com.example.spendwise.data.entity.toTransaction
import com.example.spendwise.viewmodel.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionRepository(
    private val transactionDao: TransactionDao,
    private val monthlyBalanceDao: MonthlyBalanceDao
) {

    fun getTransactions(): Flow<List<Transaction>> {
        return transactionDao.getAllTransactions()
            .map { list -> list.map { it.toTransaction() } as List<Transaction> }
    }

    suspend fun insertTransaction(transaction: Transaction) {
        transactionDao.insertTransaction(transaction.toEntity())
    }
    suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.updateTransaction(transaction.toEntity())
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction.toEntity())
    }
    fun getCurrentBalance(month: String) =
        monthlyBalanceDao.getCurrentBalance(month)


}
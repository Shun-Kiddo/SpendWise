package com.example.spendwise.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendwise.data.roomdb.AppDatabase
import com.example.spendwise.data.entity.MonthlySummaryEntity
import com.example.spendwise.data.repository.TransactionRepository
import com.example.spendwise.utils.MonthlySummaryCalculator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TransactionViewModel(application: Application) : AndroidViewModel(application) {

//    private val db = AppDatabase.getDatabase(application)
//    private val dao = db.transactionDao()
//val transactions: StateFlow<List<Transaction>> = dao.getAllTransactions()
//    .map { list -> list.map { it.toTransaction() } }
//    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

private val database = AppDatabase.getDatabase(application)

    private val repository = TransactionRepository(
        database.transactionDao(),
        database.monthlyBalanceDao()
    )

    val transactions: StateFlow<List<Transaction>> =
        repository.getTransactions()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val monthlySummaries: StateFlow<List<MonthlySummaryEntity>> =
        transactions
            .map { txList ->
                MonthlySummaryCalculator.calculateMonthlySummaries(txList)
                    .sortedBy { it.month }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.updateTransaction(transaction)
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }
//    fun updateTransaction(transaction: Transaction) {
//        viewModelScope.launch {
//            dao.updateTransaction(transaction.toEntity())
//        }
//    }

//    fun deleteTransaction(transaction: Transaction) {
//        viewModelScope.launch {
//            dao.deleteTransaction(transaction.toEntity())
//        }
//    }
}
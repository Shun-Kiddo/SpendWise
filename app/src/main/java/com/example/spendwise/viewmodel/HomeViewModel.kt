package com.example.spendwise.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendwise.data.repository.TransactionRepository
import com.example.spendwise.data.roomdb.AppDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    //    var resetTime: Long? = null
    //        private set
    //    var resetTime by mutableStateOf<String?>(null)
    //    private val dao = AppDatabase.getDatabase(application).transactionDao()
    //    private val monthlyBalanceDao = AppDatabase.getDatabase(application).monthlyBalanceDao()
    //   val currentMonthBalance = monthlyBalanceDao.getCurrentBalance(currentMonth)
    //    val transactions: StateFlow<List<Transaction>> = dao.getAllTransactions()
    //        .map { list -> list.map { it.toTransaction() } }
    //        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    //    fun addTransaction(transaction: Transaction) {
    //        viewModelScope.launch {
    //            dao.insertTransaction(transaction.toEntity())
    //        }
    //    }
    private val database = AppDatabase.getDatabase(application)

    private val repository = TransactionRepository(
        database.transactionDao(),
        database.monthlyBalanceDao()
    )

    private val currentMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(java.util.Date())

val currentMonthBalance = repository.getCurrentBalance(currentMonth)
    val transactions: StateFlow<List<Transaction>> =
        repository.getTransactions()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.insertTransaction(transaction)
        }
    }



//    fun getCurrentMonth(): String {
//        return java.text.SimpleDateFormat("yyyy-MM", Locale.getDefault())
//            .format(java.util.Date())
//    }
//    fun saveMonthlyBalance(balance: Double) {
//        viewModelScope.launch {
//
//            // Save ending balance of the month
//            monthlyBalanceDao.saveBalance(
//                MonthlyBalanceEntity(
//                    month = currentMonth,
//                    remainingBalance = 0.0,     // <-- UI should become 0
//                    resetTimestamp = System.currentTimeMillis()
//                )
//            )
//
//            // update UI immediately
//            resetTime = System.currentTimeMillis()
//        }
//    }

//fun saveMonthlyBalance(balanceBeforeReset: Double) {
//    viewModelScope.launch {
//
//        val currentMonth =
//            SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(java.util.Date())
//
//        val now = System.currentTimeMillis()
//
//        monthlyBalanceDao.insertHistory(
//            BalanceHistoryEntity(
//                month = currentMonth,
//                closingBalance = balanceBeforeReset,
//                savedAt = now
//            )
//        )
//
//        monthlyBalanceDao.saveCurrentBalance(
//            MonthlyBalanceEntity(
//                month = currentMonth,
//                remainingBalance = 0.0,
//                resetTimestamp = now
//            )
//        )
//
//        /* -----------------------------------------
//           3️⃣ UPDATE UI FILTER TIME
//        ------------------------------------------*/
//        resetTime = now
//    }
//}
//    val currentMonthBalance: StateFlow<MonthlyBalanceEntity?> =
//        monthlyBalanceDao.getBalanceForMonth(getCurrentMonth())
//            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}

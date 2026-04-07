package com.example.spendwise.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.spendwise.utils.BalanceCard
import com.example.spendwise.utils.SmallCard
import com.example.spendwise.viewmodel.Transaction
import com.example.spendwise.viewmodel.TransactionType
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spendwise.navigation.TopNavBar
import com.example.spendwise.viewmodel.HomeViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatMoney(amount: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale.US)
    formatter.maximumFractionDigits = 0
    formatter.minimumFractionDigits = 0
    return formatter.format(amount)
}

fun parseDateToMillis(date: String): Long {
    return try {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        sdf.parse(date)?.time ?: 0L
    } catch (e: Exception) {
        0L
    }
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {

    val savedBalanceEntity by viewModel.currentMonthBalance.collectAsState(initial = null)
    val transactions by viewModel.transactions.collectAsState(initial = emptyList())

    val currentMonth = remember {
        SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
    }

    var showDialog by remember { mutableStateOf(false) }
    var showNoIncomeDialog by remember { mutableStateOf(false) }
    var showAlreadyResetDialog by remember { mutableStateOf(false) }

    var selectedType by remember { mutableStateOf(TransactionType.INCOME) }

    /* ---------------- FILTER AFTER RESET ---------------- */
    val filteredTransactions = remember(transactions, savedBalanceEntity) {

        val resetTime = savedBalanceEntity?.resetTimestamp

        if (resetTime == null) transactions
        else transactions.filter { parseDateToMillis(it.date) > resetTime }
    }

    val incomeTotal =
        filteredTransactions.filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }

    val expenseTotal =
        filteredTransactions.filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }

    val calculatedBalance = incomeTotal - expenseTotal

    /* ---------------- UI BALANCE ---------------- */
    val uiBalance = calculatedBalance

    Scaffold(
        topBar = { TopNavBar() }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            BalanceCard(
                balance = "₱ ${formatMoney(uiBalance)}",
//                onResetClick = {
//
//                    // prevent resetting twice in same month
//                    if (savedBalanceEntity?.month == currentMonth) {
//                        showAlreadyResetDialog = true
//                        return@BalanceCard
//                    }
//
//                    // send CURRENT BALANCE before reset
//                    viewModel.saveMonthlyBalance(calculatedBalance)
//                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                SmallCard(
                    "Income",
                    Icons.Default.ArrowUpward,
                    "₱ ${formatMoney(incomeTotal)}",
                    Color(0xFFCFDACF)
                ) {
                    selectedType = TransactionType.INCOME
                    showDialog = true
                }

                SmallCard(
                    "Expense",
                    Icons.Default.ArrowDownward,
                    "₱ ${formatMoney(expenseTotal)}",
                    Color(0xFFEED2D2)
                ) {
                    selectedType = TransactionType.EXPENSE

                    if (uiBalance <= 0) showNoIncomeDialog = true
                    else showDialog = true
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TransactionSection(filteredTransactions)
        }
    }

    /* ---------------- DIALOGS ---------------- */

    if (showAlreadyResetDialog) {
        AlertDialog(
            onDismissRequest = { showAlreadyResetDialog = false },
            confirmButton = {
                TextButton(onClick = { showAlreadyResetDialog = false }) {
                    Text("OK")
                }
            },
            title = { Text("Already Reset") },
            text = { Text("You have already reset your balance for this month.") }
        )
    }

    if (showDialog) {
        AddTransactionDialog(
            type = selectedType,
            onDismiss = { showDialog = false },
            onSave = { viewModel.addTransaction(it) }
        )
    }

    if (showNoIncomeDialog) {
        AlertDialog(
            onDismissRequest = { showNoIncomeDialog = false },
            confirmButton = {
                TextButton(onClick = { showNoIncomeDialog = false }) {
                    Text("OK")
                }
            },
            title = { Text("Insufficient Balance") },
            text = { Text("You cannot add an expense because there is no remaining balance or income.") }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(
    type: TransactionType,
    onDismiss: () -> Unit,
    onSave: (Transaction) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val suggestions = if (type == TransactionType.INCOME)
        listOf("Salary", "Allowance", "Gift", "Investment")
    else
        listOf("Food", "Transport", "Bills", "Shopping", "Entertainment")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                // HEADER
                Column {
                    Text(
                        text = "New ${type.name.lowercase().replaceFirstChar { it.uppercase() }}",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = if (type == TransactionType.INCOME) Color(0xFF2E7D32) else Color(0xFFC62828)
                        )
                    )
                }

                // CATEGORY DROPDOWN (Modernized)
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Category") },
                      //  leadingIcon = { Icon(Icons.Default.Category, contentDescription = null, tint = Color(0xFF29CFAE)) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF29CFAE),
                            focusedLabelColor = Color(0xFF29CFAE)
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        suggestions.forEach { selection ->
                            DropdownMenuItem(
                                text = { Text(selection) },
                                onClick = {
                                    title = selection
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                // AMOUNT FIELD
                OutlinedTextField(
                    value = amount,
                    onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) amount = it },
                    label = { Text("Amount") },
                   // leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null, tint = Color(0xFF29CFAE)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF29CFAE),
                        focusedLabelColor = Color(0xFF29CFAE)
                    )
                )

                // NOTES FIELD
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (Optional)") },
                   // leadingIcon = { Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFF29CFAE)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    minLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF29CFAE),
                        focusedLabelColor = Color(0xFF29CFAE)
                    )
                )

                // ACTIONS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray)
                    ) {
                        Text("Dismiss", fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = {
                            val timestamp = java.text.SimpleDateFormat(
                                "yyyy-MM-dd HH:mm",
                                java.util.Locale.getDefault()
                            ).format(java.util.Date())

                            onSave(
                                Transaction(
                                    title = title,
                                    amount = amount.toDoubleOrNull() ?: 0.0,
                                    type = type,
                                    date = timestamp,
                                    notes = notes
                                )
                            )
                            onDismiss()
                        },
                        enabled = title.isNotBlank() && amount.isNotBlank(),
                        modifier = Modifier.weight(1.5f),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (type == TransactionType.INCOME) {
                                Color(0xFFCFDACF)
                            } else {
                                Color(0xFFEED2D2)
                            },
                            contentColor = if (type == TransactionType.INCOME) {
                                Color(0xFF2E7D32)
                            } else {
                                Color(0xFFC62828)
                            },
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Text(
                            text = "Save",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionSection(list: List<Transaction>) {

    var isLoading by remember { mutableStateOf(true) }

    // Simulate short loading
    LaunchedEffect(list) {
        isLoading = true
        kotlinx.coroutines.delay(500)
        isLoading = false
    }

    Column {

        Text(
            "Recent Transactions",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Box with fixed height to make LazyColumn scrollable
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            when {
                isLoading -> {
                    // Loading spinner
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                list.isEmpty() -> {
                    // No transactions message
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No transactions found", color = Color.Gray)
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(list) { it ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(it.title)
                                    Text(
                                        it.date,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                                Text(
                                    text = if (it.type == TransactionType.INCOME)
                                        "+₱ ${formatMoney(it.amount)}"
                                    else
                                        "-₱ ${formatMoney(it.amount)}",
                                    color = if (it.type == TransactionType.INCOME)
                                        Color(0xFF2E7D32)
                                    else
                                        Color(0xFFC62828)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
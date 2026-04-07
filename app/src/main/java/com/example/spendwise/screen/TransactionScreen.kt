package com.example.spendwise.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.spendwise.navigation.TopNavBar
import com.example.spendwise.viewmodel.Transaction
import com.example.spendwise.viewmodel.TransactionType
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spendwise.viewmodel.TransactionViewModel

@Composable
fun TransactionScreen(
    modifier: Modifier = Modifier,
    transactionViewModel: TransactionViewModel = viewModel()
) {
    val allTransactions by transactionViewModel.transactions.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }
    var selectedTransaction by remember { mutableStateOf<Transaction?>(null) }

    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    // Filtered transactions
    val filteredTransactions = remember(searchQuery, allTransactions) {
        if (searchQuery.isBlank()) allTransactions
        else allTransactions.filter { tx ->
            tx.title.contains(searchQuery, ignoreCase = true) ||
                    tx.notes.contains(searchQuery, ignoreCase = true) ||
                    tx.amount.toString().contains(searchQuery)
        }
    }

    LaunchedEffect(allTransactions) {
        isLoading = true
        kotlinx.coroutines.delay(500)
        isLoading = false
    }

    Scaffold(
        topBar = { TopNavBar() }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {


            Spacer(modifier = Modifier.height(8.dp))

            // Search Bar
            OutlinedTextField(
                shape = RoundedCornerShape(20.dp),
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search by keyword") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("All Transactions", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
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
                    filteredTransactions.isEmpty() -> {
                        // No transactions message
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No transactions found", color = Color.Gray)
                        }
                    }
                    else -> {
                        // Transaction list
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(bottom = 80.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(filteredTransactions, key = { it.id }) { transaction ->
                                TransactionCard(
                                    transaction = transaction,
                                    onEdit = {
                                        selectedTransaction = transaction
                                        showEditDialog = true
                                    },
                                    onDelete = {
                                        transactionViewModel.deleteTransaction(transaction)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Edit Transaction Dialog
    if (showEditDialog && selectedTransaction != null) {
        EditTransactionDialog(
            transaction = selectedTransaction!!,
            onDismiss = { showEditDialog = false },
            onSave = { updated ->
                transactionViewModel.updateTransaction(updated)
                showEditDialog = false
            }
        )
    }
}

@Composable
fun TransactionCard(
    transaction: Transaction,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    isLoading: Boolean = false
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 70.dp),
        shape = RoundedCornerShape(12.dp)
    ) {

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(strokeWidth = 2.dp)
            }
        } else {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Column {
                    Text(transaction.title, style = MaterialTheme.typography.titleMedium)
                    Text(transaction.date, style = MaterialTheme.typography.bodySmall)
                    Text(transaction.notes, style = MaterialTheme.typography.bodySmall)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (transaction.type == TransactionType.INCOME)
                            "+₱ ${formatMoney(transaction.amount)}"
                        else
                            "-₱ ${formatMoney(transaction.amount)}",
                        color = if (transaction.type == TransactionType.INCOME)
                            Color(0xFF2E7D32)
                        else
                            Color(0xFFC62828)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Options")
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Edit") },
                                onClick = {
                                    expanded = false
                                    onEdit()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete") },
                                onClick = {
                                    expanded = false
                                    onDelete()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTransactionDialog(
    transaction: Transaction,
    onDismiss: () -> Unit,
    onSave: (Transaction) -> Unit
) {
    var title by remember { mutableStateOf(transaction.title) }
    var amount by remember { mutableStateOf(transaction.amount.toString()) }
    var notes by remember { mutableStateOf(transaction.notes) }
    var expanded by remember { mutableStateOf(false) }


    val suggestions = if (transaction.type == TransactionType.INCOME)
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
                        text = "Edit ${transaction.type.name.lowercase().replaceFirstChar { it.uppercase() }}",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = if (transaction.type == TransactionType.INCOME) Color(0xFF2E7D32) else Color(0xFFC62828)
                        )
                    )
                }

                // CATEGORY DROPDOWN
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Category") },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF29CFAE),
                            focusedLabelColor =  Color(0xFF29CFAE)
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
                            val updatedTransaction = transaction.copy(
                                title = title,
                                amount = amount.toDoubleOrNull() ?: transaction.amount,
                                notes = notes
                            )
                            onSave(updatedTransaction)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1.5f),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (transaction.type == TransactionType.INCOME) {
                                Color(0xFFCFDACF)
                            } else {
                                Color(0xFFEED2D2)
                            },
                            contentColor = if (transaction.type == TransactionType.INCOME) {
                                Color(0xFF2E7D32)
                            } else {
                                Color(0xFFC62828)
                            },
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Text(
                            text = "Update",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
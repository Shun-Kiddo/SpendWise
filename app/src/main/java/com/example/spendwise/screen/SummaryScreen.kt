package com.example.spendwise.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spendwise.data.entity.MonthlySummaryEntity
import com.example.spendwise.navigation.TopNavBar
import com.example.spendwise.viewmodel.HomeViewModel
import com.example.spendwise.viewmodel.Transaction
import com.example.spendwise.viewmodel.TransactionType
import com.example.spendwise.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SummaryScreen(
    modifier: Modifier = Modifier,
    viewmodel: TransactionViewModel = viewModel(),
    viewModel: HomeViewModel = viewModel()
) {
    val summaries by viewmodel.monthlySummaries.collectAsState()

    val transactions by viewmodel.transactions.collectAsState()
    val incomeTotal = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    val expenseTotal = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }


    val currentMonth = remember {
        SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
    }
    val calculatedBalance = incomeTotal - expenseTotal

    val savedBalanceEntity by viewModel.currentMonthBalance.collectAsState(initial = null)
    val uiBalance = remember(savedBalanceEntity, calculatedBalance) {

        // If reset exists for this month → LOCK balance to stored value (0)
        if (savedBalanceEntity?.month == currentMonth) {
            0.0
        } else {
            calculatedBalance
        }
    }
    Scaffold(
        topBar = { TopNavBar() }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = 80.dp // 👈 THIS FIXES YOUR PROBLEM
            )
        ){

            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF29CFAE)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(18.dp)) {
                        Text(
                            "Remaining Balance",
                            color = Color.White,
                            style = MaterialTheme.typography.labelLarge
                        )
                        Text(
                            "₱ ${formatMoney(uiBalance)}",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(18.dp)) }

            item {
                if (summaries.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No transactions yet", color = Color.Gray)
                    }
                } else {
                    AnalyticsBarGraph(
                        summaries = summaries,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            item { Spacer(Modifier.height(18.dp)) }

            item {
                ExpenseListSection(transactions)
            }
        }
    }
}

@Composable
fun AnalyticsBarGraph(
    summaries: List<MonthlySummaryEntity>,
    modifier: Modifier = Modifier
) {
    val maxValue = summaries.flatMap { listOf(it.income, it.expenses) }.maxOrNull() ?: 0.0

    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 300.dp)
    ) {

        Text(
            text = "Monthly Analytics",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(28.dp),
            elevation = CardDefaults.cardElevation(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(Modifier.fillMaxWidth().padding(vertical = 16.dp, horizontal = 12.dp)) {

                // grid background
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val lineSpacing = size.height / 5
                    repeat(5) { i ->
                        drawLine(
                            color = Color.LightGray.copy(alpha = 0.4f),
                            start = Offset(0f, size.height - i * lineSpacing),
                            end = Offset(size.width, size.height - i * lineSpacing),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                }

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    items(summaries) { month ->
                        MonthBarItem(
                            summary = month,
                            maxValue = maxValue
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun MonthBarItem(
    summary: MonthlySummaryEntity,
    maxValue: Double
) {
    val incomeRatio = if (maxValue == 0.0) 0f else (summary.income / maxValue).toFloat()
    val expenseRatio = if (maxValue == 0.0) 0f else (summary.expenses / maxValue).toFloat()

    val animatedIncome by animateFloatAsState(incomeRatio, tween(900))
    val animatedExpense by animateFloatAsState(expenseRatio, tween(900))

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {

        // 🔥 BAR CONTAINER
        Box(
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter
        ) {

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.Bottom
            ) {

                // INCOME BAR with top label
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        "₱ ${formatMoney(summary.income)}",
                        fontSize = 6.sp,
                        color = Color(0xFF2E7D32)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .width(18.dp)
                            .fillMaxHeight(animatedIncome)
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFF66BB6A), Color(0xFF1B5E20))
                                ),
                                RoundedCornerShape(6.dp)
                            )
                    )
                }

                // EXPENSE BAR with top label
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        "₱ ${formatMoney(summary.expenses)}",
                        fontSize = 6.sp,
                        color = Color(0xFFC62828)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .width(18.dp)
                            .fillMaxHeight(animatedExpense)
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFFEF5350), Color(0xFFB71C1C))
                                ),
                                RoundedCornerShape(6.dp)
                            )
                    )
                }
            }
        }

        Spacer(Modifier.height(6.dp))
        Text(summary.month, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun ExpenseListSection(transactions: List<Transaction>) {

    val expenses = transactions.filter { it.type == TransactionType.EXPENSE }
    var isLoading by remember { mutableStateOf(true) }

    // Simulate short loading
    LaunchedEffect(expenses) {
        isLoading = true
        kotlinx.coroutines.delay(500)
        isLoading = false
    }

    Column {

        Text(
            "All Expenses",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(10.dp))

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
                expenses.isEmpty() -> {
                    // No data message
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No expenses yet", color = Color.Gray)
                    }
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(expenses) { expense ->
                            ExpenseRow(expense)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExpenseRow(expense: Transaction) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column {
                Text(expense.title)
                Text(
                    expense.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Text(
                "-₱ ${formatMoney(expense.amount)}",
                color = Color(0xFFC62828),
                style = MaterialTheme.typography.labelLarge
            )
        }

        Divider()
    }
}

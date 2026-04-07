package com.example.spendwise.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.spendwise.navigation.TopNavBar
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
// Sample User Data Class
data class User(
    val name: String,
    val phone: String,
    val email: String,
    val work: String
)

@Composable
fun SettingsScreen(
    user: User = User("Shun Kiddo", "09123456789", "shun@example.com", "Software Engineer"),
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Dialog state variables
    var showTermsDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showAppGuideDialog by remember { mutableStateOf(false) }
    Scaffold(
        topBar = { TopNavBar() }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            item {
                Text(
                    text = "User Details",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2F1)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        DetailRow(label = "Name", value = user.name)
                        DetailRow(label = "Phone", value = user.phone)
                        DetailRow(label = "Email", value = user.email)
                        DetailRow(label = "Work", value = user.work)
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(30.dp)) }

            item {
                Text(
                    text = "App Info",
                    style = MaterialTheme.typography.titleSmall
                )
            }

            item { Spacer(modifier = Modifier.height(10.dp)) }

            item {
                Button(
                    onClick = { showAppGuideDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF667974))
                ) {
                    Text("App Guide", color = Color.White)
                }
            }
            item { Spacer(modifier = Modifier.height(5.dp)) }
            item {
                Button(
                    onClick = { showTermsDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF667974))
                ) {
                    Text("Terms & Conditions", color = Color.White)
                }
            }
            item { Spacer(modifier = Modifier.height(5.dp)) }
            item {
                Button(
                    onClick = { showAboutDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF667974))
                ) {
                    Text("About", color = Color.White)
                }
            }
        }
    }

// ---------------- APP GUIDE ----------------
    if (showAppGuideDialog) {
        AlertDialog(
            onDismissRequest = { showAppGuideDialog = false },
            title = { Text("App Guide") },
            text = {
                Text(
                    """
                Welcome to SpendWise! Here's how to get started:

                • Tap "Income" to add your income.
                • Tap "Expense" to record an expense.
                • Your dashboard will show monthly summaries and remaining balance.
                • Use the Transactions screen to view, edit, or delete past entries.
                
                Keep track of your spending and make smarter financial decisions!
                """.trimIndent()
                )
            },
            confirmButton = {
                TextButton(onClick = { showAppGuideDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

// ---------------- TERMS & CONDITIONS ----------------
    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            title = { Text("Terms & Conditions") },
            text = {
                Text(
                    """
                By using SpendWise, you agree to the following terms:

                1. SpendWise is for personal finance tracking only.
                2. All data is stored locally on your device.
                3. We are not responsible for any financial decisions made based on app data.
                4. You agree not to misuse the app for any illegal activity.

                For full legal information, please contact the developer.
                """.trimIndent()
                )
            },
            confirmButton = {
                TextButton(onClick = { showTermsDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

// ---------------- ABOUT ----------------
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("About SpendWise") },
            text = {
                Text(
                    """
                SpendWise v1.0
                
                Developed by Jayson Mancol.
                
                SpendWise is a mobile app designed to help students and individuals track their daily income and expenses. 
                View monthly summaries, analyze spending, and manage your budget efficiently.
                """.trimIndent()
                )
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    SettingsScreen(modifier = Modifier.fillMaxSize())
}
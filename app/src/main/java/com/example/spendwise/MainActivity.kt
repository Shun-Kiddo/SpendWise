package com.example.spendwise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.spendwise.screen.HomeScreen
import com.example.spendwise.ui.theme.SpendWiseTheme
import com.example.spendwise.navigation.NavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SpendWiseTheme {
                setContent {
                    SpendWiseTheme {
                        NavGraph()
                    }
                }
                }
            }
        }
    }

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SpendWiseTheme {
        NavGraph()
    }
}
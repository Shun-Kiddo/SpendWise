package com.example.spendwise.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.spendwise.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavBar() {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(1.dp) // space between logo and text
            ) {
                // Logo
                Image(
                    painter = painterResource(id = R.drawable.spendwise1),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .height(50.dp)
                        .width(50.dp)
                )

                // Text next to logo
                Text(
                    text = "SpendWise",
                    color = Color.White,
                    style = androidx.compose.material3.MaterialTheme.typography.titleLarge
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF29CFAE),
            titleContentColor = Color.White,
            actionIconContentColor = Color.White
        )
    )
}
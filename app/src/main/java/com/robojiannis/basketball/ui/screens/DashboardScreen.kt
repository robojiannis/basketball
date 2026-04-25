package com.robojiannis.basketball.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robojiannis.basketball.ui.MainViewModel
import com.robojiannis.basketball.ui.theme.*

@Composable
fun DashboardScreen(viewModel: MainViewModel) {
    val matches by viewModel.allMatches.collectAsState()
    
    // Calculate some dummy stats from matches if needed
    val tpg = 1.5f // turnovers per game

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Background).padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "DASHBOARD",
                fontSize = 32.sp,
                color = TextPrimary,
                fontWeight = FontWeight.Black,
                letterSpacing = (-1).sp
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
        item {
            StatProgressCard("TURNOVERS PER GAME", tpg, 2.0f, "40%", isLimit = true)
        }
    }
}

@Composable
fun StatProgressCard(label: String, value: Float, goal: Float, percentage: String, isLimit: Boolean = false) {
    Surface(
        color = CardBackground,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(androidx.compose.foundation.BorderStroke(1.dp, BorderColor), RoundedCornerShape(24.dp))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    label, 
                    color = TextSecondary, 
                    fontSize = 11.sp, 
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
                Box(
                    modifier = Modifier.background(
                        Color.White.copy(alpha = 0.05f),
                        RoundedCornerShape(8.dp)
                    ).padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("+12%", color = OrangePrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                "%.1f".format(value),
                color = TextPrimary,
                fontSize = 64.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-2).sp
            )
        }
    }
}
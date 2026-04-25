package com.robojiannis.basketball.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.robojiannis.basketball.ui.MainViewModel
import com.robojiannis.basketball.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AveragesScreen(viewModel: MainViewModel, navController: NavController) {
    val matches by viewModel.allMatches.collectAsState()

    fun List<Int>.avgStr() = if (isEmpty()) "0.0" else "%.1f".format(map { it.toFloat() }.average())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "SEASON AVERAGES", 
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        letterSpacing = (-1).sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = OrangePrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Background,
                    titleContentColor = TextPrimary
                )
            )
        },
        containerColor = Background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 20.dp)
        ) {
            item {
                StatAverageCard("POINTS PER GAME", matches.map { it.points }.avgStr())
            }
            item {
                StatAverageCard("REBOUNDS PER GAME", matches.map { it.rebounds }.avgStr())
            }
            item {
                StatAverageCard("ASSISTS PER GAME", matches.map { it.assists }.avgStr())
            }
            item {
                StatAverageCard("STEALS PER GAME", matches.map { it.steals }.avgStr())
            }
            item {
                StatAverageCard("BLOCKS PER GAME", matches.map { it.blocks }.avgStr())
            }
            item {
                StatAverageCard("TURNOVERS PER GAME", matches.map { it.turnovers }.avgStr())
            }
        }
    }
}

@Composable
fun StatAverageCard(label: String, value: String) {
    Surface(
        color = CardBackground,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(androidx.compose.foundation.BorderStroke(1.dp, BorderColor), RoundedCornerShape(24.dp))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                label, 
                color = TextSecondary, 
                fontSize = 11.sp, 
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                value,
                color = TextPrimary,
                fontSize = 64.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-2).sp
            )
        }
    }
}

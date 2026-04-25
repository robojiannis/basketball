package com.robojiannis.basketball.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robojiannis.basketball.data.Match
import com.robojiannis.basketball.ui.MainViewModel
import com.robojiannis.basketball.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SettingsScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val matches by viewModel.allMatches.collectAsState()
    val quartersPerMatch by viewModel.quartersPerMatch.collectAsState()
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        uri?.let {
            try {
                context.contentResolver.openOutputStream(it)?.use { outputStream ->
                    outputStream.write(generateCsvContent(matches).toByteArray())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            containerColor = CardBackground,
            title = { Text("Clear All Data?", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("This will permanently delete all recorded matches. This action cannot be undone.", color = TextSecondary) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearData()
                        showDeleteConfirmation = false
                    }
                ) {
                    Text("Clear All", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "SETTINGS",
            fontSize = 32.sp,
            color = TextPrimary,
            fontWeight = FontWeight.Black,
            letterSpacing = (-1).sp
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            "MATCH CONFIGURATION", 
            color = TextSecondary, 
            fontWeight = FontWeight.ExtraBold, 
            fontSize = 12.sp,
            letterSpacing = 1.sp
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            color = CardBackground,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth().border(androidx.compose.foundation.BorderStroke(1.dp, BorderColor), RoundedCornerShape(20.dp))
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Quarters per match", color = TextPrimary, fontWeight = FontWeight.Bold)
                    Text("Standard is 4 quarters", color = TextSecondary, fontSize = 12.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { if (quartersPerMatch > 1) viewModel.updateQuartersPerMatch(quartersPerMatch - 1) },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = Color.White.copy(alpha = 0.05f))
                    ) {
                        Text("-", color = TextPrimary, fontWeight = FontWeight.Black, fontSize = 24.sp)
                    }
                    Text(
                        quartersPerMatch.toString(),
                        color = OrangePrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    IconButton(
                        onClick = { if (quartersPerMatch < 10) viewModel.updateQuartersPerMatch(quartersPerMatch + 1) },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = Color.White.copy(alpha = 0.05f))
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase", tint = TextPrimary)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))
        
        Text(
            "DATA MANAGEMENT", 
            color = TextSecondary, 
            fontWeight = FontWeight.ExtraBold, 
            fontSize = 12.sp,
            letterSpacing = 1.sp
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        SettingsActionCard(
            title = "Export to Google Sheets",
            description = "Save your performance data as a CSV file. This file can be opened directly in Google Sheets.",
            icon = Icons.Default.Share,
            onClick = {
                if (matches.isNotEmpty()) {
                    val timestamp = SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(Date())
                    val fileName = "basketball_stats_$timestamp.csv"
                    exportLauncher.launch(fileName)
                }
            }
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        SettingsActionCard(
            title = "Clear Local Data",
            description = "Reset all cached stats. This action is permanent and cannot be undone.",
            icon = Icons.Default.Delete,
            isDestructive = true,
            onClick = { showDeleteConfirmation = true }
        )
    }
}

@Composable
fun SettingsActionCard(title: String, description: String, icon: ImageVector, isDestructive: Boolean = false, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = CardBackground,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth(),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(if (isDestructive) Color.Red.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = if (isDestructive) LossColor else OrangePrimary, modifier = Modifier.size(20.dp))
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(title, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(description, color = TextSecondary, fontSize = 12.sp, lineHeight = 16.sp)
            }
        }
    }
}

private fun generateCsvContent(matches: List<Match>): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val header = "Date,Home Team,Away Team,Home Score,Away Score,Points,Rebounds,Assists,Steals,Blocks,Turnovers,Result\n"
    val csvContent = StringBuilder(header)

    for (match in matches) {
        val dateStr = dateFormat.format(Date(match.date))
        val result = if (match.isWin) "W" else "L"
        csvContent.append("$dateStr,${match.homeName},${match.awayName},${match.homeScore},${match.awayScore},${match.points},${match.rebounds},${match.assists},${match.steals},${match.blocks},${match.turnovers},$result\n")
    }
    return csvContent.toString()
}

package com.robojiannis.basketball.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.robojiannis.basketball.Screen
import com.robojiannis.basketball.data.Match
import com.robojiannis.basketball.ui.MainViewModel
import com.robojiannis.basketball.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MatchesScreen(viewModel: MainViewModel, navController: NavController) {
    val matches by viewModel.allMatches.collectAsState()
    var matchToDelete by remember { mutableStateOf<Match?>(null) }

    if (matchToDelete != null) {
        AlertDialog(
            onDismissRequest = { matchToDelete = null },
            containerColor = CardBackground,
            title = {
                Text(
                    text = "Delete Match?",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete the match against ${matchToDelete?.awayName}?",
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        matchToDelete?.let { viewModel.deleteMatch(it) }
                        matchToDelete = null
                    }
                ) {
                    Text("Delete", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { matchToDelete = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.LiveEntry.createRoute()) },
                containerColor = OrangePrimary,
                contentColor = Color.Black,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Match")
            }
        },
        containerColor = Background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "MATCHES",
                fontSize = 32.sp,
                color = TextPrimary,
                fontWeight = FontWeight.Black,
                letterSpacing = (-1).sp
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            SeasonAveragesCard(matches) {
                navController.navigate(Screen.Averages.route)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "PLAYER PERFORMANCE",
                fontSize = 12.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(matches) { match ->
                    MatchCard(
                        match = match,
                        onClick = { navController.navigate(Screen.LiveEntry.createRoute(match.id)) },
                        onDelete = { matchToDelete = match }
                    )
                }
            }
        }
    }
}

@Composable
fun SeasonAveragesCard(matches: List<Match>, onClick: () -> Unit) {
    Surface(
        color = CardBackground,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(androidx.compose.foundation.BorderStroke(1.dp, BorderColor), RoundedCornerShape(24.dp))
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                "SEASON AVERAGES", 
                color = OrangePrimary, 
                fontWeight = FontWeight.Black, 
                fontSize = 12.sp,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                AverageItem("PPG", if (matches.isEmpty()) "0.0" else "%.1f".format(matches.map { it.points }.average()))
                AverageItem("RPG", if (matches.isEmpty()) "0.0" else "%.1f".format(matches.map { it.rebounds }.average()))
                AverageItem("APG", if (matches.isEmpty()) "0.0" else "%.1f".format(matches.map { it.assists }.average()))
            }
        }
    }
}

@Composable
fun AverageItem(label: String, value: String) {
    Column {
        Text(label, color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text(value, color = TextPrimary, fontSize = 36.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
fun MatchCard(match: Match, onClick: () -> Unit, onDelete: () -> Unit) {
    val dateFormat = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }
    val dateString = remember(match.date) { dateFormat.format(Date(match.date)) }
    val quartersPlayed = match.quarters.count { it.isPlaying }
    val totalQuarters = match.quarters.size
    var showMatchDetails by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    if (showMatchDetails) {
        AlertDialog(
            onDismissRequest = { showMatchDetails = false },
            containerColor = CardBackground,
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Match Details",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = {
                        val scoreText = buildString {
                            append("${match.homeName.uppercase()} vs ${match.awayName.uppercase()}\n")
                            match.quarters.forEach { quarter ->
                                append("Q${quarter.quarterNumber}: ${quarter.homeScore} - ${quarter.awayScore}\n")
                            }
                            append("TOTAL: ${match.homeScore} - ${match.awayScore}")
                        }
                        clipboardManager.setText(AnnotatedString(scoreText))
                        Toast.makeText(context, "Scores copied to clipboard", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(
                            Icons.Default.ContentCopy,
                            contentDescription = "Copy Scores",
                            tint = OrangePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("QUARTER", color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 10.sp, modifier = Modifier.weight(1f))
                            Text(match.homeName.uppercase(), color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 10.sp, modifier = Modifier.weight(1f))
                            Text(match.awayName.uppercase(), color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 10.sp, modifier = Modifier.weight(1f))
                        }
                        HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
                        match.quarters.forEach { quarter ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Q${quarter.quarterNumber}", color = TextPrimary, fontSize = 14.sp, modifier = Modifier.weight(1f))
                                Text("${quarter.homeScore}", color = OrangePrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(1f))
                                Text("${quarter.awayScore}", color = TextPrimary, fontSize = 14.sp, modifier = Modifier.weight(1f))
                            }
                        }
                        HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("TOTAL", color = TextSecondary, fontWeight = FontWeight.Black, fontSize = 14.sp, modifier = Modifier.weight(1f))
                            Text("${match.homeScore}", color = OrangePrimary, fontWeight = FontWeight.Black, fontSize = 14.sp, modifier = Modifier.weight(1f))
                            Text("${match.awayScore}", color = TextPrimary, fontWeight = FontWeight.Black, fontSize = 14.sp, modifier = Modifier.weight(1f))
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("PLAYER STATS", color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            StatMini("PTS", match.points)
                            StatMini("REB", match.rebounds)
                            StatMini("AST", match.assists)
                            StatMini("STL", match.steals)
                            StatMini("BLK", match.blocks)
                            StatMini("TO", match.turnovers)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showMatchDetails = false }) {
                    Text("Close", color = OrangePrimary)
                }
            }
        )
    }

    Surface(
        color = CardBackground,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(androidx.compose.foundation.BorderStroke(1.dp, BorderColor), RoundedCornerShape(20.dp))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row {
                    StatShort("PTS", match.points)
                    Spacer(modifier = Modifier.width(20.dp))
                    StatShort("REB", match.rebounds)
                    Spacer(modifier = Modifier.width(20.dp))
                    StatShort("AST", match.assists)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray.copy(alpha = 0.5f), modifier = Modifier.size(20.dp))
                    }
                    Box(
                        modifier = Modifier
                            .background(if (match.isWin) WinColor else Color.DarkGray, RoundedCornerShape(6.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(if (match.isWin) "W" else "L", color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                Text(
                    "${match.homeName.uppercase()} vs ${match.awayName.uppercase()}", 
                    color = TextPrimary, 
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "${match.homeScore} : ${match.awayScore}", 
                        color = TextSecondary, 
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { showMatchDetails = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Info, contentDescription = "Match Details", tint = OrangePrimary, modifier = Modifier.size(18.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "PLAYED $quartersPlayed/$totalQuarters QUARTERS",
                    color = OrangePrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = dateString,
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun StatShort(label: String, value: Int) {
    Column {
        Text(label, color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(value.toString(), color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
fun StatMini(label: String, value: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(value.toString(), color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Black)
    }
}
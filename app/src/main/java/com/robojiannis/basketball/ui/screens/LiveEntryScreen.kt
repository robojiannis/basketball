package com.robojiannis.basketball.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.robojiannis.basketball.data.Match
import com.robojiannis.basketball.data.QuarterStats
import com.robojiannis.basketball.ui.MainViewModel
import com.robojiannis.basketball.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveEntryScreen(viewModel: MainViewModel, navController: NavController, matchId: Int? = null) {
    val quartersPerMatch by viewModel.quartersPerMatch.collectAsState()
    var currentQuarterIndex by remember { mutableIntStateOf(0) }
    
    var quarterStatsList by remember { 
        mutableStateOf(List(quartersPerMatch) { QuarterStats(it + 1) }) 
    }
    
    var homeName by remember { mutableStateOf("HOME") }
    var awayName by remember { mutableStateOf("AWAY") }

    var showHomeNameDialog by remember { mutableStateOf(false) }
    var showAwayNameDialog by remember { mutableStateOf(false) }
    var showSummaryDialog by remember { mutableStateOf(false) }

    LaunchedEffect(matchId, quartersPerMatch) {
        if (matchId != null && matchId != -1) {
            val match = viewModel.getMatchById(matchId)
            match?.let {
                homeName = it.homeName
                awayName = it.awayName
                if (it.quarters.isNotEmpty()) {
                    quarterStatsList = it.quarters
                }
            }
        } else {
             quarterStatsList = List(quartersPerMatch) { QuarterStats(it + 1) }
        }
    }

    if (showHomeNameDialog) {
        NameEditDialog(
            title = "Edit Home Team Name",
            currentName = homeName,
            onDismiss = { showHomeNameDialog = false },
            onConfirm = { 
                homeName = it
                showHomeNameDialog = false
            }
        )
    }

    if (showAwayNameDialog) {
        NameEditDialog(
            title = "Edit Away Team Name",
            currentName = awayName,
            onDismiss = { showAwayNameDialog = false },
            onConfirm = { 
                awayName = it
                showAwayNameDialog = false
            }
        )
    }

    if (showSummaryDialog) {
        AlertDialog(
            onDismissRequest = { showSummaryDialog = false },
            containerColor = CardBackground,
            title = { Text("Match Summary", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Quarter", color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1f))
                        Text(homeName.uppercase(), color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1f))
                        Text(awayName.uppercase(), color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1f))
                    }
                    HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
                    quarterStatsList.forEach { quarter ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Q${quarter.quarterNumber}", color = TextPrimary, modifier = Modifier.weight(1f))
                            Text("${quarter.homeScore}", color = OrangePrimary, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            Text("${quarter.awayScore}", color = TextPrimary, modifier = Modifier.weight(1f))
                        }
                    }
                    HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("TOTAL", color = TextSecondary, fontWeight = FontWeight.Black, modifier = Modifier.weight(1f))
                        Text("${quarterStatsList.sumOf { it.homeScore }}", color = OrangePrimary, fontWeight = FontWeight.Black, modifier = Modifier.weight(1f))
                        Text("${quarterStatsList.sumOf { it.awayScore }}", color = TextPrimary, fontWeight = FontWeight.Black, modifier = Modifier.weight(1f))
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSummaryDialog = false }) {
                    Text("Close", color = OrangePrimary)
                }
            }
        )
    }

    val currentStats = quarterStatsList[currentQuarterIndex]

    fun updateCurrentStats(update: (QuarterStats) -> QuarterStats) {
        quarterStatsList = quarterStatsList.mapIndexed { index, stats ->
            if (index == currentQuarterIndex) update(stats) else stats
        }
    }

    val totalHomeScore = quarterStatsList.sumOf { it.homeScore }
    val totalAwayScore = quarterStatsList.sumOf { it.awayScore }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "MATCHES", 
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        letterSpacing = (-1).sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showSummaryDialog = true }) {
                        Icon(Icons.Default.Info, contentDescription = "Summary", tint = OrangePrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Background,
                    titleContentColor = TextPrimary,
                    navigationIconContentColor = OrangePrimary
                )
            )
        },
        containerColor = Background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            // Quarter Selector
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items((1..quarterStatsList.size).toList()) { q ->
                    val isSelected = currentQuarterIndex == q - 1
                    Surface(
                        onClick = { currentQuarterIndex = q - 1 },
                        color = if (isSelected) OrangePrimary else CardBackground,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.width(60.dp).height(40.dp).border(1.dp, if (isSelected) OrangePrimary else BorderColor, RoundedCornerShape(12.dp))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("Q$q", color = if (isSelected) Color.Black else TextSecondary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Score Board
            Surface(
                color = CardBackground,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth().border(androidx.compose.foundation.BorderStroke(1.dp, BorderColor), RoundedCornerShape(24.dp))
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ScoreItem(homeName, currentStats.homeScore, totalHomeScore, onEdit = { showHomeNameDialog = true }) { delta ->
                        updateCurrentStats { it.copy(homeScore = (it.homeScore + delta).coerceAtLeast(0)) }
                    }
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(20.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("LIVE", color = OrangePrimary, fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                        }
                        Text(":", color = TextSecondary, fontSize = 24.sp, fontWeight = FontWeight.Black)
                        Text("TOTAL", color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }

                    ScoreItem(awayName, currentStats.awayScore, totalAwayScore, onEdit = { showAwayNameDialog = true }) { delta ->
                        updateCurrentStats { it.copy(awayScore = (it.awayScore + delta).coerceAtLeast(0)) }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Is Playing Toggle
            Surface(
                color = if (currentStats.isPlaying) OrangePrimary.copy(alpha = 0.1f) else CardBackground,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, if (currentStats.isPlaying) OrangePrimary else BorderColor, RoundedCornerShape(16.dp)),
                onClick = { updateCurrentStats { it.copy(isPlaying = !it.isPlaying) } }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Player is on court", color = TextPrimary, fontWeight = FontWeight.Bold)
                    Switch(
                        checked = currentStats.isPlaying,
                        onCheckedChange = { isPlaying -> updateCurrentStats { it.copy(isPlaying = isPlaying) } },
                        colors = SwitchDefaults.colors(checkedThumbColor = OrangePrimary)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (currentStats.isPlaying) {
                // Stats Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    item { 
                        StatCard("POINTS", "+1", currentStats.points, 
                            onIncrement = { updateCurrentStats { it.copy(points = it.points + 1, homeScore = it.homeScore + 1) } },
                            onDecrement = { if (currentStats.points >= 1) updateCurrentStats { it.copy(points = it.points - 1, homeScore = it.homeScore - 1) } }) 
                    }
                    item { 
                        StatCard("POINTS", "+2", currentStats.points, 
                            onIncrement = { updateCurrentStats { it.copy(points = it.points + 2, homeScore = it.homeScore + 2) } },
                            onDecrement = { if (currentStats.points >= 2) updateCurrentStats { it.copy(points = it.points - 2, homeScore = it.homeScore - 2) } }) 
                    }
                    item { 
                        StatCard("DEEP RANGE", "+3", currentStats.points, 
                            onIncrement = { updateCurrentStats { it.copy(points = it.points + 3, homeScore = it.homeScore + 3) } },
                            onDecrement = { if (currentStats.points >= 3) updateCurrentStats { it.copy(points = it.points - 3, homeScore = it.homeScore - 3) } }) 
                    }
                    item { 
                        StatCard("REBOUND", "+", currentStats.rebounds, 
                            onIncrement = { updateCurrentStats { it.copy(rebounds = it.rebounds + 1) } },
                            onDecrement = { if (currentStats.rebounds > 0) updateCurrentStats { it.copy(rebounds = it.rebounds - 1) } }) 
                    }
                    item { 
                        StatCard("ASSIST", "+", currentStats.assists, 
                            onIncrement = { updateCurrentStats { it.copy(assists = it.assists + 1) } },
                            onDecrement = { if (currentStats.assists > 0) updateCurrentStats { it.copy(assists = it.assists - 1) } }) 
                    }
                    item { 
                        StatCard("STEAL", "DEF", currentStats.steals, 
                            onIncrement = { updateCurrentStats { it.copy(steals = it.steals + 1) } },
                            onDecrement = { if (currentStats.steals > 0) updateCurrentStats { it.copy(steals = it.steals - 1) } }) 
                    }
                    item { 
                        StatCard("BLOCK", "DEF", currentStats.blocks, 
                            onIncrement = { updateCurrentStats { it.copy(blocks = it.blocks + 1) } },
                            onDecrement = { if (currentStats.blocks > 0) updateCurrentStats { it.copy(blocks = it.blocks - 1) } }) 
                    }
                    item { 
                        StatCard("TURNOVER", "+", currentStats.turnovers, 
                            onIncrement = { updateCurrentStats { it.copy(turnovers = it.turnovers + 1) } },
                            onDecrement = { if (currentStats.turnovers > 0) updateCurrentStats { it.copy(turnovers = it.turnovers - 1) } }) 
                    }
                }
            } else {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text("Player is currently benched for Q${currentQuarterIndex + 1}", color = TextSecondary, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val finalPoints = quarterStatsList.sumOf { it.points }
                    val finalRebounds = quarterStatsList.sumOf { it.rebounds }
                    val finalAssists = quarterStatsList.sumOf { it.assists }
                    val finalSteals = quarterStatsList.sumOf { it.steals }
                    val finalBlocks = quarterStatsList.sumOf { it.blocks }
                    val finalTurnovers = quarterStatsList.sumOf { it.turnovers }
                    
                    val match = Match(
                        id = matchId ?: 0,
                        homeName = homeName,
                        awayName = awayName,
                        homeScore = totalHomeScore,
                        awayScore = totalAwayScore,
                        points = finalPoints,
                        rebounds = finalRebounds,
                        assists = finalAssists,
                        steals = finalSteals,
                        blocks = finalBlocks,
                        turnovers = finalTurnovers,
                        isWin = totalHomeScore > totalAwayScore,
                        quarters = quarterStatsList
                    )
                    viewModel.saveMatch(match)
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(bottom = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    if (matchId != null && matchId != -1) "UPDATE MATCH" else "END MATCH", 
                    color = Color.Black, 
                    fontWeight = FontWeight.Black, 
                    fontSize = 16.sp,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
fun NameEditDialog(title: String, currentName: String, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var text by remember { mutableStateOf(currentName) }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardBackground,
        title = { Text(title, color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = OrangePrimary,
                    unfocusedBorderColor = BorderColor
                )
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(text) }) {
                Text("Confirm", color = OrangePrimary, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

@Composable
fun ScoreItem(label: String, quarterScore: Int, totalScore: Int, onEdit: () -> Unit, onScoreChange: (Int) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onEdit() }) {
            Text(label.uppercase(), color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                Icons.Default.Edit, 
                contentDescription = "Edit name", 
                modifier = Modifier.size(10.dp),
                tint = TextSecondary.copy(alpha = 0.5f)
            )
        }
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = { onScoreChange(-1) },
                modifier = Modifier.size(28.dp).background(Color.White.copy(alpha = 0.05f), CircleShape)
            ) {
                Text("-", color = TextSecondary, fontWeight = FontWeight.Black, fontSize = 16.sp)
            }
            
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 4.dp)) {
                Text(
                    quarterScore.toString(),
                    color = TextPrimary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1).sp
                )
                Text(
                    totalScore.toString(),
                    color = OrangePrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            IconButton(
                onClick = { onScoreChange(1) },
                modifier = Modifier.size(28.dp).background(Color.White.copy(alpha = 0.05f), CircleShape)
            ) {
                Text("+", color = OrangePrimary, fontWeight = FontWeight.Black, fontSize = 16.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatCard(label: String, actionText: String, total: Int, onIncrement: () -> Unit, onDecrement: () -> Unit) {
    Surface(
        onClick = onIncrement,
        color = CardBackground,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .height(130.dp)
            .fillMaxWidth()
            .border(androidx.compose.foundation.BorderStroke(1.dp, BorderColor), RoundedCornerShape(20.dp))
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(
                text = label,
                color = TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
                modifier = Modifier.align(Alignment.TopStart)
            )
            
            Text(
                text = actionText,
                color = TextPrimary,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.align(Alignment.Center)
            )
            
            Row(modifier = Modifier.align(Alignment.BottomStart), verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "Q TOTAL",
                    color = TextSecondary,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = total.toString(),
                    color = OrangePrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
            }
            
            // Minus button to correct mistakes
            Surface(
                onClick = { onDecrement() },
                color = Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(36.dp).align(Alignment.BottomEnd)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("-", color = TextSecondary, fontWeight = FontWeight.Black, fontSize = 20.sp)
                }
            }
        }
    }
}
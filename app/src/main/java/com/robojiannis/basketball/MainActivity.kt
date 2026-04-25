package com.robojiannis.basketball

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.robojiannis.basketball.ui.MainViewModel
import com.robojiannis.basketball.ui.screens.AveragesScreen
import com.robojiannis.basketball.ui.screens.LiveEntryScreen
import com.robojiannis.basketball.ui.screens.MatchesScreen
import com.robojiannis.basketball.ui.screens.SettingsScreen
import com.robojiannis.basketball.ui.theme.FullCourtPressTheme
import com.robojiannis.basketball.ui.theme.Background
import com.robojiannis.basketball.ui.theme.OrangePrimary

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FullCourtPressTheme {
                MainApp()
            }
        }
    }
}

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Matches : Screen("matches", "MATCHES", Icons.Filled.List)
    object Settings : Screen("settings", "SETTINGS", Icons.Filled.Settings)
    object Averages : Screen("averages", "AVERAGES", Icons.Filled.Star)
    object LiveEntry : Screen("live_entry?matchId={matchId}", "LIVE", Icons.Filled.List) {
        fun createRoute(matchId: Int? = null) = if (matchId != null) "live_entry?matchId=$matchId" else "live_entry"
    }
}

@Composable
fun MainApp() {
    val navController = rememberNavController()
    val viewModel: MainViewModel = viewModel()
    val items = listOf(Screen.Matches, Screen.Settings)

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            if (currentDestination?.route in items.map { it.route }) {
                NavigationBar(
                    containerColor = Background,
                    tonalElevation = 0.dp
                ) {
                    items.forEach { screen ->
                        val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                        NavigationBarItem(
                            icon = { 
                                Icon(
                                    screen.icon, 
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                ) 
                            },
                            label = { 
                                Text(
                                    screen.label,
                                    fontSize = 10.sp,
                                    fontWeight = if (selected) FontWeight.Black else FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ) 
                            },
                            selected = selected,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = OrangePrimary,
                                selectedTextColor = OrangePrimary,
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray,
                                indicatorColor = Color.Transparent
                            ),
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = Screen.Matches.route, Modifier.padding(innerPadding)) {
            composable(Screen.Matches.route) { MatchesScreen(viewModel, navController) }
            composable(Screen.Averages.route) { AveragesScreen(viewModel, navController) }
            composable(Screen.Settings.route) { SettingsScreen(viewModel) }
            composable(
                route = Screen.LiveEntry.route,
                arguments = listOf(navArgument("matchId") { 
                    type = NavType.IntType
                    defaultValue = -1
                })
            ) { backStackEntry ->
                val matchId = backStackEntry.arguments?.getInt("matchId")
                LiveEntryScreen(viewModel, navController, if (matchId == -1) null else matchId)
            }
        }
    }
}

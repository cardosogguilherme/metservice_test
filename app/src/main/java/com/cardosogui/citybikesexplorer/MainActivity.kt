package com.cardosogui.citybikesexplorer

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRailItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItemColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cardosogui.citybikesexplorer.navigation.CityBikesNavHost
import com.cardosogui.citybikesexplorer.navigation.Routes
import com.cardosogui.citybikesexplorer.ui.theme.CityBikesExplorerTheme
import com.cardosogui.citybikesexplorer.ui.theme.GreenActive
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CityBikesExplorerTheme {
                CityBikesExplorerApp()
            }
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    fun CityBikesExplorerApp() {
        val navController = rememberNavController()
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        // Show the bottom navigation only on the top-level tabs; hide it for detail and the ride flow
        // (so the user cannot tab away mid-ride).
        val showBottomNav = currentRoute == null || currentRoute in Routes.topLevelRoutes

        val colors = NavigationSuiteItemColors(
            navigationBarItemColors = NavigationBarItemDefaults.colors(
                selectedIconColor = GreenActive,
                unselectedIconColor = Color.Gray,
                indicatorColor = Color.Transparent
            ),
            navigationRailItemColors = NavigationRailItemColors(
                selectedIconColor = GreenActive,
                unselectedIconColor = Color.Gray,
                selectedTextColor = GreenActive,
                selectedIndicatorColor = Color.Transparent,
                unselectedTextColor = Color.Gray,
                disabledIconColor = Color.Gray,
                disabledTextColor = Color.Gray
            ),
            navigationDrawerItemColors = NavigationDrawerItemDefaults.colors(
                selectedIconColor = GreenActive,
                unselectedIconColor = Color.Gray
            )
        )

        NavigationSuiteScaffold(
            layoutType = if (showBottomNav) NavigationSuiteType.NavigationBar else NavigationSuiteType.None,
            navigationSuiteItems = {
                AppDestinations.entries.forEach { destination ->
                    item(
                        icon = {
                            Icon(
                                painterResource(destination.icon),
                                contentDescription = destination.label
                            )
                        },
                        label = { Text(destination.label) },
                        selected = currentRoute == destination.route,
                        onClick = {
                            navController.navigate(destination.route) {
                                // Standard multi-tab behaviour: keep a single instance per tab and
                                // preserve each tab's own back stack state.
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = colors
                    )
                }
            }
        ) {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                CityBikesNavHost(
                    navController = navController,
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
    }

    enum class AppDestinations(
        val label: String,
        val icon: Int,
        val route: String,
    ) {
        HOME("Explore", R.drawable.ic_home, Routes.STATIONS),
        FAVORITES("Favorites", R.drawable.ic_favorite, Routes.FAVORITES),
        PROFILE("Profile", R.drawable.ic_account_box, Routes.PROFILE),
    }
}

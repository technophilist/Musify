package com.example.musify.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.musify.ui.components.MusifyBottomNavigation

/**
 * A composable that takes care of setting the active icon of the bottom
 * navigation composable based on the currentBackStackEntry of the [navController].
 * This composable acts as a recomposition scope. Therefore, reading the
 * state value of [NavHostController.currentBackStackEntryAsState()] will result
 * in the recomposition of only this composable.
 */
@Composable
fun MusifyBottomNavigationConnectedWithBackStack(
    navController: NavHostController,
    navigationItems: List<MusifyBottomNavigationDestinations>,
    modifier: Modifier = Modifier
) {
    val currentlySelectedItem =
        navController.currentBackStackEntryAsState().value?.destination?.route?.let {
            when (it) {
                MusifyBottomNavigationDestinations.Home.route -> MusifyBottomNavigationDestinations.Home
                MusifyBottomNavigationDestinations.Search.route -> MusifyBottomNavigationDestinations.Search
                else -> MusifyBottomNavigationDestinations.Premium
            }
        } ?: MusifyBottomNavigationDestinations.Home
    MusifyBottomNavigation(
        modifier = modifier,
        navigationItems = navigationItems,
        currentlySelectedItem = currentlySelectedItem,
        onItemClick = { bottomNavigationDestination ->
            navController.navigate(bottomNavigationDestination.route) {
                popUpTo(navController.graph.startDestinationId) {
                    // Save backstack state. This will ensure restoration of
                    // nested navigation screen when the user comes back to
                    // the destination.
                    saveState = true
                }
                // prevent duplicate destinations when the navigation is
                // clicked multiple times
                launchSingleTop = true
                // restore state if previously saved
                restoreState = true
            }
        }
    )
}
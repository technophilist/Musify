package com.example.musify.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraph.Companion.findStartDestination
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
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    var previousValidBottomNavigationDestination by remember {
        mutableStateOf<MusifyBottomNavigationDestinations>(MusifyBottomNavigationDestinations.Home)
    }
    val currentlySelectedItem by remember {
        derivedStateOf {
            // if the most recent backstack entry is null or it's route
            // doesn't match with any of the routes defined in
            // MusifyBottomNavigationDestinations sealed classes, then
            // use the previously found valid bottom navigation destination
            // as the currently selected item.
            // If it does match one of the routes defined by one of the classes that
            // is a child of MusifyBottomNavigationDestinations sealed class,
            // then store it in a variable and update the state.
            // Essentially, it is picking the most recent valid route that
            // matches the route of a class that is a child of MusifyBottomNavigationDestinations
            // sealed class.
            currentBackStackEntry?.let {
                val route = if (it.isInNestedNavGraph) it.destination.parent?.route
                else it.destination.route
                previousValidBottomNavigationDestination = when (route) {
                    MusifyBottomNavigationDestinations.Home.route -> MusifyBottomNavigationDestinations.Home
                    MusifyBottomNavigationDestinations.Search.route -> MusifyBottomNavigationDestinations.Search
                    MusifyBottomNavigationDestinations.Premium.route -> MusifyBottomNavigationDestinations.Premium
                    else -> previousValidBottomNavigationDestination
                }
                previousValidBottomNavigationDestination
            } ?: previousValidBottomNavigationDestination
        }
    }

    MusifyBottomNavigation(
        modifier = modifier,
        navigationItems = navigationItems,
        currentlySelectedItem = currentlySelectedItem,
        onItemClick = { bottomNavigationDestination ->
            // TODO if the user preses the same icon, in the same destination, pop the backstack.
            navController.navigate(bottomNavigationDestination.route) {
                popUpTo(navController.graph.findStartDestination().id) {
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

/**
 * A [Boolean] that indicates whether the current backstack entry is part
 * of a nested NavGraph.
 */
private val NavBackStackEntry.isInNestedNavGraph get() = this.destination.parent?.parent != null
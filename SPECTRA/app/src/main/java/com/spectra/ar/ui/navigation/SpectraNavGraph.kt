package com.spectra.ar.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.spectra.ar.ui.screens.arcamera.ArCameraScreen
import com.spectra.ar.ui.screens.explore.ExploreScreen
import com.spectra.ar.ui.screens.history.HistoryScreen
import com.spectra.ar.ui.screens.map.MapScreen
import com.spectra.ar.ui.screens.memories.MemoriesScreen
import com.spectra.ar.ui.screens.onboarding.OnboardingScreen
import com.spectra.ar.ui.screens.openinghours.OpeningHoursEditorScreen
import com.spectra.ar.ui.screens.permissions.PermissionCenterScreen
import com.spectra.ar.ui.screens.settings.AppearanceSettingsScreen
import com.spectra.ar.ui.screens.settings.ModelSettingsScreen
import com.spectra.ar.ui.screens.settings.PrivacySettingsScreen
import com.spectra.ar.ui.screens.settings.SettingsScreen

@Composable
fun SpectraNavGraph(startLensId: String? = null) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Explore.route) {
        composable(Screen.Onboarding.route) { OnboardingScreen(onFinished = { navController.navigate(Screen.Explore.route) }) }
        composable(Screen.PermissionCenter.route) { PermissionCenterScreen(onBack = { navController.popBackStack() }) }

        composable(Screen.Explore.route) {
            ExploreScreen(
                onOpenLens = { lensId -> navController.navigate(Screen.ArCamera.withLens(lensId)) },
                onOpenSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(
            route = Screen.ArCamera.route,
            arguments = listOf(navArgument("lensId") { type = NavType.StringType; nullable = true; defaultValue = startLensId })
        ) { backStackEntry ->
            ArCameraScreen(
                lensId = backStackEntry.arguments?.getString("lensId"),
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Memories.route) { MemoriesScreen(onOpenMarker = { /* focus marker in AR camera */ }) }
        composable(Screen.Map.route) { MapScreen() }
        composable(Screen.History.route) { HistoryScreen() }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateAppearance = { navController.navigate(Screen.SettingsAppearance.route) },
                onNavigatePrivacy = { navController.navigate(Screen.SettingsPrivacy.route) },
                onNavigateModels = { navController.navigate(Screen.SettingsModels.route) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.SettingsAppearance.route) { AppearanceSettingsScreen(onBack = { navController.popBackStack() }) }
        composable(Screen.SettingsPrivacy.route) { PrivacySettingsScreen(onBack = { navController.popBackStack() }) }
        composable(Screen.SettingsModels.route) { ModelSettingsScreen(onBack = { navController.popBackStack() }) }

        composable(
            route = Screen.OpeningHoursEditor.route,
            arguments = listOf(navArgument("ownerId") { type = NavType.StringType })
        ) { backStackEntry ->
            OpeningHoursEditorScreen(
                ownerId = backStackEntry.arguments?.getString("ownerId").orEmpty(),
                onDone = { navController.popBackStack() }
            )
        }
    }
}

package com.spectra.ar.ui.navigation

sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object PermissionCenter : Screen("permission_center")
    data object Explore : Screen("explore")
    data object ArCamera : Screen("ar_camera?lensId={lensId}") {
        fun withLens(lensId: String?) = if (lensId != null) "ar_camera?lensId=$lensId" else "ar_camera"
    }
    data object Memories : Screen("memories")
    data object Map : Screen("map")
    data object History : Screen("history")
    data object Settings : Screen("settings")
    data object SettingsAppearance : Screen("settings/appearance")
    data object SettingsPrivacy : Screen("settings/privacy")
    data object SettingsModels : Screen("settings/models")
    data object OpeningHoursEditor : Screen("opening_hours_editor/{ownerId}") {
        fun forOwner(ownerId: String) = "opening_hours_editor/$ownerId"
    }
}

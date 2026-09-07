package com.spectra.ar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.spectra.ar.ui.navigation.SpectraNavGraph
import com.spectra.ar.ui.theme.SpectraTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)
        splash.setKeepOnScreenCondition { viewModel.isLoading.value }

        val lensIdFromShortcut = intent?.getStringExtra("lens_id")

        setContent {
            val themePrefs by viewModel.themePreferences.collectAsState()
            SpectraTheme(preferences = themePrefs) {
                SpectraNavGraph(startLensId = lensIdFromShortcut)
            }
        }
    }
}

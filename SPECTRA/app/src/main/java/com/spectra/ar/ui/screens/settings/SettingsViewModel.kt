package com.spectra.ar.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spectra.ar.data.preferences.AppPreferences
import com.spectra.ar.data.preferences.DataRetentionSettings
import com.spectra.ar.data.preferences.PrivacyPreferences
import com.spectra.ar.ui.theme.AccentColor
import com.spectra.ar.ui.theme.ThemeMode
import com.spectra.ar.ui.theme.ThemePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppearanceSettingsViewModel @Inject constructor(
    private val appPreferences: AppPreferences
) : ViewModel() {
    val themePreferences = appPreferences.themePreferences.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThemePreferences())

    fun setMode(mode: ThemeMode) = viewModelScope.launch { appPreferences.setThemeMode(mode) }
    fun setAccent(accent: AccentColor) = viewModelScope.launch { appPreferences.setAccent(accent) }
    fun setDynamicColor(enabled: Boolean) = viewModelScope.launch { appPreferences.setDynamicColor(enabled) }
    fun setAmoled(enabled: Boolean) = viewModelScope.launch { appPreferences.setAmoled(enabled) }
    fun setAutoSchedule(enabled: Boolean) = viewModelScope.launch { appPreferences.setAutoScheduleAtSunset(enabled) }
    fun setReduceMotion(enabled: Boolean) = viewModelScope.launch { appPreferences.setReduceMotion(enabled) }
}

@HiltViewModel
class PrivacySettingsViewModel @Inject constructor(
    private val privacyPreferences: PrivacyPreferences
) : ViewModel() {
    val cloudAiAllowed = privacyPreferences.cloudAiAllowed.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val retentionSettings = privacyPreferences.retentionSettings.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DataRetentionSettings())

    fun setCloudAiAllowed(allowed: Boolean) = viewModelScope.launch { privacyPreferences.setCloudAiAllowed(allowed) }
    fun setRetention(settings: DataRetentionSettings) = viewModelScope.launch { privacyPreferences.setRetentionSettings(settings) }
}

@HiltViewModel
class ModelSettingsViewModel @Inject constructor(
    private val modelManager: com.spectra.ar.models.ModelManager,
    private val workScheduler: com.spectra.ar.workers.WorkScheduler
) : ViewModel() {
    val catalog = modelManager.catalog

    fun download(modelId: String) = workScheduler.scheduleModelDownload(modelId)
    fun remove(model: com.spectra.ar.models.ModelInfo) = modelManager.removeModel(model)
}

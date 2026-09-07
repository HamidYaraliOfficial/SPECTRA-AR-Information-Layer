package com.spectra.ar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spectra.ar.data.preferences.AppPreferences
import com.spectra.ar.ui.theme.ThemePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    appPreferences: AppPreferences
) : ViewModel() {

    val isLoading = MutableStateFlow(true)

    val themePreferences: StateFlow<ThemePreferences> = appPreferences.themePreferences
        .stateIn(viewModelScope, SharingStarted.Eagerly, ThemePreferences())

    init {
        viewModelScope.launch {
            // First-frame preference read; splash screen holds until this resolves.
            appPreferences.onboardingComplete
            isLoading.value = false
        }
    }
}

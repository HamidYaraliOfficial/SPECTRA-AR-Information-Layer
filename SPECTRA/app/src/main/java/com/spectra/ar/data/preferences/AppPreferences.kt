package com.spectra.ar.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.spectra.ar.core.di.AppPrefsStore
import com.spectra.ar.core.util.SpectraConstants
import com.spectra.ar.ui.theme.AccentColor
import com.spectra.ar.ui.theme.ThemeMode
import com.spectra.ar.ui.theme.ThemePreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/** General, non-sensitive app settings: appearance, language, thresholds, lens defaults. */
@Singleton
class AppPreferences @Inject constructor(
    @AppPrefsStore private val store: DataStore<Preferences>
) {
    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val ACCENT = stringPreferencesKey("accent")
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val AMOLED = booleanPreferencesKey("amoled")
        val AUTO_SCHEDULE = booleanPreferencesKey("auto_schedule_sunset")
        val REDUCE_MOTION = booleanPreferencesKey("reduce_motion")
        val LANGUAGE_TAG = stringPreferencesKey("language_tag")
        val DETECTION_THRESHOLD = floatPreferencesKey("detection_threshold")
        val OCR_THRESHOLD = floatPreferencesKey("ocr_threshold")
        val DEBUG_MODE = booleanPreferencesKey("debug_mode")
        val VOICE_COMMANDS_ENABLED = booleanPreferencesKey("voice_commands_enabled")
        val LAST_ACTIVE_LENS = stringPreferencesKey("last_active_lens")
        val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
        val AUTO_LOCK_MINUTES = intPreferencesKey("auto_lock_minutes")
        val BIOMETRIC_LOCK_ENABLED = booleanPreferencesKey("biometric_lock_enabled")
    }

    val themePreferences: Flow<ThemePreferences> = store.data.map { prefs ->
        ThemePreferences(
            mode = prefs[Keys.THEME_MODE]?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() } ?: ThemeMode.SYSTEM,
            accent = prefs[Keys.ACCENT]?.let { runCatching { AccentColor.valueOf(it) }.getOrNull() } ?: AccentColor.WINDOWS_DEFAULT,
            dynamicColor = prefs[Keys.DYNAMIC_COLOR] ?: false,
            amoled = prefs[Keys.AMOLED] ?: false,
            autoScheduleAtSunset = prefs[Keys.AUTO_SCHEDULE] ?: false,
            reduceMotion = prefs[Keys.REDUCE_MOTION] ?: false
        )
    }

    suspend fun setThemeMode(mode: ThemeMode) = store.edit { it[Keys.THEME_MODE] = mode.name }
    suspend fun setAccent(accent: AccentColor) = store.edit { it[Keys.ACCENT] = accent.name }
    suspend fun setDynamicColor(enabled: Boolean) = store.edit { it[Keys.DYNAMIC_COLOR] = enabled }
    suspend fun setAmoled(enabled: Boolean) = store.edit { it[Keys.AMOLED] = enabled }
    suspend fun setAutoScheduleAtSunset(enabled: Boolean) = store.edit { it[Keys.AUTO_SCHEDULE] = enabled }
    suspend fun setReduceMotion(enabled: Boolean) = store.edit { it[Keys.REDUCE_MOTION] = enabled }

    val languageTag: Flow<String?> = store.data.map { it[Keys.LANGUAGE_TAG] }
    suspend fun setLanguageTag(tag: String) = store.edit { it[Keys.LANGUAGE_TAG] = tag }

    val detectionThreshold: Flow<Float> = store.data.map { it[Keys.DETECTION_THRESHOLD] ?: SpectraConstants.DEFAULT_DETECTION_CONFIDENCE_THRESHOLD }
    suspend fun setDetectionThreshold(value: Float) = store.edit { it[Keys.DETECTION_THRESHOLD] = value }

    val ocrThreshold: Flow<Float> = store.data.map { it[Keys.OCR_THRESHOLD] ?: SpectraConstants.DEFAULT_OCR_CONFIDENCE_THRESHOLD }
    suspend fun setOcrThreshold(value: Float) = store.edit { it[Keys.OCR_THRESHOLD] = value }

    val debugModeEnabled: Flow<Boolean> = store.data.map { it[Keys.DEBUG_MODE] ?: false }
    suspend fun setDebugMode(enabled: Boolean) = store.edit { it[Keys.DEBUG_MODE] = enabled }

    val voiceCommandsEnabled: Flow<Boolean> = store.data.map { it[Keys.VOICE_COMMANDS_ENABLED] ?: false }
    suspend fun setVoiceCommandsEnabled(enabled: Boolean) = store.edit { it[Keys.VOICE_COMMANDS_ENABLED] = enabled }

    val lastActiveLens: Flow<String> = store.data.map { it[Keys.LAST_ACTIVE_LENS] ?: "EXPLORE" }
    suspend fun setLastActiveLens(lensId: String) = store.edit { it[Keys.LAST_ACTIVE_LENS] = lensId }

    val onboardingComplete: Flow<Boolean> = store.data.map { it[Keys.ONBOARDING_COMPLETE] ?: false }
    suspend fun setOnboardingComplete(complete: Boolean) = store.edit { it[Keys.ONBOARDING_COMPLETE] = complete }

    val autoLockMinutes: Flow<Int> = store.data.map { it[Keys.AUTO_LOCK_MINUTES] ?: SpectraConstants.DEFAULT_AUTO_LOCK_TIMEOUT_MINUTES }
    suspend fun setAutoLockMinutes(minutes: Int) = store.edit { it[Keys.AUTO_LOCK_MINUTES] = minutes }

    val biometricLockEnabled: Flow<Boolean> = store.data.map { it[Keys.BIOMETRIC_LOCK_ENABLED] ?: false }
    suspend fun setBiometricLockEnabled(enabled: Boolean) = store.edit { it[Keys.BIOMETRIC_LOCK_ENABLED] = enabled }
}

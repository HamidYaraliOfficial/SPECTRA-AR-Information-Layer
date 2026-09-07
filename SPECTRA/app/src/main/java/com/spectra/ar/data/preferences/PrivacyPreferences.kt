package com.spectra.ar.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.spectra.ar.core.di.PrivacyPrefsStore
import com.spectra.ar.core.util.SpectraConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

data class DataRetentionSettings(
    val ocrDays: Int = SpectraConstants.DEFAULT_RETENTION_OCR_DAYS,
    val detectionDays: Int = SpectraConstants.DEFAULT_RETENTION_DETECTION_DAYS,
    val locationDays: Int = SpectraConstants.DEFAULT_RETENTION_LOCATION_DAYS,
    val sessionDays: Int = SpectraConstants.DEFAULT_RETENTION_SESSION_DAYS,
    val autoDeleteEnabled: Boolean = true
)

/** Every toggle here defaults to the most private option; nothing here defaults to "on". */
@Singleton
class PrivacyPreferences @Inject constructor(
    @PrivacyPrefsStore private val store: DataStore<Preferences>
) {
    private object Keys {
        val CLOUD_AI_ALLOWED = booleanPreferencesKey("cloud_ai_allowed")
        val ANALYTICS_CONSENT = booleanPreferencesKey("analytics_consent")
        val CRASH_REPORTING_CONSENT = booleanPreferencesKey("crash_reporting_consent")
        val RETENTION_OCR_DAYS = intPreferencesKey("retention_ocr_days")
        val RETENTION_DETECTION_DAYS = intPreferencesKey("retention_detection_days")
        val RETENTION_LOCATION_DAYS = intPreferencesKey("retention_location_days")
        val RETENTION_SESSION_DAYS = intPreferencesKey("retention_session_days")
        val AUTO_DELETE_ENABLED = booleanPreferencesKey("auto_delete_enabled")
    }

    val cloudAiAllowed: Flow<Boolean> = store.data.map { it[Keys.CLOUD_AI_ALLOWED] ?: false }
    suspend fun setCloudAiAllowed(allowed: Boolean) = store.edit { it[Keys.CLOUD_AI_ALLOWED] = allowed }

    val analyticsConsent: Flow<Boolean> = store.data.map { it[Keys.ANALYTICS_CONSENT] ?: false }
    suspend fun setAnalyticsConsent(consent: Boolean) = store.edit { it[Keys.ANALYTICS_CONSENT] = consent }

    val crashReportingConsent: Flow<Boolean> = store.data.map { it[Keys.CRASH_REPORTING_CONSENT] ?: false }
    suspend fun setCrashReportingConsent(consent: Boolean) = store.edit { it[Keys.CRASH_REPORTING_CONSENT] = consent }

    val retentionSettings: Flow<DataRetentionSettings> = store.data.map { p ->
        DataRetentionSettings(
            ocrDays = p[Keys.RETENTION_OCR_DAYS] ?: SpectraConstants.DEFAULT_RETENTION_OCR_DAYS,
            detectionDays = p[Keys.RETENTION_DETECTION_DAYS] ?: SpectraConstants.DEFAULT_RETENTION_DETECTION_DAYS,
            locationDays = p[Keys.RETENTION_LOCATION_DAYS] ?: SpectraConstants.DEFAULT_RETENTION_LOCATION_DAYS,
            sessionDays = p[Keys.RETENTION_SESSION_DAYS] ?: SpectraConstants.DEFAULT_RETENTION_SESSION_DAYS,
            autoDeleteEnabled = p[Keys.AUTO_DELETE_ENABLED] ?: true
        )
    }

    suspend fun setRetentionSettings(settings: DataRetentionSettings) = store.edit {
        it[Keys.RETENTION_OCR_DAYS] = settings.ocrDays
        it[Keys.RETENTION_DETECTION_DAYS] = settings.detectionDays
        it[Keys.RETENTION_LOCATION_DAYS] = settings.locationDays
        it[Keys.RETENTION_SESSION_DAYS] = settings.sessionDays
        it[Keys.AUTO_DELETE_ENABLED] = settings.autoDeleteEnabled
    }
}

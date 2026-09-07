package com.spectra.ar.core.util

/** App-wide constants. Kept in one place so tuning values doesn't require hunting across modules. */
object SpectraConstants {
    // Vision pipeline
    const val DEFAULT_DETECTION_CONFIDENCE_THRESHOLD = 0.55f
    const val DEFAULT_OCR_CONFIDENCE_THRESHOLD = 0.60f
    const val MIN_DETECTION_INTERVAL_MS_HIGH_POWER = 66L   // ~15 Hz on capable/plugged-in devices
    const val MIN_DETECTION_INTERVAL_MS_BALANCED = 133L    // ~7.5 Hz default
    const val MIN_DETECTION_INTERVAL_MS_BATTERY_SAVER = 400L // ~2.5 Hz under battery/thermal pressure
    const val TRACKER_MAX_MISSES_BEFORE_DROP = 8
    const val TRACKER_IOU_MATCH_THRESHOLD = 0.3f

    // Battery / thermal
    const val LOW_BATTERY_THRESHOLD_PERCENT = 20

    // Data retention defaults (days)
    const val DEFAULT_RETENTION_OCR_DAYS = 30
    const val DEFAULT_RETENTION_DETECTION_DAYS = 14
    const val DEFAULT_RETENTION_LOCATION_DAYS = 14
    const val DEFAULT_RETENTION_SESSION_DAYS = 90

    // Security
    const val DEFAULT_AUTO_LOCK_TIMEOUT_MINUTES = 5
    const val KEYSTORE_ALIAS = "spectra_master_key"
    const val ENCRYPTED_PREFS_FILE = "secure_prefs.xml"

    // Preferences DataStore names
    const val DATASTORE_APP_PREFS = "spectra_app_prefs"
    const val DATASTORE_PRIVACY_PREFS = "spectra_privacy_prefs"

    // Work tags
    const val WORK_TAG_MODEL_DOWNLOAD = "spectra_model_download"
    const val WORK_TAG_OCR_BATCH = "spectra_ocr_batch"
    const val WORK_TAG_THUMBNAIL = "spectra_thumbnail"
    const val WORK_TAG_BACKUP = "spectra_backup"
    const val WORK_TAG_INDEXING = "spectra_indexing"
    const val WORK_TAG_CLEANUP = "spectra_data_cleanup"

    const val DATABASE_NAME = "spectra.db"
}

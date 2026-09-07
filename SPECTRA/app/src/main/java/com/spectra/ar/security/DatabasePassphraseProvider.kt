package com.spectra.ar.security

import androidx.security.crypto.EncryptedSharedPreferences
import com.spectra.ar.core.util.SpectraConstants
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Generates a random SQLCipher passphrase once per install, then seals it inside
 * EncryptedSharedPreferences (itself backed by an Android Keystore key). The raw
 * passphrase is only ever held in memory for the duration of a database open call.
 */
@Singleton
class DatabasePassphraseProvider @Inject constructor(
    private val encryptedSharedPreferences: EncryptedSharedPreferences,
    private val encryptionHelper: EncryptionHelper
) {
    fun getOrCreatePassphrase(): ByteArray {
        val existing = encryptedSharedPreferences.getString(KEY_DB_PASSPHRASE, null)
        if (existing != null) return android.util.Base64.decode(existing, android.util.Base64.NO_WRAP)

        val fresh = encryptionHelper.randomBytes(32)
        val encoded = android.util.Base64.encodeToString(fresh, android.util.Base64.NO_WRAP)
        encryptedSharedPreferences.edit().putString(KEY_DB_PASSPHRASE, encoded).apply()
        return fresh
    }

    companion object {
        private const val KEY_DB_PASSPHRASE = "db_passphrase_v1"
    }
}

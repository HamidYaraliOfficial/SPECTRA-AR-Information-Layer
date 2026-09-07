package com.spectra.ar.privacy

import android.content.Context
import com.spectra.ar.core.util.SpectraPermission
import javax.inject.Inject
import javax.inject.Singleton

data class PermissionStatus(val permission: SpectraPermission, val granted: Boolean)

/** Single source of truth for "what does SPECTRA have access to right now" — backs the
 *  Privacy Center screen. Never caches grants; always reads live from the system. */
@Singleton
class PermissionCenter @Inject constructor() {
    fun statusesFor(context: Context, permissions: List<SpectraPermission> = SpectraPermission.entries): List<PermissionStatus> =
        permissions.map { PermissionStatus(it, it.isGranted(context)) }
}

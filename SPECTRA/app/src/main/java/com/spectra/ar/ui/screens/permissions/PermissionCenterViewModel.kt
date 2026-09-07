package com.spectra.ar.ui.screens.permissions

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spectra.ar.core.util.SpectraPermission
import com.spectra.ar.privacy.PermissionCenter
import com.spectra.ar.privacy.PermissionStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PermissionCenterViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val permissionCenter: PermissionCenter
) : ViewModel() {

    private val _statuses = MutableStateFlow<List<PermissionStatus>>(emptyList())
    val statuses: StateFlow<List<PermissionStatus>> = _statuses.asStateFlow()

    init { refresh() }

    fun refresh() {
        _statuses.value = permissionCenter.statusesFor(context)
    }

    /** The actual OS prompt / "open settings" deep link is triggered from the Activity via
     *  an ActivityResult launcher; this call only signals intent from the UI layer. */
    fun requestToggle(permission: SpectraPermission) {
        viewModelScope.launch { refresh() }
    }
}

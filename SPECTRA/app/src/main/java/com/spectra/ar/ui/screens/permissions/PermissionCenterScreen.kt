package com.spectra.ar.ui.screens.permissions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.spectra.ar.R
import com.spectra.ar.core.util.SpectraPermission

@Composable
fun PermissionCenterScreen(onBack: () -> Unit, viewModel: PermissionCenterViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val statuses by viewModel.statuses.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.privacy_center_title)) }) }) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
            items(statuses) { status ->
                val (titleRes, descRes) = permissionCopy(status.permission)
                ListItem(
                    headlineContent = { Text(stringResource(titleRes)) },
                    supportingContent = { Text(stringResource(descRes), style = MaterialTheme.typography.bodySmall) },
                    trailingContent = { Switch(checked = status.granted, onCheckedChange = { viewModel.requestToggle(status.permission) }) }
                )
            }
        }
    }
}

private fun permissionCopy(permission: SpectraPermission): Pair<Int, Int> = when (permission) {
    SpectraPermission.CAMERA -> R.string.privacy_permission_camera to R.string.privacy_permission_camera_desc
    SpectraPermission.MICROPHONE -> R.string.privacy_permission_microphone to R.string.privacy_permission_microphone_desc
    SpectraPermission.LOCATION_FINE, SpectraPermission.LOCATION_COARSE -> R.string.privacy_permission_location to R.string.privacy_permission_location_desc
    SpectraPermission.NOTIFICATIONS -> R.string.privacy_permission_notifications to R.string.privacy_permission_notifications_desc
}

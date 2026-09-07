package com.spectra.ar.ui.screens.memories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spectra.ar.spatial.SpatialJournalManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MemoriesViewModel @Inject constructor(
    journalManager: SpatialJournalManager
) : ViewModel() {
    val notes = journalManager.observeAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}

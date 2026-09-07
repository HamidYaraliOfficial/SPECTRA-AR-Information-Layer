package com.spectra.ar.ui.screens.openinghours

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spectra.ar.data.database.dao.OpeningHoursDao
import com.spectra.ar.data.database.entities.OpeningHoursEntity
import com.spectra.ar.data.database.entities.OpeningHoursOwnerType
import com.spectra.ar.openinghours.DayHours
import com.spectra.ar.openinghours.OpeningHours
import com.spectra.ar.openinghours.OpeningHoursCalculator
import com.spectra.ar.openinghours.OpeningHoursSnapshot
import com.spectra.ar.openinghours.RemainingDuration
import com.spectra.ar.openinghours.TimeRange
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject
import java.time.LocalDateTime

data class OpeningHoursEditorUiState(
    val ownerId: String = "",
    val hours: OpeningHours = OpeningHours.empty(),
    val livePreview: OpeningHoursSnapshot? = null,
    val previewRemaining: RemainingDuration? = null,
    val isSaving: Boolean = false
)

@HiltViewModel
class OpeningHoursEditorViewModel @Inject constructor(
    private val dao: OpeningHoursDao,
    private val calculator: OpeningHoursCalculator,
    private val json: Json
) : ViewModel() {

    private val _uiState = MutableStateFlow(OpeningHoursEditorUiState())
    val uiState: StateFlow<OpeningHoursEditorUiState> = _uiState.asStateFlow()

    fun load(ownerId: String) {
        _uiState.value = _uiState.value.copy(ownerId = ownerId)
        viewModelScope.launch {
            val existing = dao.get(ownerId)
            val hours = existing?.let { runCatching { json.decodeFromString(OpeningHours.serializer(), it.openingHoursJson) }.getOrNull() } ?: OpeningHours.empty()
            updateHours(hours)
        }
    }

    fun updateDay(dayOfWeek: Int, transform: (DayHours) -> DayHours) {
        val current = _uiState.value.hours
        val updatedDays = current.days.map { if (it.dayOfWeek == dayOfWeek) transform(it) else it }
        updateHours(current.copy(days = updatedDays, isConfigured = true))
    }

    fun copyDayToAll(dayOfWeek: Int) {
        val source = _uiState.value.hours.days.firstOrNull { it.dayOfWeek == dayOfWeek } ?: return
        val current = _uiState.value.hours
        val updatedDays = current.days.map { source.copy(dayOfWeek = it.dayOfWeek) }
        updateHours(current.copy(days = updatedDays, isConfigured = true))
    }

    fun addRange(dayOfWeek: Int, openMinutes: Int, closeMinutes: Int) = updateDay(dayOfWeek) { day ->
        day.copy(isClosed = false, ranges = day.ranges + TimeRange(openMinutes, closeMinutes))
    }

    fun removeRange(dayOfWeek: Int, index: Int) = updateDay(dayOfWeek) { day ->
        day.copy(ranges = day.ranges.filterIndexed { i, _ -> i != index })
    }

    fun setClosed(dayOfWeek: Int, closed: Boolean) = updateDay(dayOfWeek) { it.copy(isClosed = closed, isOpen24Hours = false) }
    fun setOpen24Hours(dayOfWeek: Int, open24: Boolean) = updateDay(dayOfWeek) { it.copy(isOpen24Hours = open24, isClosed = false) }

    fun save() {
        val state = _uiState.value
        _uiState.value = state.copy(isSaving = true)
        viewModelScope.launch {
            dao.upsert(
                OpeningHoursEntity(
                    ownerId = state.ownerId,
                    ownerType = OpeningHoursOwnerType.MARKER,
                    openingHoursJson = json.encodeToString(OpeningHours.serializer(), state.hours),
                    updatedAtEpochMillis = System.currentTimeMillis()
                )
            )
            _uiState.value = _uiState.value.copy(isSaving = false)
        }
    }

    private fun updateHours(hours: OpeningHours) {
        val snapshot = calculator.computeSnapshot(hours, LocalDateTime.now())
        val remaining = snapshot.minutesUntilChange?.let { calculator.toRemainingDuration(it) }
        _uiState.value = _uiState.value.copy(hours = hours, livePreview = snapshot, previewRemaining = remaining)
    }
}

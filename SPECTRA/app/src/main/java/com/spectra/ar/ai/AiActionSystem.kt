package com.spectra.ar.ai

import com.spectra.ar.location.PlacesRepository
import com.spectra.ar.data.database.entities.PlaceSource
import com.spectra.ar.spatial.SpatialJournalManager
import com.spectra.ar.tasks.ArTaskManager
import javax.inject.Inject
import javax.inject.Singleton

/** Executes an [AiSuggestedAction] only after the caller has surfaced a confirmation UI
 *  (ai_action_confirm_title/body) and the user has approved it — this class never runs
 *  a side-effecting action on its own initiative. */
@Singleton
class AiActionSystem @Inject constructor(
    private val journalManager: SpatialJournalManager,
    private val taskManager: ArTaskManager,
    private val placesRepository: PlacesRepository
) {
    suspend fun execute(action: AiSuggestedAction, markerId: String? = null): Result<Unit> = runCatching {
        when (action.type) {
            AiActionType.CREATE_NOTE -> journalManager.addNote(markerId, action.payload["body"] ?: action.summary)
            AiActionType.CREATE_TASK -> taskManager.createTask(title = action.payload["title"] ?: action.summary, markerId = markerId)
            AiActionType.CREATE_REMINDER -> journalManager.addNote(
                markerId, action.payload["body"] ?: action.summary,
                remindAt = action.payload["remindAtEpochMillis"]?.toLongOrNull()
            )
            AiActionType.ADD_PLACE -> {
                val lat = action.payload["lat"]?.toDoubleOrNull()
                val lng = action.payload["lng"]?.toDoubleOrNull()
                if (lat != null && lng != null) {
                    placesRepository.savePlace(action.payload["name"] ?: action.summary, action.payload["category"], lat, lng, action.payload["address"], PlaceSource.USER_ADDED)
                }
            }
            AiActionType.TRANSLATE, AiActionType.SEARCH, AiActionType.ADD_BOOKMARK -> {
                // Handled directly by the UI layer (opens Translate sheet / Search / Bookmarks);
                // nothing persisted here beyond what the user explicitly confirms downstream.
            }
        }
    }
}

package com.spectra.ar.ar

import com.google.ar.core.Anchor
import com.google.ar.core.HitResult
import com.google.ar.core.Session
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Tracks the mapping between SPECTRA marker IDs and live ARCore [Anchor] objects for the
 * current session, and handles anchor lifecycle (creation, disposal, re-attachment after
 * tracking loss). Persisted marker poses live in Room (ArMarkerEntity); this class only
 * holds the *live* in-session anchors.
 */
@Singleton
class AnchorManager @Inject constructor() {

    private val liveAnchors = ConcurrentHashMap<String, Anchor>()

    fun createAnchorFromHit(markerId: String, hitResult: HitResult): Anchor {
        val anchor = hitResult.createAnchor()
        liveAnchors[markerId]?.detach()
        liveAnchors[markerId] = anchor
        return anchor
    }

    fun createAnchorFromPose(session: Session, markerId: String, pose: com.google.ar.core.Pose): Anchor {
        val anchor = session.createAnchor(pose)
        liveAnchors[markerId]?.detach()
        liveAnchors[markerId] = anchor
        return anchor
    }

    fun get(markerId: String): Anchor? = liveAnchors[markerId]

    fun allTracking(): List<Pair<String, Anchor>> =
        liveAnchors.entries
            .filter { it.value.trackingState == com.google.ar.core.TrackingState.TRACKING }
            .map { it.key to it.value }

    fun detach(markerId: String) {
        liveAnchors.remove(markerId)?.detach()
    }

    fun detachAll() {
        liveAnchors.values.forEach { it.detach() }
        liveAnchors.clear()
    }
}

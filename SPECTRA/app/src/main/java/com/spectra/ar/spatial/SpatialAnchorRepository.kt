package com.spectra.ar.spatial

import com.spectra.ar.data.database.dao.ArMarkerDao
import com.spectra.ar.data.database.entities.ArMarkerEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/** Persistence for the Personal AR Memory Layer's spatial anchors — the Room-backed
 *  counterpart to the live, in-session [com.spectra.ar.ar.AnchorManager]. */
@Singleton
class SpatialAnchorRepository @Inject constructor(
    private val dao: ArMarkerDao
) {
    fun observeAll(): Flow<List<ArMarkerEntity>> = dao.observeAll()
    fun observeForLens(lensId: String): Flow<List<ArMarkerEntity>> = dao.observeByLens(lensId)
    suspend fun save(marker: ArMarkerEntity) = dao.upsert(marker)
    suspend fun get(id: String): ArMarkerEntity? = dao.getById(id)
    suspend fun delete(id: String) = dao.deleteById(id)
}

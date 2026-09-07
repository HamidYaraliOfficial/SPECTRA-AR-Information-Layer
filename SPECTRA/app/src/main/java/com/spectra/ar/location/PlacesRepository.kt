package com.spectra.ar.location

import com.spectra.ar.data.database.dao.PlaceDao
import com.spectra.ar.data.database.entities.PlaceEntity
import com.spectra.ar.data.database.entities.PlaceSource
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/** Point of Interest layer. Provider lookups (e.g. nearby restaurants) are never persisted
 *  unless the user explicitly saves a place — see [persistedByUserConsent]. */
@Singleton
class PlacesRepository @Inject constructor(
    private val placeDao: PlaceDao
) {
    fun observeSavedPlaces(): Flow<List<PlaceEntity>> = placeDao.observeAll()

    suspend fun savePlace(name: String, category: String?, lat: Double, lng: Double, address: String?, source: PlaceSource) {
        placeDao.upsert(
            PlaceEntity(
                id = UUID.randomUUID().toString(),
                name = name, category = category, latitude = lat, longitude = lng, address = address,
                source = source, persistedByUserConsent = true,
                createdAtEpochMillis = System.currentTimeMillis()
            )
        )
    }

    suspend fun nearby(lat: Double, lng: Double, radiusDegrees: Double = 0.02): List<PlaceEntity> =
        placeDao.findWithinBoundingBox(lat - radiusDegrees, lat + radiusDegrees, lng - radiusDegrees, lng + radiusDegrees)

    suspend fun delete(id: String) = placeDao.deleteById(id)
}

package com.spectra.ar.spatial

import com.google.common.truth.Truth.assertThat
import com.spectra.ar.data.database.dao.ArMarkerDao
import com.spectra.ar.data.database.entities.ArMarkerEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SpatialAnchorRepositoryTest {

    private fun marker(id: String) = ArMarkerEntity(
        id = id, cloudAnchorId = null, label = "test", latitude = null, longitude = null, altitude = null,
        poseTranslationX = 0f, poseTranslationY = 0f, poseTranslationZ = 0f,
        poseRotationQx = 0f, poseRotationQy = 0f, poseRotationQz = 0f, poseRotationQw = 1f,
        lensId = "EXPLORE", tag = null, createdAtEpochMillis = 0L, updatedAtEpochMillis = 0L
    )

    @Test
    fun `save delegates to dao upsert`() = runTest {
        val dao = mockk<ArMarkerDao>(relaxed = true)
        val repository = SpatialAnchorRepository(dao)
        val m = marker("m1")

        repository.save(m)

        coVerify { dao.upsert(m) }
    }

    @Test
    fun `observeAll returns dao flow`() = runTest {
        val dao = mockk<ArMarkerDao>()
        val expected = listOf(marker("m1"), marker("m2"))
        coEvery { dao.observeAll() } returns flowOf(expected)
        val repository = SpatialAnchorRepository(dao)

        val result = repository.observeAll()

        result.collect { assertThat(it).isEqualTo(expected) }
    }

    @Test
    fun `delete delegates to dao deleteById`() = runTest {
        val dao = mockk<ArMarkerDao>(relaxed = true)
        val repository = SpatialAnchorRepository(dao)

        repository.delete("m1")

        coVerify { dao.deleteById("m1") }
    }
}

package com.spectra.ar.search

import com.spectra.ar.data.database.dao.SearchDao
import javax.inject.Inject
import javax.inject.Singleton

/** Local, offline full-text search across Notes, Tasks, OCR text and Places, backed by
 *  SQLite FTS4 (see data/database/entities/SearchFtsEntities.kt). */
@Singleton
class SearchEngine @Inject constructor(
    private val searchDao: SearchDao
) {
    suspend fun search(query: String, kinds: Set<SearchResultKind> = SearchResultKind.entries.toSet()): List<SearchResult> {
        if (query.isBlank()) return emptyList()
        val ftsQuery = "$query*"
        val results = mutableListOf<SearchResult>()

        if (SearchResultKind.NOTE in kinds) {
            results += searchDao.searchNotes(ftsQuery).map { SearchResult(SearchResultKind.NOTE, it.id, it.body.take(60), it.body) }
        }
        if (SearchResultKind.TASK in kinds) {
            results += searchDao.searchTasks(ftsQuery).map { SearchResult(SearchResultKind.TASK, it.id, it.title, it.notes.orEmpty()) }
        }
        if (SearchResultKind.OCR_TEXT in kinds) {
            results += searchDao.searchOcr(ftsQuery).map { SearchResult(SearchResultKind.OCR_TEXT, it.id, it.content.take(60), it.content) }
        }
        if (SearchResultKind.PLACE in kinds) {
            results += searchDao.searchPlaces(ftsQuery).map { SearchResult(SearchResultKind.PLACE, it.id, it.name, it.address.orEmpty()) }
        }
        return results
    }
}

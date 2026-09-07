package com.spectra.ar.search

enum class SearchResultKind { NOTE, TASK, OCR_TEXT, PLACE }
data class SearchResult(val kind: SearchResultKind, val id: String, val title: String, val snippet: String)

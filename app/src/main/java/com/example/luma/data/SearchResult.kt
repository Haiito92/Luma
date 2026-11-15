package com.example.luma.data

sealed interface SearchResult {
    data class Success(val places: List<Place>) : SearchResult
    object NoConnection : SearchResult
    object ServiceUnavailable : SearchResult      // server error, throttled, bad data
}

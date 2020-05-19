package dev.xtr.suggestivetext.sample.api

import kotlinx.serialization.Serializable

@Serializable
data class SearchResult (
	val artistName: String,
	val trackName: String
)
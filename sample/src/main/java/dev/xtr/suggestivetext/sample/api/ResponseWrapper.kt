package dev.xtr.suggestivetext.sample.api

import kotlinx.serialization.Serializable

@Serializable
data class ResponseWrapper(
	val resultCount : Int,
	val results : List<SearchResult>
)
package dev.xtr.suggestivetext.sample.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.request.get
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

class ApiClient {
    @UnstableDefault
    private val json = Json(JsonConfiguration(ignoreUnknownKeys = true))
    private val client = HttpClient(Android)

    suspend fun search(query: String): List<SearchResult> {
        val responseString =
            client.get<String>("https://itunes.apple.com/search?term=$query&entity=musicVideo")
        val response = json.parse(ResponseWrapper.serializer(), responseString)
        return response.results
    }
}
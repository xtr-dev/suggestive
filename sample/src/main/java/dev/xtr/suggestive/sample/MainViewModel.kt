package dev.xtr.suggestivetext.sample

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.xtr.suggestivetext.sample.api.ApiClient
import dev.xtr.suggestivetext.sample.api.SearchResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val api = ApiClient()
    private var searchJob: Job = Job()
    val results = MutableLiveData<List<SearchResult>>()

    fun search(query: String) {
        searchJob.cancel()
        searchJob = viewModelScope.launch {
            val list = api.search(query)
            results.postValue(list)
        }
    }
}
package com.example.fetchdata.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _searchQuery = MutableLiveData<String>()
    val searchQuery: LiveData<String> = _searchQuery

    private val _shouldResetToDefault = MutableLiveData<Boolean>()
    val shouldResetToDefault: LiveData<Boolean> = _shouldResetToDefault

    private val _currentTab = MutableLiveData<Int>(0)
    val currentTab: LiveData<Int> = _currentTab

    private var searchJob: Job? = null
    private var lastSearchQuery = ""

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 300L
        private const val MIN_SEARCH_LENGTH = 2
    }

    fun setCurrentTab(position: Int) {
        _currentTab.value = position
    }

    fun onSearchQueryChanged(query: String) {
        val trimmedQuery = query.trim()
        android.util.Log.d("HomeViewModel", "Search query changed: '$trimmedQuery' (length: ${trimmedQuery.length})")
        searchJob?.cancel()

        when {
            trimmedQuery.isEmpty() -> {
                android.util.Log.d("HomeViewModel", "Query empty, triggering reset")
                _shouldResetToDefault.value = true
                lastSearchQuery = ""
            }
            trimmedQuery.length >= MIN_SEARCH_LENGTH -> {
                // Only search if query is at least 2 characters, with debounce
                android.util.Log.d("HomeViewModel", "Query length >= $MIN_SEARCH_LENGTH, scheduling search")
                searchJob = viewModelScope.launch {
                    delay(SEARCH_DEBOUNCE_DELAY)
                    if (trimmedQuery != lastSearchQuery) {
                        lastSearchQuery = trimmedQuery
                        _searchQuery.value = trimmedQuery
                    }
                }
            }
            else -> {
                // Query is too short, do nothing
                android.util.Log.d("HomeViewModel", "Query too short, ignoring")
            }
        }
    }

    fun onSearchQuerySubmitted(query: String) {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isNotEmpty()) {
            lastSearchQuery = trimmedQuery
            _searchQuery.value = trimmedQuery
        } else {
            _shouldResetToDefault.value = true
        }
    }

    fun resetSearchState() {
        _shouldResetToDefault.value = false
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
    }
}


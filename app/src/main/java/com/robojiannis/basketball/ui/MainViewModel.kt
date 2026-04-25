package com.robojiannis.basketball.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.robojiannis.basketball.data.AppDatabase
import com.robojiannis.basketball.data.Match
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val matchDao = AppDatabase.getDatabase(application).matchDao()
    private val sharedPrefs = application.getSharedPreferences("basketball_prefs", Context.MODE_PRIVATE)

    val allMatches: StateFlow<List<Match>> = matchDao.getAllMatches()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _quartersPerMatch = MutableStateFlow(sharedPrefs.getInt("quarters_per_match", 4))
    val quartersPerMatch = _quartersPerMatch.asStateFlow()

    fun updateQuartersPerMatch(quarters: Int) {
        viewModelScope.launch {
            sharedPrefs.edit().putInt("quarters_per_match", quarters).apply()
            _quartersPerMatch.value = quarters
        }
    }

    fun saveMatch(match: Match) {
        viewModelScope.launch {
            matchDao.insertMatch(match)
        }
    }

    fun deleteMatch(match: Match) {
        viewModelScope.launch {
            matchDao.deleteMatch(match)
        }
    }

    suspend fun getMatchById(id: Int): Match? {
        return matchDao.getMatchById(id)
    }

    fun clearData() {
        viewModelScope.launch {
            matchDao.deleteAllMatches()
        }
    }
}

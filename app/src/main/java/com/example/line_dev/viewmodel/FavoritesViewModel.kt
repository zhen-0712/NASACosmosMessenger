package com.example.line_dev.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.line_dev.data.local.FavoriteEntity
import com.example.line_dev.data.repository.FavoriteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FavoriteRepository(application)

    val favorites: StateFlow<List<FavoriteEntity>> = repository
        .getAllFavorites()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteFavorite(entity: FavoriteEntity) {
        viewModelScope.launch {
            repository.removeFavorite(entity)
        }
    }
}
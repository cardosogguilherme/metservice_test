package com.cardosogui.citybikesexplorer.favorites

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/** In-memory store of favorited station ids. Resets when the process dies. */
interface FavoritesRepository {
    val favoriteIds: StateFlow<Set<String>>
    fun toggle(stationId: String)
}

@Singleton
class InMemoryFavoritesRepository @Inject constructor() : FavoritesRepository {

    private val _favoriteIds = MutableStateFlow<Set<String>>(emptySet())
    override val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    override fun toggle(stationId: String) {
        _favoriteIds.update { current ->
            if (stationId in current) current - stationId else current + stationId
        }
    }
}

package com.example.myapplication.Hints.ui.favourite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.Hints.model.Article
import com.example.domain.Hints.use_case.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FavouriteUiState(
    val articles: List<Article> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val searchSuggestions: List<String> = emptyList(),
    val showSuggestions: Boolean = false,
    val userId: Int = 1
)

sealed class FavouriteEvent {
    data class SearchQueryChanged(val query: String) : FavouriteEvent()
    data class Search(val query: String) : FavouriteEvent()
    data class ToggleFavorite(val articleId: Int) : FavouriteEvent()
    data class ToggleLike(val articleId: Int) : FavouriteEvent()
    data object ClearSearch : FavouriteEvent()
    data object LoadFavourites : FavouriteEvent()
}

class FavouriteViewModel(
    private val getFavoriteArticlesUseCase: GetFavoriteArticlesUseCase,
    private val removeFromFavoriteUseCase: RemoveFromFavoriteUseCase,
    private val addLikeUseCase: AddLikeUseCase,
    private val removeLikeUseCase: RemoveLikeUseCase,
    private val getLikesCountUseCase: GetLikesCountUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavouriteUiState())
    val uiState: StateFlow<FavouriteUiState> = _uiState.asStateFlow()

    private var allArticles: List<Article> = emptyList()

    init {
        loadFavourites()
    }

    fun onEvent(event: FavouriteEvent) {
        when (event) {
            is FavouriteEvent.SearchQueryChanged -> updateSuggestions(event.query)
            is FavouriteEvent.Search -> search(event.query)
            is FavouriteEvent.ToggleFavorite -> toggleFavorite(event.articleId)
            is FavouriteEvent.ToggleLike -> toggleLike(event.articleId)
            FavouriteEvent.ClearSearch -> clearSearch()
            FavouriteEvent.LoadFavourites -> loadFavourites()
        }
    }

    private fun loadFavourites() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val articles = getFavoriteArticlesUseCase(_uiState.value.userId)
                allArticles = articles

                _uiState.update {
                    it.copy(
                        articles = articles,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    private fun updateSuggestions(query: String) {
        _uiState.update { state ->
            if (query.isBlank()) {
                state.copy(
                    searchQuery = query,
                    searchSuggestions = emptyList(),
                    showSuggestions = false
                )
            } else {
                val suggestions = allArticles
                    .filter { it.title.contains(query, ignoreCase = true) }
                    .map { it.title }
                state.copy(
                    searchQuery = query,
                    searchSuggestions = suggestions,
                    showSuggestions = suggestions.isNotEmpty()
                )
            }
        }
    }

    private fun search(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(searchQuery = query, isLoading = true, showSuggestions = false) }

            if (query.isBlank()) {
                loadFavourites()
                return@launch
            }

            try {
                val results = allArticles.filter { article ->
                    article.title.contains(query, ignoreCase = true) ||
                            article.mainWords.any { it.contains(query, ignoreCase = true) }
                }
                _uiState.update {
                    it.copy(
                        articles = results,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    private fun toggleFavorite(articleId: Int) {
        viewModelScope.launch {
            val userId = _uiState.value.userId
            removeFromFavoriteUseCase(userId, articleId)

            // Удаляем из списка
            _uiState.update { state ->
                val updatedArticles = state.articles.filter { it.id != articleId }
                state.copy(articles = updatedArticles)
            }
        }
    }

    private fun toggleLike(articleId: Int) {
        viewModelScope.launch {
            val userId = _uiState.value.userId
            val article = _uiState.value.articles.find { it.id == articleId }
            val isCurrentlyLiked = article?.isLiked ?: false

            if (isCurrentlyLiked) {
                removeLikeUseCase(userId, articleId)
            } else {
                addLikeUseCase(userId, articleId)
            }

            val newLikesCount = getLikesCountUseCase(articleId)

            _uiState.update { state ->
                val updatedArticles = state.articles.map { article ->
                    if (article.id == articleId) {
                        article.copy(
                            isLiked = !isCurrentlyLiked,
                            likesCount = newLikesCount
                        )
                    } else article
                }
                state.copy(articles = updatedArticles)
            }
        }
    }

    private fun clearSearch() {
        _uiState.update { it.copy(searchQuery = "", searchSuggestions = emptyList()) }
        loadFavourites()
    }
}
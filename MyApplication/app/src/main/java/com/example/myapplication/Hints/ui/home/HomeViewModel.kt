package com.example.myapplication.Hints.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.Hints.model.Article
import com.example.domain.Hints.use_case.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val articles: List<Article> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val searchSuggestions: List<String> = emptyList(),
    val showSuggestions: Boolean = false,
    val userId: Int = 1
)

sealed class HomeEvent {
    data class SearchQueryChanged(val query: String) : HomeEvent()
    data class Search(val query: String) : HomeEvent()
    data class ToggleFavorite(val articleId: Int) : HomeEvent()
    data class ToggleLike(val articleId: Int) : HomeEvent()
    data object ClearSearch : HomeEvent()
    data object LoadArticles : HomeEvent()
}

class HomeViewModel(
    private val getArticlesUseCase: GetArticlesUseCase,
    private val getArticlesByCategoryUseCase: GetArticlesByCategoryUseCase,
    private val searchArticlesUseCase: SearchArticlesUseCase,
    private val addToFavoriteUseCase: AddToFavoriteUseCase,
    private val removeFromFavoriteUseCase: RemoveFromFavoriteUseCase,
    private val isArticleFavoriteUseCase: IsArticleFavoriteUseCase,
    private val addLikeUseCase: AddLikeUseCase,
    private val removeLikeUseCase: RemoveLikeUseCase,
    private val getLikesCountUseCase: GetLikesCountUseCase,
    private val isArticleLikedUseCase: IsArticleLikedUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var allArticles: List<Article> = emptyList()

    init {
        loadArticles()
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.SearchQueryChanged -> updateSuggestions(event.query)
            is HomeEvent.Search -> search(event.query)
            is HomeEvent.ToggleFavorite -> toggleFavorite(event.articleId)
            is HomeEvent.ToggleLike -> toggleLike(event.articleId)
            HomeEvent.ClearSearch -> clearSearch()
            HomeEvent.LoadArticles -> loadArticles()
        }
    }

    private fun loadArticles(categoryId: Int? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val articles = if (categoryId == null) {
                    getArticlesUseCase()
                } else {
                    getArticlesByCategoryUseCase(categoryId)
                }

                allArticles = articles

                val enrichedArticles = articles.map { article ->
                    article.copy(
                        isLiked = isArticleLiked(article.id),
                        isFavorite = isArticleFavorite(article.id)
                    )
                }

                _uiState.update {
                    it.copy(
                        articles = enrichedArticles,
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
                loadArticles()
                return@launch
            }

            try {
                val results = searchArticlesUseCase(query)
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

    private suspend fun isArticleFavorite(articleId: Int): Boolean =
        isArticleFavoriteUseCase(_uiState.value.userId, articleId)

    private suspend fun isArticleLiked(articleId: Int): Boolean =
        isArticleLikedUseCase(_uiState.value.userId, articleId)

    private fun toggleFavorite(articleId: Int) {
        viewModelScope.launch {
            val userId = _uiState.value.userId
            val isCurrentlyFavorite = isArticleFavoriteUseCase(userId, articleId)

            if (isCurrentlyFavorite) {
                removeFromFavoriteUseCase(userId, articleId)
            } else {
                addToFavoriteUseCase(userId, articleId)
            }

            _uiState.update { state ->
                val updatedArticles = state.articles.map { article ->
                    if (article.id == articleId) {
                        article.copy(isFavorite = !isCurrentlyFavorite)
                    } else article
                }
                state.copy(articles = updatedArticles)
            }
        }
    }

    private fun toggleLike(articleId: Int) {
        viewModelScope.launch {
            val userId = _uiState.value.userId
            val isCurrentlyLiked = isArticleLikedUseCase(userId, articleId)

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
        loadArticles()
    }
}
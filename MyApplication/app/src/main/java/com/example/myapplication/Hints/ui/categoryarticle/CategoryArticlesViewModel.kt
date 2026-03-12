package com.example.myapplication.Hints.ui.categoryarticles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.Hints.model.Article
import com.example.domain.Hints.use_case.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CategoryArticlesUiState(
    val articles: List<Article> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val searchSuggestions: List<String> = emptyList(),
    val showSuggestions: Boolean = false,
    val userId: Int = 1
)

sealed class CategoryArticlesEvent {
    data class LoadArticles(val categoryId: Int) : CategoryArticlesEvent()
    data class SearchQueryChanged(val query: String) : CategoryArticlesEvent()
    data class Search(val query: String) : CategoryArticlesEvent()
    data class ToggleFavorite(val articleId: Int) : CategoryArticlesEvent()
    data class ToggleLike(val articleId: Int) : CategoryArticlesEvent()
    data object ClearSearch : CategoryArticlesEvent()
}

class CategoryArticlesViewModel(
    private val categoryId: Int,
    private val categoryName: String,
    private val getArticlesByCategoryUseCase: GetArticlesByCategoryUseCase,
    private val searchArticlesUseCase: SearchArticlesUseCase,
    private val addToFavoriteUseCase: AddToFavoriteUseCase,
    private val removeFromFavoriteUseCase: RemoveFromFavoriteUseCase,
    private val isArticleFavoriteUseCase: IsArticleFavoriteUseCase,
    private val addLikeUseCase: AddLikeUseCase,
    private val removeLikeUseCase: RemoveLikeUseCase,
    private val isArticleLikedUseCase: IsArticleLikedUseCase,
    private val getLikesCountUseCase: GetLikesCountUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryArticlesUiState())
    val uiState: StateFlow<CategoryArticlesUiState> = _uiState.asStateFlow()

    private var allArticles: List<Article> = emptyList()

    init {
        loadArticles()
    }

    fun onEvent(event: CategoryArticlesEvent) {
        when (event) {
            is CategoryArticlesEvent.LoadArticles -> loadArticles(event.categoryId)
            is CategoryArticlesEvent.SearchQueryChanged -> updateSuggestions(event.query)
            is CategoryArticlesEvent.Search -> search(event.query)
            is CategoryArticlesEvent.ToggleFavorite -> toggleFavorite(event.articleId)
            is CategoryArticlesEvent.ToggleLike -> toggleLike(event.articleId)
            CategoryArticlesEvent.ClearSearch -> clearSearch()
        }
    }

    private fun loadArticles(categoryId: Int = this.categoryId) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val articles = getArticlesByCategoryUseCase(categoryId)
                allArticles = articles

                val enrichedArticles = articles.map { article ->
                    article.copy(
                        isLiked = isArticleLikedUseCase(_uiState.value.userId, article.id),
                        isFavorite = isArticleFavoriteUseCase(_uiState.value.userId, article.id)
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
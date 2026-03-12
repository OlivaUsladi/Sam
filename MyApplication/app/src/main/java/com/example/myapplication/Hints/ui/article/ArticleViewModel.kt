package com.example.myapplication.Hints.ui.article

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.Hints.model.ArticleContent
import com.example.domain.Hints.use_case.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class ArticleUiState(
    val articleContent: ArticleContent? = null,
    val articleTitle: String = "",
    val articleCategory: String = "Продуктивность", //Потом нормально сделать
    val articleDate: String = "01.01.2025",  //Потом нормально сделать
    val articleImageUrl: String = "",  //Потом нормально сделать
    val isLoading: Boolean = false,
    val error: String? = null,
    val isFavorite: Boolean = false,
    val isLiked: Boolean = false,
    val likesCount: Int = 0,
    val userId: Int = 1
)

sealed class ArticleEvent {
    data class LoadArticle(val articleId: Int) : ArticleEvent()
    data class ToggleFavorite(val articleId: Int) : ArticleEvent()
    data class ToggleLike(val articleId: Int) : ArticleEvent()
}

class ArticleViewModel(
    private val getArticleContentUseCase: GetArticleContentUseCase,
    private val addToFavoriteUseCase: AddToFavoriteUseCase,
    private val removeFromFavoriteUseCase: RemoveFromFavoriteUseCase,
    private val isArticleFavoriteUseCase: IsArticleFavoriteUseCase,
    private val addLikeUseCase: AddLikeUseCase,
    private val removeLikeUseCase: RemoveLikeUseCase,
    private val isArticleLikedUseCase: IsArticleLikedUseCase,
    private val getLikesCountUseCase: GetLikesCountUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ArticleUiState())
    val uiState: StateFlow<ArticleUiState> = _uiState.asStateFlow()


    private val imageUrls = listOf(
        "https://i.ibb.co/bjy899VJ/aerial-view-business-data-analysis-graph.jpg",
        "https://i.ibb.co/zTjB1k12/brunette-woman-sitting-desk-surrounded-with-gadgets-papers.jpg",
        "https://i.ibb.co/274TSKSf/close-up-person-meditating-home.jpg",
        "https://i.ibb.co/Ld1K3vgj/tea-book-relax.jpg",
        "https://i.ibb.co/gMNnL2Yp/ceramic-mug-with-coffee-silver-dollar-gum-leaves.jpg",
        "https://i.ibb.co/YF85HrRg/doctor-doing-their-work-pediatrics-office.jpg"
    )

    fun onEvent(event: ArticleEvent) {
        when (event) {
            is ArticleEvent.LoadArticle -> loadArticle(event.articleId)
            is ArticleEvent.ToggleFavorite -> toggleFavorite(event.articleId)
            is ArticleEvent.ToggleLike -> toggleLike(event.articleId)
        }
    }

    private fun loadArticle(articleId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val content = getArticleContentUseCase(articleId)

                val userId = _uiState.value.userId
                val isFavorite = isArticleFavoriteUseCase(userId, articleId)
                val isLiked = isArticleLikedUseCase(userId, articleId)
                val likesCount = getLikesCountUseCase(articleId)

                _uiState.update {
                    it.copy(
                        articleContent = content,
                        articleImageUrl = imageUrls.getOrNull(articleId - 1) ?: "",
                        isLoading = false,
                        isFavorite = isFavorite,
                        isLiked = isLiked,
                        likesCount = likesCount
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

            _uiState.update {
                it.copy(isFavorite = !isCurrentlyFavorite)
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

            _uiState.update {
                it.copy(
                    isLiked = !isCurrentlyLiked,
                    likesCount = newLikesCount
                )
            }
        }
    }
}
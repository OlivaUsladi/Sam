package com.example.myapplication.Hints.ui.article

import com.example.domain.Hints.model.Article
import com.example.domain.Hints.model.ArticleContent
import com.example.domain.Hints.model.Favorite
import com.example.domain.Hints.model.Like
import com.example.domain.Hints.use_case.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class ArticleViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private val getContent: GetArticleContentUseCase = mock()
    private val getArticle: GetArticleUseCase = mock()
    private val addFav: AddToFavoriteUseCase = mock()
    private val removeFav: RemoveFromFavoriteUseCase = mock()
    private val isFav: IsArticleFavoriteUseCase = mock()
    private val addLike: AddLikeUseCase = mock()
    private val removeLike: RemoveLikeUseCase = mock()
    private val isLiked: IsArticleLikedUseCase = mock()
    private val getLikes: GetLikesCountUseCase = mock()

    private lateinit var vm: ArticleViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        vm = ArticleViewModel(
                getContent, getArticle, addFav, removeFav, isFav,
                addLike, removeLike, isLiked, getLikes)
    }

    @After fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `loadArticle populates content and flags`() = runTest(dispatcher) {
        val content = mock<ArticleContent>()
        whenever(getContent.invoke(1)).thenReturn(content)
        whenever(isFav.invoke(1, 1)).thenReturn(true)
        whenever(isLiked.invoke(1, 1)).thenReturn(false)
        whenever(getLikes.invoke(1)).thenReturn(15)

        vm.onEvent(ArticleEvent.LoadArticle(1))
        advanceUntilIdle()

        val s = vm.uiState.value
        assertEquals(content, s.articleContent)
        assertTrue(s.isFavorite)
        assertFalse(s.isLiked)
        assertEquals(15, s.likesCount)
        assertFalse(s.isLoading)
    }

    @Test
    fun `loadArticle on error keeps error message`() = runTest(dispatcher) {
        whenever(getContent.invoke(1)).thenThrow(RuntimeException("Network err"))

        vm.onEvent(ArticleEvent.LoadArticle(1))
        advanceUntilIdle()

        assertEquals("Network err", vm.uiState.value.error)
        assertFalse(vm.uiState.value.isLoading)
    }

    @Test
    fun `toggleFavorite when not fav adds favorite`() = runTest(dispatcher) {
        whenever(isFav.invoke(1, 1)).thenReturn(false)
        whenever(addFav.invoke(1, 1)).thenReturn(mock<Favorite>())

        vm.onEvent(ArticleEvent.ToggleFavorite(1))
        advanceUntilIdle()

        verify(addFav).invoke(1, 1)
        assertTrue(vm.uiState.value.isFavorite)
    }

    @Test
    fun `toggleFavorite when fav removes favorite`() = runTest(dispatcher) {
        whenever(isFav.invoke(1, 1)).thenReturn(true)

        vm.onEvent(ArticleEvent.ToggleFavorite(1))
        advanceUntilIdle()

        verify(removeFav).invoke(1, 1)
        assertFalse(vm.uiState.value.isFavorite)
    }

    @Test
    fun `toggleLike when not liked adds like and increments count`() = runTest(dispatcher) {
        whenever(getContent.invoke(1)).thenReturn(mock())
        whenever(isFav.invoke(1, 1)).thenReturn(false)
        whenever(isLiked.invoke(1, 1)).thenReturn(false).thenReturn(false)
        whenever(getLikes.invoke(1)).thenReturn(5)
        whenever(addLike.invoke(1, 1)).thenReturn(mock<Like>())

        vm.onEvent(ArticleEvent.LoadArticle(1))
        advanceUntilIdle()
        vm.onEvent(ArticleEvent.ToggleLike(1))
        advanceUntilIdle()

        verify(addLike).invoke(1, 1)
        assertEquals(6, vm.uiState.value.likesCount)
        assertTrue(vm.uiState.value.isLiked)
    }

    @Test
    fun `toggleLike when liked removes like and decrements count`() = runTest(dispatcher) {
        whenever(getContent.invoke(1)).thenReturn(mock())
        whenever(isFav.invoke(1, 1)).thenReturn(false)
        whenever(isLiked.invoke(1, 1)).thenReturn(true).thenReturn(true)
        whenever(getLikes.invoke(1)).thenReturn(5)

        vm.onEvent(ArticleEvent.LoadArticle(1))
        advanceUntilIdle()
        vm.onEvent(ArticleEvent.ToggleLike(1))
        advanceUntilIdle()

        verify(removeLike).invoke(1, 1)
        assertEquals(4, vm.uiState.value.likesCount)
        assertFalse(vm.uiState.value.isLiked)
    }
}

package com.example.data.Hints

import com.example.data.Hints.datasource.remote.ArticleRemoteDataSource
import com.example.data.Hints.datasource.remote.api.ArticleApiService
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import retrofit2.Response

class ArticleRemoteDataSourceTest {

    private lateinit var apiService: ArticleApiService
    private lateinit var dataSource: ArticleRemoteDataSource

    @Before
    fun setUp() {
        apiService = Mockito.mock(ArticleApiService::class.java)
        dataSource = ArticleRemoteDataSource(apiService)
    }

    @Test
    fun `isFavourite returns true only for successful true body`() {
        runBlocking {
            Mockito.`when`(apiService.isFavourite(1, 1)).thenReturn(Response.success(true))
            Assert.assertTrue(dataSource.isFavourite(1, 1))
        }
    }

    @Test
    fun `isFavourite returns false for successful false body`() {
        runBlocking {
            Mockito.`when`(apiService.isFavourite(1, 1)).thenReturn(Response.success(false))
            Assert.assertFalse(dataSource.isFavourite(1, 1))
        }
    }

    @Test
    fun `isFavourite returns false for unsuccessful response`() {
        runBlocking {
            Mockito.`when`(apiService.isFavourite(1, 1)).thenReturn(Response.error(404, "".toResponseBody(null)))
            Assert.assertFalse(dataSource.isFavourite(1, 1))
        }
    }

    @Test
    fun `isLiked returns true only for successful true body`() {
        runBlocking {
            Mockito.`when`(apiService.isLiked(2, 1)).thenReturn(Response.success(true))
            Assert.assertTrue(dataSource.isLiked(2, 1))
        }
    }

    @Test
    fun `isLiked returns false for unsuccessful response`() {
        runBlocking {
            Mockito.`when`(apiService.isLiked(2, 1)).thenReturn(Response.error(500, "".toResponseBody(null)))
            Assert.assertFalse(dataSource.isLiked(2, 1))
        }
    }

    @Test
    fun `getLikesCount returns body value when successful`() {
        runBlocking {
            Mockito.`when`(apiService.getLikesCount(2)).thenReturn(Response.success(7L))
            Assert.assertEquals(7, dataSource.getLikesCount(2))
        }
    }

    @Test
    fun `getLikesCount returns zero when body is null`() {
        runBlocking {
            Mockito.`when`(apiService.getLikesCount(2)).thenReturn(Response.success(null))
            Assert.assertEquals(0, dataSource.getLikesCount(2))
        }
    }

    @Test
    fun `getLikesCount returns zero when response unsuccessful`() {
        runBlocking {
            Mockito.`when`(apiService.getLikesCount(2)).thenReturn(Response.error(500, "".toResponseBody(null)))
            Assert.assertEquals(0, dataSource.getLikesCount(2))
        }
    }

    @Test(expected = IllegalStateException::class)
    fun `addToFavourites throws when response unsuccessful`() {
        runBlocking {
            Mockito.`when`(apiService.addToFavourites(3, 1)).thenReturn(Response.error(400, "".toResponseBody(null)))
            dataSource.addToFavourites(3, 1)
        }
    }

    @Test
    fun `addToFavourites completes when response successful`() {
        runBlocking {
            Mockito.`when`(apiService.addToFavourites(3, 1)).thenReturn(Response.success(Unit))
            dataSource.addToFavourites(3, 1)
            Mockito.verify(apiService).addToFavourites(3, 1)
        }
    }

    @Test(expected = IllegalStateException::class)
    fun `removeFromFavourites throws when response unsuccessful`() {
        runBlocking {
            Mockito.`when`(apiService.removeFromFavourites(3, 1)).thenReturn(Response.error(400, "".toResponseBody(null)))
            dataSource.removeFromFavourites(3, 1)
        }
    }

    @Test(expected = IllegalStateException::class)
    fun `addLike throws when response unsuccessful`() {
        runBlocking {
            Mockito.`when`(apiService.addLike(4, 1)).thenReturn(Response.error(400, "".toResponseBody(null)))
            dataSource.addLike(4, 1)
        }
    }

    @Test(expected = IllegalStateException::class)
    fun `removeLike throws when response unsuccessful`() {
        runBlocking {
            Mockito.`when`(apiService.removeLike(4, 1)).thenReturn(Response.error(400, "".toResponseBody(null)))
            dataSource.removeLike(4, 1)
        }
    }
}
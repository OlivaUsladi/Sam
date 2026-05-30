import com.example.backend.dto.ArticleCategoryResponseDto;
import com.example.backend.dto.ArticleDetailResponseDto;
import com.example.backend.dto.ArticleResponseDto;
import com.example.backend.entity.ArticleCategoryEntity;
import com.example.backend.entity.ArticleContentEntity;
import com.example.backend.entity.ArticleEntity;
import com.example.backend.entity.FavouriteArticleEntity;
import com.example.backend.entity.LikeArticleEntity;
import com.example.backend.mapper.ArticleMapper;
import com.example.backend.repository.*;
import com.example.backend.service.ArticleService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @Mock private ArticleRepository articleRepository;
    @Mock private ArticleCategoryRepository categoryRepository;
    @Mock private ArticleContentRepository contentRepository;
    @Mock private FavouriteArticleRepository favouriteRepository;
    @Mock private LikeArticleRepository likeRepository;
    @Mock private ArticleMapper mapper;

    @InjectMocks
    private ArticleService articleService;

    private ArticleEntity article(int id, int catId, String title) {
        ArticleEntity a = new ArticleEntity();
        a.setId(id);
        a.setCategoryId(catId);
        a.setTitle(title);
        a.setLikesCount(0);
        return a;
    }

    private ArticleCategoryEntity category(int id, String name) {
        ArticleCategoryEntity c = new ArticleCategoryEntity();
        c.setId(id); c.setName(name);
        return c;
    }

    private ArticleResponseDto sampleArticleDto(int id) {
        return new ArticleResponseDto(
                id, "T",
                new ArticleCategoryResponseDto(1, "C", null),
                List.of(), "A", null, null, null, 0, false, false);
    }

    private ArticleDetailResponseDto sampleArticleDetailDto(int id) {
        return new ArticleDetailResponseDto(
                id, "T",
                new ArticleCategoryResponseDto(1, "C", null),
                List.of(), "A", null, null, null, 0, true, false,
                List.of(), null);
    }

    // ----- CATEGORIES -----

    @Test
    void getCategories_returnsAllMapped() {
        when(categoryRepository.findAll()).thenReturn(List.of(category(1, "Финансы"), category(2, "Учёба")));
        when(mapper.toDto(any(ArticleCategoryEntity.class)))
                .thenReturn(new ArticleCategoryResponseDto(1, "Финансы", null));

        List<ArticleCategoryResponseDto> r = articleService.getCategories();
        assertEquals(2, r.size());
    }


    @Test
    void getArticles_returnsAll() {
        when(articleRepository.findAllByOrderByCreatedAtDesc())
                .thenReturn(List.of(article(1, 1, "Бюджет")));
        when(categoryRepository.findAllById(any())).thenReturn(List.of(category(1, "Финансы")));
        when(favouriteRepository.findByUserId(1)).thenReturn(List.of());
        when(likeRepository.findByUserId(1)).thenReturn(List.of());
        when(mapper.toDto(any(ArticleEntity.class), any(), anyBoolean(), anyBoolean()))
                .thenReturn(sampleArticleDto(1));

        List<ArticleResponseDto> r = articleService.getArticles(1);
        assertEquals(1, r.size());
    }

    @Test
    void getArticlesByCategory_filtersByCategory() {
        when(articleRepository.findByCategoryIdOrderByCreatedAtDesc(2))
                .thenReturn(List.of(article(1, 2, "Учёба")));
        when(categoryRepository.findAllById(any())).thenReturn(List.of(category(2, "Учёба")));
        when(favouriteRepository.findByUserId(1)).thenReturn(List.of());
        when(likeRepository.findByUserId(1)).thenReturn(List.of());
        when(mapper.toDto(any(ArticleEntity.class), any(), anyBoolean(), anyBoolean()))
                .thenReturn(sampleArticleDto(1));

        List<ArticleResponseDto> r = articleService.getArticlesByCategory(1, 2);
        assertEquals(1, r.size());
    }

    @Test
    void searchArticles_withBlankQuery_returnsAll() {
        when(articleRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of());
        List<ArticleResponseDto> r = articleService.searchArticles(1, "  ");
        assertNotNull(r);
        verify(articleRepository).findAllByOrderByCreatedAtDesc();
    }

    @Test
    void searchArticles_byQuery_callsSearch() {
        when(articleRepository.searchByQuery("ипотека")).thenReturn(List.of());
        articleService.searchArticles(1, "ипотека");
        verify(articleRepository).searchByQuery("ипотека");
    }

    @Test
    void getArticle_whenMissing_throws() {
        when(articleRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> articleService.getArticle(1, 99));
    }

    @Test
    void getArticle_returnsDetailWithFlags() {
        ArticleEntity a = article(1, 5, "X");
        ArticleCategoryEntity c = category(5, "C");
        ArticleContentEntity content = new ArticleContentEntity();
        when(articleRepository.findById(1)).thenReturn(Optional.of(a));
        when(categoryRepository.findById(5)).thenReturn(Optional.of(c));
        when(contentRepository.findById(1)).thenReturn(Optional.of(content));
        when(favouriteRepository.existsByUserIdAndArticleId(1, 1)).thenReturn(true);
        when(likeRepository.existsByUserIdAndArticleId(1, 1)).thenReturn(false);
        when(mapper.toDetailDto(a, c, content, true, false))
                .thenReturn(sampleArticleDetailDto(1));

        ArticleDetailResponseDto resp = articleService.getArticle(1, 1);
        assertNotNull(resp);
    }

    // ----- FAVOURITES -----

    @Test
    void getFavouriteArticles_returnsByIds() {
        FavouriteArticleEntity f = new FavouriteArticleEntity();
        f.setUserId(1); f.setArticleId(10);
        when(favouriteRepository.findByUserId(1)).thenReturn(List.of(f));
        when(articleRepository.findAllById(List.of(10))).thenReturn(List.of(article(10, 1, "X")));
        when(categoryRepository.findAllById(any())).thenReturn(List.of(category(1, "C")));
        when(likeRepository.findByUserId(1)).thenReturn(List.of());
        when(mapper.toDto(any(ArticleEntity.class), any(), anyBoolean(), anyBoolean()))
                .thenReturn(sampleArticleDto(10));

        List<ArticleResponseDto> r = articleService.getFavouriteArticles(1);
        assertEquals(1, r.size());
    }

    @Test
    void getFavouriteArticles_whenEmpty_returnsEmpty() {
        when(favouriteRepository.findByUserId(1)).thenReturn(List.of());

        List<ArticleResponseDto> r = articleService.getFavouriteArticles(1);
        assertTrue(r.isEmpty());
    }

    @Test
    void addToFavourites_whenAlready_doesNotDouble() {
        when(articleRepository.findById(1)).thenReturn(Optional.of(article(1, 1, "X")));
        when(favouriteRepository.existsByUserIdAndArticleId(1, 1)).thenReturn(true);

        articleService.addToFavourites(1, 1);

        verify(favouriteRepository, never()).save(any());
    }

    @Test
    void addToFavourites_whenNew_saves() {
        when(articleRepository.findById(1)).thenReturn(Optional.of(article(1, 1, "X")));
        when(favouriteRepository.existsByUserIdAndArticleId(1, 1)).thenReturn(false);

        articleService.addToFavourites(1, 1);

        verify(favouriteRepository).save(any(FavouriteArticleEntity.class));
    }

    @Test
    void removeFromFavourites_callsDelete() {
        articleService.removeFromFavourites(1, 1);
        verify(favouriteRepository).deleteByUserIdAndArticleId(1, 1);
    }

    // ----- LIKES -----

    @Test
    void addLike_increasesCounter() {
        ArticleEntity a = article(1, 1, "X");
        a.setLikesCount(2);
        when(articleRepository.findById(1)).thenReturn(Optional.of(a));
        when(likeRepository.existsByUserIdAndArticleId(1, 1)).thenReturn(false);

        articleService.addLike(1, 1);

        verify(likeRepository).save(any(LikeArticleEntity.class));
        assertEquals(3, a.getLikesCount());
    }

    @Test
    void addLike_whenExists_noChange() {
        ArticleEntity a = article(1, 1, "X");
        a.setLikesCount(2);
        when(articleRepository.findById(1)).thenReturn(Optional.of(a));
        when(likeRepository.existsByUserIdAndArticleId(1, 1)).thenReturn(true);

        articleService.addLike(1, 1);

        verify(likeRepository, never()).save(any());
        assertEquals(2, a.getLikesCount());
    }

    @Test
    void removeLike_decreasesCounter() {
        ArticleEntity a = article(1, 1, "X");
        a.setLikesCount(2);
        when(likeRepository.existsByUserIdAndArticleId(1, 1)).thenReturn(true);
        when(articleRepository.findById(1)).thenReturn(Optional.of(a));

        articleService.removeLike(1, 1);

        verify(likeRepository).deleteByUserIdAndArticleId(1, 1);
        assertEquals(1, a.getLikesCount());
    }

    @Test
    void removeLike_neverBelowZero() {
        ArticleEntity a = article(1, 1, "X");
        a.setLikesCount(0);
        when(likeRepository.existsByUserIdAndArticleId(1, 1)).thenReturn(true);
        when(articleRepository.findById(1)).thenReturn(Optional.of(a));

        articleService.removeLike(1, 1);

        assertEquals(0, a.getLikesCount());
    }

    @Test
    void getLikesCount_returnsRepoCount() {
        when(likeRepository.countByArticleId(1)).thenReturn(7L);
        assertEquals(7L, articleService.getLikesCount(1));
    }
}

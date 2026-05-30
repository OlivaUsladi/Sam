package com.example.backend.service;

import com.example.backend.dto.ArticleCategoryResponseDto;
import com.example.backend.dto.ArticleDetailResponseDto;
import com.example.backend.dto.ArticleResponseDto;
import com.example.backend.entity.ArticleCategoryEntity;
import com.example.backend.entity.ArticleContentEntity;
import com.example.backend.entity.ArticleEntity;
import com.example.backend.entity.FavouriteArticleEntity;
import com.example.backend.entity.LikeArticleEntity;
import com.example.backend.mapper.ArticleMapper;
import com.example.backend.repository.ArticleCategoryRepository;
import com.example.backend.repository.ArticleContentRepository;
import com.example.backend.repository.ArticleRepository;
import com.example.backend.repository.FavouriteArticleRepository;
import com.example.backend.repository.LikeArticleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleCategoryRepository categoryRepository;
    private final ArticleContentRepository contentRepository;
    private final FavouriteArticleRepository favouriteRepository;
    private final LikeArticleRepository likeRepository;
    private final ArticleMapper mapper;


    @Transactional(readOnly = true)
    public List<ArticleCategoryResponseDto> getCategories() {
        return categoryRepository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }


    @Transactional(readOnly = true)
    public List<ArticleResponseDto> getArticles(Integer userId) {
        return mapList(articleRepository.findAllByOrderByCreatedAtDesc(), userId);
    }

    @Transactional(readOnly = true)
    public List<ArticleResponseDto> getArticlesByCategory(Integer userId, Integer categoryId) {
        return mapList(articleRepository.findByCategoryIdOrderByCreatedAtDesc(categoryId), userId);
    }

    @Transactional(readOnly = true)
    public List<ArticleResponseDto> searchArticles(Integer userId, String query) {
        if (query == null || query.isBlank()) return getArticles(userId);
        return mapList(articleRepository.searchByQuery(query.trim()), userId);
    }

    @Transactional(readOnly = true)
    public ArticleResponseDto getArticle(Integer userId, Integer articleId) {
        ArticleEntity article = requireArticle(articleId);
        ArticleCategoryEntity category = requireCategory(article.getCategoryId());
        return mapper.toDto(article,
                category,
                favouriteRepository.existsByUserIdAndArticleId(userId, articleId),
                likeRepository.existsByUserIdAndArticleId(userId, articleId)
        );
    }

    @Transactional(readOnly = true)
    public ArticleDetailResponseDto getArticleContent(Integer userId, Integer articleId) {
        ArticleEntity article = requireArticle(articleId);
        ArticleCategoryEntity category = requireCategory(article.getCategoryId());
        ArticleContentEntity content = contentRepository.findById(articleId).orElse(null);
        return mapper.toDetailDto(
                article,
                category,
                content,
                favouriteRepository.existsByUserIdAndArticleId(userId, articleId),
                likeRepository.existsByUserIdAndArticleId(userId, articleId)
        );
    }


    @Transactional(readOnly = true)
    public List<ArticleResponseDto> getFavouriteArticles(Integer userId) {
        List<Integer> articleIds = favouriteRepository.findByUserId(userId).stream()
                .map(FavouriteArticleEntity::getArticleId)
                .toList();
        if (articleIds.isEmpty()) return List.of();
        return mapList(articleRepository.findAllById(articleIds), userId);
    }

    @Transactional(readOnly = true)
    public boolean isFavourite(Integer userId, Integer articleId) {
        return favouriteRepository.existsByUserIdAndArticleId(userId, articleId);
    }

    @Transactional
    public void addToFavourites(Integer userId, Integer articleId) {
        requireArticle(articleId);
        if (favouriteRepository.existsByUserIdAndArticleId(userId, articleId)) return;
        FavouriteArticleEntity fav = new FavouriteArticleEntity();
        fav.setUserId(userId);
        fav.setArticleId(articleId);
        favouriteRepository.save(fav);
    }

    @Transactional
    public void removeFromFavourites(Integer userId, Integer articleId) {
        favouriteRepository.deleteByUserIdAndArticleId(userId, articleId);
    }


    @Transactional(readOnly = true)
    public List<ArticleResponseDto> getLikedArticles(Integer userId) {
        List<Integer> articleIds = likeRepository.findByUserId(userId).stream()
                .map(LikeArticleEntity::getArticleId)
                .toList();
        if (articleIds.isEmpty()) return List.of();
        return mapList(articleRepository.findAllById(articleIds), userId);
    }

    @Transactional(readOnly = true)
    public boolean isLiked(Integer userId, Integer articleId) {
        return likeRepository.existsByUserIdAndArticleId(userId, articleId);
    }

    @Transactional(readOnly = true)
    public long getLikesCount(Integer articleId) {
        return likeRepository.countByArticleId(articleId);
    }

    @Transactional
    public void addLike(Integer userId, Integer articleId) {
        ArticleEntity article = requireArticle(articleId);
        if (likeRepository.existsByUserIdAndArticleId(userId, articleId)) return;

        LikeArticleEntity like = new LikeArticleEntity();
        like.setUserId(userId);
        like.setArticleId(articleId);
        likeRepository.save(like);

        int current = article.getLikesCount() == null ? 0 : article.getLikesCount();
        article.setLikesCount(current + 1);
    }

    @Transactional
    public void removeLike(Integer userId, Integer articleId) {
        if (!likeRepository.existsByUserIdAndArticleId(userId, articleId)) return;
        likeRepository.deleteByUserIdAndArticleId(userId, articleId);

        ArticleEntity article = requireArticle(articleId);
        int current = article.getLikesCount() == null ? 0 : article.getLikesCount();
        article.setLikesCount(Math.max(0, current - 1));
    }

    private List<ArticleResponseDto> mapList(List<ArticleEntity> articles, Integer userId) {
        if (articles.isEmpty()) return List.of();

        List<Integer> categoryIds = articles.stream()
                .map(ArticleEntity::getCategoryId)
                .distinct()
                .toList();
        Map<Integer, ArticleCategoryEntity> categoriesById = categoryRepository.findAllById(categoryIds).stream()
                .collect(Collectors.toMap(ArticleCategoryEntity::getId, Function.identity()));

        Set<Integer> favouriteIds = favouriteRepository.findByUserId(userId).stream()
                .map(FavouriteArticleEntity::getArticleId)
                .collect(Collectors.toSet());
        Set<Integer> likedIds = likeRepository.findByUserId(userId).stream()
                .map(LikeArticleEntity::getArticleId)
                .collect(Collectors.toSet());

        return articles.stream()
                .map(a -> mapper.toDto(
                        a,
                        categoriesById.get(a.getCategoryId()),
                        favouriteIds.contains(a.getId()),
                        likedIds.contains(a.getId())
                ))
                .toList();
    }

    private ArticleEntity requireArticle(Integer articleId) {
        return articleRepository.findById(articleId)
                .orElseThrow(() -> new EntityNotFoundException("Article " + articleId + " not found"));
    }

    private ArticleCategoryEntity requireCategory(Integer categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Article category " + categoryId + " not found"));
    }
}

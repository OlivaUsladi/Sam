package com.example.backend.service;

import com.example.backend.dto.RecipeDetailResponseDto;
import com.example.backend.dto.RecipeResponseDto;
import com.example.backend.entity.*;
import com.example.backend.mapper.RecipeMapper;
import com.example.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeGroceryCrossRepository recipeGroceryCrossRepository;
    private final LikeRecipeRepository likeRecipeRepository;
    private final FavouriteRecipeRepository favouriteRecipeRepository;
    private final UserRepository userRepository;
    private final RecipeMapper recipeMapper;

    //Для неавторизованных пользователей
    @Transactional(readOnly = true)
    public List<RecipeResponseDto> getAllRecipesPublic() {
        return recipeMapper.toResponseDtoList(recipeRepository.findAll());
    }

    //Для неавторизованных пользователей
    @Transactional(readOnly = true)
    public RecipeDetailResponseDto getRecipeByIdPublic(Integer id) {
        RecipeEntity recipe = recipeRepository.findById(id).orElse(null);
        return recipeMapper.toDetailResponseDto(recipe);
    }

    //Для неавторизованных пользователей
    @Transactional(readOnly = true)
    public List<RecipeResponseDto> searchRecipesPublic(String query) {
        return recipeMapper.toResponseDtoList(recipeRepository.findByTitleContainingIgnoreCase(query));
    }

    //Для неавторизованных пользователей
    @Transactional(readOnly = true)
    public List<RecipeResponseDto> getRecipesByCategoryPublic(Integer categoryId) {
        return recipeMapper.toResponseDtoList(recipeRepository.findByCategoriesId(categoryId));
    }

    //Для неавторизованных пользователей
    @Transactional(readOnly = true)
    public List<RecipeResponseDto> getRecipesByGroceryItemsPublic(List<Integer> groceryItemIds) {
        return recipeMapper.toResponseDtoList(recipeRepository.findByGroceryItemIds(groceryItemIds));
    }

    //Для неавторизованных пользователей
    @Transactional(readOnly = true)
    public List<RecipeResponseDto> getRecipesByExactGroceryItemsPublic(List<Integer> groceryItemIds) {
        List<RecipeEntity> allRecipes = recipeRepository.findAll();

        List<RecipeEntity> filtered = allRecipes.stream()
                .filter(recipe -> {
                    List<Integer> recipeItemIds = recipe.getRecipeGroceries().stream()
                            .map(rg -> rg.getGroceryItem().getId())
                            .collect(Collectors.toList());
                    return recipeItemIds.stream().allMatch(groceryItemIds::contains);
                })
                .collect(Collectors.toList());

        return recipeMapper.toResponseDtoList(filtered);
    }

    // ДАЛЬШЕ ДЛЯ АВТОРИЗОВАННЫХ ПОЛЬЗОВАТЕЛЕЙ

    @Transactional(readOnly = true)
    public List<RecipeResponseDto> getAllRecipesForUser(Integer userId) {
        List<RecipeEntity> recipes = recipeRepository.findAll();

        if (userId == null) {
            return getAllRecipesPublic();
        }

        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return getAllRecipesPublic();
        }

        // лайк и избранное проверяем
        return recipes.stream()
                .map(recipe -> enrichResponseDtoWithUserData(recipe, user))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RecipeDetailResponseDto getRecipeByIdForUser(Integer id, Integer userId) {
        RecipeEntity recipe = recipeRepository.findById(id).orElse(null);
        if (recipe == null) return null;

        RecipeDetailResponseDto dto = recipeMapper.toDetailResponseDto(recipe);

        if (userId != null) {
            UserEntity user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                dto.setIsLiked(likeRecipeRepository.existsByUserAndRecipe(user, recipe));
                dto.setIsFavorite(favouriteRecipeRepository.existsByUserAndRecipe(user, recipe));
            } else {
                dto.setIsLiked(false);
                dto.setIsFavorite(false);
            }
        } else {
            dto.setIsLiked(false);
            dto.setIsFavorite(false);
        }

        return dto;
    }

    @Transactional(readOnly = true)
    public List<RecipeResponseDto> searchRecipesForUser(String query, Integer userId) {
        List<RecipeEntity> recipes = recipeRepository.findByTitleContainingIgnoreCase(query);

        if (userId == null) {
            return recipeMapper.toResponseDtoList(recipes);
        }

        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return recipeMapper.toResponseDtoList(recipes);
        }

        return recipes.stream()
                .map(recipe -> enrichResponseDtoWithUserData(recipe, user))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RecipeResponseDto> getRecipesByCategoryForUser(Integer categoryId, Integer userId) {
        List<RecipeEntity> recipes = recipeRepository.findByCategoriesId(categoryId);

        if (userId == null) {
            return recipeMapper.toResponseDtoList(recipes);
        }

        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return recipeMapper.toResponseDtoList(recipes);
        }

        return recipes.stream()
                .map(recipe -> enrichResponseDtoWithUserData(recipe, user))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RecipeResponseDto> getRecipesByGroceryItemsForUser(List<Integer> groceryItemIds, Integer userId) {
        List<RecipeEntity> recipes = recipeRepository.findByGroceryItemIds(groceryItemIds);

        if (userId == null) {
            return recipeMapper.toResponseDtoList(recipes);
        }

        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return recipeMapper.toResponseDtoList(recipes);
        }

        return recipes.stream()
                .map(recipe -> enrichResponseDtoWithUserData(recipe, user))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RecipeResponseDto> getRecipesByExactGroceryItemsForUser(List<Integer> groceryItemIds, Integer userId) {
        List<RecipeEntity> allRecipes = recipeRepository.findAll();

        // нереально крутой запрос для фильтрации рецептов ТОЛЬКО по заданным продуктам
        //но возможно надо оптимизировать (ищет по всем продуктам каждого рецепта)
        List<RecipeEntity> filtered = allRecipes.stream()
                .filter(recipe -> recipe.getRecipeGroceries().stream()
                            .map(rg -> rg.getGroceryItem().getId())
                            .collect(Collectors.toList()).stream().allMatch(groceryItemIds::contains))
                .collect(Collectors.toList());

        if (userId == null) {
            return recipeMapper.toResponseDtoList(filtered);
        }

        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return recipeMapper.toResponseDtoList(filtered);
        }

        return filtered.stream()
                .map(recipe -> enrichResponseDtoWithUserData(recipe, user))
                .collect(Collectors.toList());
    }



    @Transactional
    public void addLike(Integer recipeId, Integer userId) {
        if (userId == null) {
            throw new RuntimeException("Требуется авторизация");
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        RecipeEntity recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Рецепт не найден"));

        if (!likeRecipeRepository.existsByUserAndRecipe(user, recipe)) {
            LikeRecipeEntity like = new LikeRecipeEntity();
            like.setUser(user);
            like.setRecipe(recipe);
            likeRecipeRepository.save(like);

            int currentLikes = recipe.getLikesCount() == null ? 0 : recipe.getLikesCount();
            recipe.setLikesCount(currentLikes + 1);
            recipeRepository.save(recipe);
        }
    }

    @Transactional
    public void removeLike(Integer recipeId, Integer userId) {
        if (userId == null) {
            throw new RuntimeException("Требуется авторизация");
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        RecipeEntity recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Рецепт не найден"));

        likeRecipeRepository.deleteByUserAndRecipe(user, recipe);

        int currentLikes = recipe.getLikesCount() == null ? 0 : recipe.getLikesCount();
        recipe.setLikesCount(Math.max(0, currentLikes - 1));
        recipeRepository.save(recipe);
    }

    @Transactional
    public void addToFavourites(Integer recipeId, Integer userId) {
        if (userId == null) {
            throw new RuntimeException("Требуется авторизация");
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        RecipeEntity recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Рецепт не найден"));

        if (!favouriteRecipeRepository.existsByUserAndRecipe(user, recipe)) {
            FavouriteRecipeEntity favourite = new FavouriteRecipeEntity();
            favourite.setUser(user);
            favourite.setRecipe(recipe);
            favouriteRecipeRepository.save(favourite);
        }
    }

    @Transactional
    public void removeFromFavourites(Integer recipeId, Integer userId) {
        if (userId == null) {
            throw new RuntimeException("Требуется авторизация");
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        RecipeEntity recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Рецепт не найден"));

        favouriteRecipeRepository.deleteByUserAndRecipe(user, recipe);
    }

    @Transactional(readOnly = true)
    public List<RecipeResponseDto> getFavouriteRecipes(Integer userId) {
        if (userId == null) {
            throw new RuntimeException("Требуется авторизация");
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        List<FavouriteRecipeEntity> favourites = favouriteRecipeRepository.findByUser(user);

        return favourites.stream()
                .map(fav -> enrichResponseDtoWithUserData(fav.getRecipe(), user))
                .collect(Collectors.toList());
    }



    //проверка лайка и избранного
    //Возможно стоит оптимизировать!!!!!!!!
    private RecipeResponseDto enrichResponseDtoWithUserData(RecipeEntity recipe, UserEntity user) {
        RecipeResponseDto dto = recipeMapper.toResponseDto(recipe);
        if (dto != null) {
            dto.setIsLiked(likeRecipeRepository.existsByUserAndRecipe(user, recipe));
            dto.setIsFavorite(favouriteRecipeRepository.existsByUserAndRecipe(user, recipe));
        }
        return dto;
    }
}
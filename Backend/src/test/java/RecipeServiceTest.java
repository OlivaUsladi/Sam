import com.example.backend.dto.RecipeDetailResponseDto;
import com.example.backend.dto.RecipeResponseDto;
import com.example.backend.entity.*;
import com.example.backend.mapper.RecipeMapper;
import com.example.backend.repository.*;
import com.example.backend.service.RecipeService;
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
class RecipeServiceTest {

    @Mock private RecipeRepository recipeRepository;
    @Mock private RecipeGroceryCrossRepository recipeGroceryCrossRepository;
    @Mock private LikeRecipeRepository likeRecipeRepository;
    @Mock private FavouriteRecipeRepository favouriteRecipeRepository;
    @Mock private UserRepository userRepository;
    @Mock private RecipeMapper recipeMapper;

    @InjectMocks
    private RecipeService recipeService;

    private RecipeEntity recipe(int id, String title) {
        RecipeEntity r = new RecipeEntity();
        r.setId(id);
        r.setTitle(title);
        r.setLikesCount(0);
        return r;
    }

    private UserEntity user(int id) {
        UserEntity u = new UserEntity();
        u.setId(id);
        return u;
    }

    // ----- PUBLIC LISTING -----

    @Test
    void getAllRecipesPublic_returnsAll() {
        when(recipeRepository.findAll()).thenReturn(List.of(recipe(1, "Борщ"), recipe(2, "Плов")));
        when(recipeMapper.toResponseDtoList(any())).thenReturn(List.of(
                new RecipeResponseDto(), new RecipeResponseDto()));

        List<RecipeResponseDto> r = recipeService.getAllRecipesPublic();
        assertEquals(2, r.size());
    }

    @Test
    void getRecipeByIdPublic_whenMissing_returnsNullMapped() {
        when(recipeRepository.findById(99)).thenReturn(Optional.empty());
        when(recipeMapper.toDetailResponseDto(null)).thenReturn(null);

        RecipeDetailResponseDto r = recipeService.getRecipeByIdPublic(99);
        assertNull(r);
    }

    @Test
    void searchRecipesPublic_filtersByTitle() {
        when(recipeRepository.findByTitleContainingIgnoreCase("борщ"))
                .thenReturn(List.of(recipe(1, "Борщ")));
        when(recipeMapper.toResponseDtoList(any())).thenReturn(List.of(new RecipeResponseDto()));

        List<RecipeResponseDto> r = recipeService.searchRecipesPublic("борщ");
        assertEquals(1, r.size());
    }

    @Test
    void getRecipesByCategoryPublic_returnsByCategory() {
        when(recipeRepository.findByCategoriesId(5)).thenReturn(List.of(recipe(1, "Борщ")));
        when(recipeMapper.toResponseDtoList(any())).thenReturn(List.of(new RecipeResponseDto()));

        List<RecipeResponseDto> r = recipeService.getRecipesByCategoryPublic(5);
        assertEquals(1, r.size());
    }

    // ----- USER LISTING -----

    @Test
    void getAllRecipesForUser_whenUserNull_returnsPublic() {
        when(recipeRepository.findAll()).thenReturn(List.of(recipe(1, "X")));
        when(recipeMapper.toResponseDtoList(any())).thenReturn(List.of(new RecipeResponseDto()));

        List<RecipeResponseDto> r = recipeService.getAllRecipesForUser(null);
        assertEquals(1, r.size());
    }

    @Test
    void getRecipeByIdForUser_returnsEnriched() {
        RecipeEntity rec = recipe(1, "Борщ");
        RecipeDetailResponseDto dto = new RecipeDetailResponseDto();
        when(recipeRepository.findById(1)).thenReturn(Optional.of(rec));
        when(recipeMapper.toDetailResponseDto(rec)).thenReturn(dto);
        when(userRepository.findById(1)).thenReturn(Optional.of(user(1)));
        when(likeRecipeRepository.existsByUserAndRecipe(any(), eq(rec))).thenReturn(true);
        when(favouriteRecipeRepository.existsByUserAndRecipe(any(), eq(rec))).thenReturn(false);

        RecipeDetailResponseDto resp = recipeService.getRecipeByIdForUser(1, 1);
        assertTrue(resp.getIsLiked());
        assertFalse(resp.getIsFavorite());
    }

    // ----- LIKE -----

    @Test
    void addLike_whenUserMissing_throws() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> recipeService.addLike(1, 1));
    }

    @Test
    void addLike_increasesCount() {
        RecipeEntity rec = recipe(1, "Борщ");
        UserEntity u = user(1);
        when(userRepository.findById(1)).thenReturn(Optional.of(u));
        when(recipeRepository.findById(1)).thenReturn(Optional.of(rec));
        when(likeRecipeRepository.existsByUserAndRecipe(u, rec)).thenReturn(false);

        recipeService.addLike(1, 1);

        verify(likeRecipeRepository).save(any(LikeRecipeEntity.class));
        assertEquals(1, rec.getLikesCount());
    }

    @Test
    void addLike_whenAlreadyLiked_noChange() {
        RecipeEntity rec = recipe(1, "Борщ");
        rec.setLikesCount(5);
        UserEntity u = user(1);
        when(userRepository.findById(1)).thenReturn(Optional.of(u));
        when(recipeRepository.findById(1)).thenReturn(Optional.of(rec));
        when(likeRecipeRepository.existsByUserAndRecipe(u, rec)).thenReturn(true);

        recipeService.addLike(1, 1);

        verify(likeRecipeRepository, never()).save(any());
        assertEquals(5, rec.getLikesCount());
    }

    @Test
    void removeLike_decreasesCount() {
        RecipeEntity rec = recipe(1, "Борщ");
        rec.setLikesCount(5);
        UserEntity u = user(1);
        when(userRepository.findById(1)).thenReturn(Optional.of(u));
        when(recipeRepository.findById(1)).thenReturn(Optional.of(rec));

        recipeService.removeLike(1, 1);

        verify(likeRecipeRepository).deleteByUserAndRecipe(u, rec);
        assertEquals(4, rec.getLikesCount());
    }

    @Test
    void removeLike_neverGoesBelowZero() {
        RecipeEntity rec = recipe(1, "Борщ");
        rec.setLikesCount(0);
        UserEntity u = user(1);
        when(userRepository.findById(1)).thenReturn(Optional.of(u));
        when(recipeRepository.findById(1)).thenReturn(Optional.of(rec));

        recipeService.removeLike(1, 1);

        assertEquals(0, rec.getLikesCount());
    }

    // ----- FAVOURITES -----

    @Test
    void addToFavourites_whenNotYet_saves() {
        RecipeEntity rec = recipe(1, "X");
        UserEntity u = user(1);
        when(userRepository.findById(1)).thenReturn(Optional.of(u));
        when(recipeRepository.findById(1)).thenReturn(Optional.of(rec));
        when(favouriteRecipeRepository.existsByUserAndRecipe(u, rec)).thenReturn(false);

        recipeService.addToFavourites(1, 1);

        verify(favouriteRecipeRepository).save(any(FavouriteRecipeEntity.class));
    }

    @Test
    void addToFavourites_whenAlready_noChange() {
        RecipeEntity rec = recipe(1, "X");
        UserEntity u = user(1);
        when(userRepository.findById(1)).thenReturn(Optional.of(u));
        when(recipeRepository.findById(1)).thenReturn(Optional.of(rec));
        when(favouriteRecipeRepository.existsByUserAndRecipe(u, rec)).thenReturn(true);

        recipeService.addToFavourites(1, 1);

        verify(favouriteRecipeRepository, never()).save(any());
    }

    @Test
    void addLike_whenUnauthorized_throws() {
        assertThrows(RuntimeException.class, () -> recipeService.addLike(1, null));
    }
}

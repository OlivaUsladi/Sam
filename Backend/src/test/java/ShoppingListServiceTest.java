import com.example.backend.dto.*;
import com.example.backend.entity.ShoppingListEntity;
import com.example.backend.entity.ShoppingListItemEntity;
import com.example.backend.mapper.ShoppingListMapper;
import com.example.backend.repository.ShoppingListItemRepository;
import com.example.backend.repository.ShoppingListRepository;
import com.example.backend.service.ShoppingListService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShoppingListServiceTest {

    @Mock private ShoppingListRepository listRepository;
    @Mock private ShoppingListItemRepository itemRepository;
    @Mock private ShoppingListMapper mapper;

    @InjectMocks
    private ShoppingListService shoppingListService;

    private ShoppingListEntity list(int id, int userId, String name) {
        ShoppingListEntity l = new ShoppingListEntity();
        l.setId(id); l.setUserId(userId); l.setName(name);
        return l;
    }

    private ShoppingListItemEntity item(int id, int listId, String desc,
                                        Double qty, String unit, boolean checked) {
        ShoppingListItemEntity i = new ShoppingListItemEntity();
        i.setId(id);
        i.setShoppingListId(listId);
        i.setDescription(desc);
        i.setQuantity(qty);
        i.setUnit(unit);
        i.setIsChecked(checked);
        return i;
    }

    @Test
    void getLists_returnsUserLists() {
        ShoppingListEntity l1 = list(1, 1, "Завтрак");
        when(listRepository.findByUserIdOrderByCreatedAtDesc(1)).thenReturn(List.of(l1));
        when(itemRepository.findByShoppingListIdOrderById(1)).thenReturn(List.of());
        when(mapper.toDto(eq(l1), anyList()))
                .thenReturn(new ShoppingListResponseDto(1, 1, "Завтрак", LocalDateTime.now(), false, List.of()));

        List<ShoppingListResponseDto> r = shoppingListService.getLists(1);
        assertEquals(1, r.size());
    }

    @Test
    void getList_whenNotFound_throws() {
        when(listRepository.findByIdAndUserId(2, 1)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> shoppingListService.getList(1, 2));
    }

    @Test
    void createList_success_savesAndMaps() {
        when(listRepository.save(any(ShoppingListEntity.class))).thenAnswer(inv -> {
            ShoppingListEntity e = inv.getArgument(0); e.setId(100); return e;
        });
        when(mapper.toDto(any(ShoppingListEntity.class), eq(List.of())))
                .thenReturn(new ShoppingListResponseDto(100, 1, "Новый", LocalDateTime.now(), false, List.of()));

        ShoppingListResponseDto resp = shoppingListService.createList(1,
                new CreateShoppingListRequestDto("  Новый  "));

        assertEquals(100, resp.id());
        verify(listRepository).save(any(ShoppingListEntity.class));
    }

    @Test
    void renameList_success_updatesName() {
        ShoppingListEntity l = list(1, 1, "Old");
        when(listRepository.findByIdAndUserId(1, 1)).thenReturn(Optional.of(l));
        when(itemRepository.findByShoppingListIdOrderById(1)).thenReturn(List.of());
        when(mapper.toDto(l, List.of())).thenReturn(
                new ShoppingListResponseDto(1, 1, "New", LocalDateTime.now(), false, List.of()));

        shoppingListService.renameList(1, 1, new RenameShoppingListRequestDto("New"));

        assertEquals("New", l.getName());
    }

    @Test
    void deleteList_deletesItemsAndList() {
        ShoppingListEntity l = list(1, 1, "X");
        when(listRepository.findByIdAndUserId(1, 1)).thenReturn(Optional.of(l));

        shoppingListService.deleteList(1, 1);

        verify(itemRepository).deleteAllByShoppingListId(1);
        verify(listRepository).delete(l);
    }

    @Test
    void addItem_success_savesAndReturns() {
        when(listRepository.findByIdAndUserId(1, 1)).thenReturn(Optional.of(list(1, 1, "X")));
        when(itemRepository.save(any(ShoppingListItemEntity.class))).thenAnswer(inv -> {
            ShoppingListItemEntity e = inv.getArgument(0); e.setId(200); return e;
        });
        when(mapper.toDto(any(ShoppingListItemEntity.class)))
                .thenReturn(new ShoppingListItemResponseDto(200, "Молоко", false, 1.0, "л"));

        ShoppingListItemResponseDto resp = shoppingListService.addItem(1, 1,
                new AddShoppingListItemRequestDto("Молоко", 1.0, "л"));

        assertEquals(200, resp.id());
    }

    @Test
    void updateItem_updatesFields() {
        ShoppingListItemEntity it = item(10, 1, "Молоко", 1.0, "л", false);
        when(itemRepository.findById(10)).thenReturn(Optional.of(it));
        when(listRepository.findByIdAndUserId(1, 1)).thenReturn(Optional.of(list(1, 1, "X")));
        when(mapper.toDto(it)).thenReturn(
                new ShoppingListItemResponseDto(10, "Молоко 2.5%", true, 2.0, "л"));

        shoppingListService.updateItem(1, 10,
                new UpdateShoppingListItemRequestDto("Молоко 2.5%", 2.0, "л", true));

        assertEquals("Молоко 2.5%", it.getDescription());
        assertEquals(2.0, it.getQuantity());
        assertTrue(it.getIsChecked());
    }

    @Test
    void deleteItem_deletes() {
        ShoppingListItemEntity it = item(10, 1, "Х", 1.0, "шт", false);
        when(itemRepository.findById(10)).thenReturn(Optional.of(it));
        when(listRepository.findByIdAndUserId(1, 1)).thenReturn(Optional.of(list(1, 1, "X")));

        shoppingListService.deleteItem(1, 10);

        verify(itemRepository).delete(it);
    }

    @Test
    void deleteItem_whenForeignList_throws() {
        ShoppingListItemEntity it = item(10, 99, "Х", 1.0, "шт", false);
        when(itemRepository.findById(10)).thenReturn(Optional.of(it));
        when(listRepository.findByIdAndUserId(99, 1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> shoppingListService.deleteItem(1, 10));
    }

    @Test
    void checkAll_callsRepo() {
        when(listRepository.findByIdAndUserId(1, 1)).thenReturn(Optional.of(list(1, 1, "X")));

        shoppingListService.checkAll(1, 1, new CheckAllItemsRequestDto(true));

        verify(itemRepository).updateCheckedFlagForList(1, true);
    }

    @Test
    void clearCompleted_deletesChecked() {
        when(listRepository.findByIdAndUserId(1, 1)).thenReturn(Optional.of(list(1, 1, "X")));

        shoppingListService.clearCompleted(1, 1);

        verify(itemRepository).deleteCheckedByShoppingListId(1);
    }

    @Test
    void addItemsFromRecipe_whenItemMissing_createsNew() {
        when(listRepository.findByIdAndUserId(1, 1)).thenReturn(Optional.of(list(1, 1, "X")));
        when(itemRepository.findByShoppingListIdOrderById(1)).thenReturn(List.of());
        when(itemRepository.save(any(ShoppingListItemEntity.class))).thenAnswer(inv -> {
            ShoppingListItemEntity e = inv.getArgument(0); e.setId(300); return e;
        });
        when(mapper.toDto(any(ShoppingListItemEntity.class)))
                .thenReturn(new ShoppingListItemResponseDto(300, "Мука", false, 500.0, "г"));

        List<ShoppingListItemResponseDto> r = shoppingListService.addItemsFromRecipe(
                1, 1,
                new AddItemsFromRecipeRequestDto(1, List.of(
                        new RecipeIngredientFromRecipeDto("Мука", 500.0, "г"))));

        assertEquals(1, r.size());
        verify(itemRepository).save(any(ShoppingListItemEntity.class));
    }

    @Test
    void addItemsFromRecipe_whenItemExists_sumsQuantities() {
        ShoppingListItemEntity existing = item(10, 1, "Мука", 200.0, "г", false);
        when(listRepository.findByIdAndUserId(1, 1)).thenReturn(Optional.of(list(1, 1, "X")));
        when(itemRepository.findByShoppingListIdOrderById(1)).thenReturn(List.of(existing));
        when(mapper.toDto(existing))
                .thenReturn(new ShoppingListItemResponseDto(10, "Мука", false, 700.0, "г"));

        List<ShoppingListItemResponseDto> r = shoppingListService.addItemsFromRecipe(
                1, 1,
                new AddItemsFromRecipeRequestDto(1, List.of(
                        new RecipeIngredientFromRecipeDto("Мука", 500.0, "г"))));

        assertEquals(1, r.size());
        assertEquals(700.0, existing.getQuantity());
        verify(itemRepository, never()).save(any(ShoppingListItemEntity.class));
    }

    @Test
    void mergeLists_whenTargetInSources_throws() {
        MergeShoppingListsRequestDto req = new MergeShoppingListsRequestDto(
                1, List.of(1, 2));

        assertThrows(IllegalArgumentException.class,
                () -> shoppingListService.mergeLists(1, req));
    }

    @Test
    void mergeLists_combinesItems_deletesSources() {
        ShoppingListEntity target = list(1, 1, "Target");
        ShoppingListEntity src = list(2, 1, "Source");
        ShoppingListItemEntity srcItem = item(50, 2, "Яблоко", 3.0, "шт", false);

        when(listRepository.findByIdAndUserId(1, 1)).thenReturn(Optional.of(target));
        when(listRepository.findByIdAndUserId(2, 1)).thenReturn(Optional.of(src));
        when(itemRepository.findByShoppingListIdOrderById(1)).thenReturn(List.of()).thenReturn(List.of());
        when(itemRepository.findByShoppingListIdOrderById(2)).thenReturn(List.of(srcItem));
        when(itemRepository.save(any(ShoppingListItemEntity.class))).thenAnswer(inv -> {
            ShoppingListItemEntity e = inv.getArgument(0); e.setId(400); return e;
        });
        when(mapper.toDto(eq(target), anyList()))
                .thenReturn(new ShoppingListResponseDto(1, 1, "Target", LocalDateTime.now(), false, List.of()));

        shoppingListService.mergeLists(1, new MergeShoppingListsRequestDto(1, List.of(2)));

        verify(itemRepository).deleteAllByShoppingListId(2);
        verify(listRepository).delete(src);
    }
}
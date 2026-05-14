package com.example.backend.service;

import com.example.backend.dto.AddItemsFromRecipeRequestDto;
import com.example.backend.dto.AddShoppingListItemRequestDto;
import com.example.backend.dto.CheckAllItemsRequestDto;
import com.example.backend.dto.CreateShoppingListRequestDto;
import com.example.backend.dto.MergeShoppingListsRequestDto;
import com.example.backend.dto.RecipeIngredientFromRecipeDto;
import com.example.backend.dto.RenameShoppingListRequestDto;
import com.example.backend.dto.ShoppingListItemResponseDto;
import com.example.backend.dto.ShoppingListResponseDto;
import com.example.backend.dto.UpdateShoppingListItemRequestDto;
import com.example.backend.entity.ShoppingListEntity;
import com.example.backend.entity.ShoppingListItemEntity;
import com.example.backend.mapper.ShoppingListMapper;
import com.example.backend.repository.ShoppingListItemRepository;
import com.example.backend.repository.ShoppingListRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ShoppingListService {

    private final ShoppingListRepository listRepository;
    private final ShoppingListItemRepository itemRepository;
    private final ShoppingListMapper mapper;

    @Transactional(readOnly = true)
    public List<ShoppingListResponseDto> getLists(Integer userId) {
        return listRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(list -> mapper.toDto(list, itemRepository.findByShoppingListIdOrderById(list.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public ShoppingListResponseDto getList(Integer userId, Integer listId) {
        ShoppingListEntity list = requireList(userId, listId);
        return mapper.toDto(list, itemRepository.findByShoppingListIdOrderById(listId));
    }

    @Transactional
    public ShoppingListResponseDto createList(Integer userId, CreateShoppingListRequestDto request) {
        ShoppingListEntity list = new ShoppingListEntity();
        list.setUserId(userId);
        list.setName(request.name().trim());
        list = listRepository.save(list);
        return mapper.toDto(list, List.of());
    }

    @Transactional
    public ShoppingListResponseDto renameList(Integer userId, Integer listId, RenameShoppingListRequestDto request) {
        ShoppingListEntity list = requireList(userId, listId);
        list.setName(request.name().trim());
        return mapper.toDto(list, itemRepository.findByShoppingListIdOrderById(listId));
    }

    @Transactional
    public void deleteList(Integer userId, Integer listId) {
        ShoppingListEntity list = requireList(userId, listId);
        itemRepository.deleteAllByShoppingListId(listId);
        listRepository.delete(list);
    }


    @Transactional
    public ShoppingListItemResponseDto addItem(Integer userId, Integer listId, AddShoppingListItemRequestDto request) {
        requireList(userId, listId);
        ShoppingListItemEntity item = new ShoppingListItemEntity();
        item.setShoppingListId(listId);
        item.setDescription(request.description().trim());
        item.setQuantity(request.quantity());
        item.setUnit(normalizeUnit(request.unit()));
        item.setIsChecked(false);
        return mapper.toDto(itemRepository.save(item));
    }

    @Transactional
    public ShoppingListItemResponseDto updateItem(Integer userId, Integer itemId, UpdateShoppingListItemRequestDto request) {
        ShoppingListItemEntity item = requireItem(userId, itemId);
        if (request.description() != null) item.setDescription(request.description().trim());
        if (request.quantity() != null)    item.setQuantity(request.quantity());
        if (request.unit() != null)        item.setUnit(normalizeUnit(request.unit()));
        if (request.isChecked() != null)   item.setIsChecked(request.isChecked());
        return mapper.toDto(item);
    }

    @Transactional
    public void deleteItem(Integer userId, Integer itemId) {
        ShoppingListItemEntity item = requireItem(userId, itemId);
        itemRepository.delete(item);
    }

    @Transactional
    public void checkAll(Integer userId, Integer listId, CheckAllItemsRequestDto request) {
        requireList(userId, listId);
        itemRepository.updateCheckedFlagForList(listId, request.isChecked());
    }

    @Transactional
    public void clearCompleted(Integer userId, Integer listId) {
        requireList(userId, listId);
        itemRepository.deleteCheckedByShoppingListId(listId);
    }


    @Transactional
    public List<ShoppingListItemResponseDto> addItemsFromRecipe(Integer userId, Integer listId,
                                                                AddItemsFromRecipeRequestDto request) {
        requireList(userId, listId);
        List<ShoppingListItemEntity> existing = itemRepository.findByShoppingListIdOrderById(listId);
        List<ShoppingListItemEntity> result = new ArrayList<>();

        for (RecipeIngredientFromRecipeDto ingredient : request.ingredients()) {
            ShoppingListItemEntity match = findSameProduct(existing, ingredient.name(), ingredient.unit());
            if (match != null) {
                if (ingredient.amount() != null) {
                    match.setQuantity(sumQuantities(match.getQuantity(), ingredient.amount()));
                }
                result.add(match);
            } else {
                ShoppingListItemEntity created = new ShoppingListItemEntity();
                created.setShoppingListId(listId);
                created.setDescription(ingredient.name().trim());
                created.setQuantity(ingredient.amount());
                created.setUnit(normalizeUnit(ingredient.unit()));
                created.setIsChecked(false);
                created = itemRepository.save(created);
                existing.add(created);
                result.add(created);
            }
        }
        return result.stream().map(mapper::toDto).toList();
    }


    @Transactional
    public ShoppingListResponseDto mergeLists(Integer userId, MergeShoppingListsRequestDto request) {
        if (request.sourceListIds().contains(request.targetListId())) {
            throw new IllegalArgumentException("Объединяют один и тот же список");
        }

        ShoppingListEntity target = requireList(userId, request.targetListId());

        List<ShoppingListItemEntity> targetItems =
                new ArrayList<>(itemRepository.findByShoppingListIdOrderById(target.getId()));

        for (Integer sourceId : request.sourceListIds()) {
            ShoppingListEntity source = requireList(userId, sourceId);
            List<ShoppingListItemEntity> sourceItems =
                    itemRepository.findByShoppingListIdOrderById(source.getId());

            for (ShoppingListItemEntity sourceItem : sourceItems) {
                ShoppingListItemEntity match =
                        findSameProduct(targetItems, sourceItem.getDescription(), sourceItem.getUnit());
                if (match != null) {
                    match.setQuantity(sumQuantities(match.getQuantity(), sourceItem.getQuantity()));
                    match.setIsChecked(Boolean.TRUE.equals(match.getIsChecked())
                            && Boolean.TRUE.equals(sourceItem.getIsChecked()));
                } else {
                    ShoppingListItemEntity copy = new ShoppingListItemEntity();
                    copy.setShoppingListId(target.getId());
                    copy.setDescription(sourceItem.getDescription());
                    copy.setQuantity(sourceItem.getQuantity());
                    copy.setUnit(sourceItem.getUnit());
                    copy.setIsChecked(Boolean.TRUE.equals(sourceItem.getIsChecked()));
                    targetItems.add(itemRepository.save(copy));
                }
            }

            itemRepository.deleteAllByShoppingListId(source.getId());
            listRepository.delete(source);
        }

        return mapper.toDto(target, itemRepository.findByShoppingListIdOrderById(target.getId()));
    }


    private ShoppingListEntity requireList(Integer userId, Integer listId) {
        return listRepository.findByIdAndUserId(listId, userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Список " + listId + " не найден для пользователя " + userId));
    }

    private ShoppingListItemEntity requireItem(Integer userId, Integer itemId) {
        ShoppingListItemEntity item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Позиция " + itemId + " не найдена"));
        requireList(userId, item.getShoppingListId());
        return item;
    }

    private ShoppingListItemEntity findSameProduct(List<ShoppingListItemEntity> items,
                                                   String description, String unit) {
        String normName = description == null ? "" : description.trim().toLowerCase();
        for (ShoppingListItemEntity item : items) {
            String itemName = item.getDescription() == null ? "" : item.getDescription().trim().toLowerCase();
            if (itemName.equals(normName) && isSameUnit(item.getUnit(), unit)) {
                return item;
            }
        }
        return null;
    }

    private static Double sumQuantities(Double a, Double b) {
        if (a == null && b == null) return null;
        if (a == null) return b;
        if (b == null) return a;
        return a + b;
    }

    private static String normalizeUnit(String unit) {
        if (unit == null) return null;
        String trimmed = unit.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static final Map<String, String> UNIT_SYNONYMS = Map.ofEntries(
            Map.entry("г",            "г"),
            Map.entry("гр",           "г"),
            Map.entry("грамм",        "г"),
            Map.entry("грамма",       "г"),
            Map.entry("граммов",      "г"),
            Map.entry("кг",           "кг"),
            Map.entry("килограмм",    "кг"),
            Map.entry("килограммов",  "кг"),
            Map.entry("мл",           "мл"),
            Map.entry("миллилитр",    "мл"),
            Map.entry("миллилитров",  "мл"),
            Map.entry("л",            "л"),
            Map.entry("литр",         "л"),
            Map.entry("литра",        "л"),
            Map.entry("литров",       "л"),
            Map.entry("шт",           "шт"),
            Map.entry("штук",         "шт"),
            Map.entry("штука",        "шт"),
            Map.entry("штуки",        "шт"),
            Map.entry("чл",           "ч.л"),
            Map.entry("ч.л",          "ч.л"),
            Map.entry("чайная ложка", "ч.л"),
            Map.entry("стл",          "ст.л"),
            Map.entry("ст.л",         "ст.л"),
            Map.entry("столовая ложка", "ст.л"),
            Map.entry("пуч",          "пучок"),
            Map.entry("пучка",        "пучок"),
            Map.entry("пучек",        "пучок"),
            Map.entry("пучок",        "пучок"),
            Map.entry("пач",          "пачка"),
            Map.entry("пачка",        "пачка"),
            Map.entry("пачки",        "пачка"),
            Map.entry("банк",         "банка"),
            Map.entry("банка",        "банка"),
            Map.entry("банки",        "банка"),
            Map.entry("бут",          "бутылка"),
            Map.entry("бутылка",      "бутылка"),
            Map.entry("бутылки",      "бутылка")
    );

    private static String canonicalizeUnit(String unit) {
        if (unit == null) return null;
        String n = unit.trim().toLowerCase().replace('ё', 'е');
        while (n.endsWith(".")) n = n.substring(0, n.length() - 1);
        if (n.isEmpty()) return null;
        String compact = n.replace(" ", "");
        String byCompact = UNIT_SYNONYMS.get(compact);
        if (byCompact != null) return byCompact;
        return UNIT_SYNONYMS.getOrDefault(n, n);
    }

    private static boolean isSameUnit(String a, String b) {
        String ca = canonicalizeUnit(a);
        String cb = canonicalizeUnit(b);
        if (ca == null && cb == null) return true;
        if (ca == null || cb == null) return false;
        if (ca.equals(cb)) return true;
        int shorter = Math.min(ca.length(), cb.length());
        return (shorter >= 3 && (ca.contains(cb) || cb.contains(ca)) || (ca.startsWith(cb) || cb.startsWith(ca)));
    }
}

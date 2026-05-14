package com.example.backend.repository;

import com.example.backend.entity.ShoppingListItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShoppingListItemRepository extends JpaRepository<ShoppingListItemEntity, Integer> {

    List<ShoppingListItemEntity> findByShoppingListIdOrderById(Integer shoppingListId);


    //@Modifying аннотация в Java Spring используется для обновления данных в базе данных(INSERT, UPDATE, DELETE)
    @Modifying
    @Query("delete from ShoppingListItemEntity i where i.shoppingListId = :listId")
    void deleteAllByShoppingListId(@Param("listId") Integer shoppingListId);

    @Modifying
    @Query("delete from ShoppingListItemEntity i where i.shoppingListId = :listId and i.isChecked = true")
    int deleteCheckedByShoppingListId(@Param("listId") Integer shoppingListId);

    @Modifying
    @Query("update ShoppingListItemEntity i set i.isChecked = :isChecked where i.shoppingListId = :listId")
    int updateCheckedFlagForList(@Param("listId") Integer shoppingListId,
                                 @Param("isChecked") Boolean isChecked);
}

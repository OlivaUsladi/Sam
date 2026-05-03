package com.example.myapplication.Recipes.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.Recipes.model.ShoppingList
import com.example.domain.Recipes.model.ShoppingListItem
import com.example.myapplication.R

@Composable
fun ShoppingListCard(
    shoppingList: ShoppingList,
    isExpanded: Boolean,
    isMergeMode: Boolean,
    isSelectedForMerge: Boolean,
    onToggleExpand: () -> Unit,
    onToggleMergeSelection: () -> Unit,
    onRenameClick: (String) -> Unit,
    onDeleteList: () -> Unit,
    onToggleItem: (Int, Boolean) -> Unit,
    onDeleteItem: (Int) -> Unit,
    onAddItem: (String) -> Unit,
    onCheckAll: () -> Unit,
    onUncheckAll: () -> Unit,
    suggestions: List<String>
) {
    var showRenameDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf(shoppingList.name) }
    var showAddItemDialog by remember { mutableStateOf(false) }
    var newItemDescription by remember { mutableStateOf("") }
    var showSuggestions by remember { mutableStateOf(false) }

    val allItemsChecked = shoppingList.items.isNotEmpty() && shoppingList.items.all { it.isChecked }
    val textDecoration = if (allItemsChecked && !isExpanded) TextDecoration.LineThrough else null

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelectedForMerge) Color(0xFFE4DB40).copy(alpha = 0.3f) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    if (isMergeMode) {
                        onToggleMergeSelection()
                    } else {
                        onToggleExpand()
                    }
                }
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isMergeMode) {
                    Checkbox(
                        checked = isSelectedForMerge,
                        onCheckedChange = { onToggleMergeSelection() }
                    )
                }

                Text(
                    text = shoppingList.name,
                    fontSize = 24.sp,
                    color = if (isExpanded && allItemsChecked) Color(0xFF3AC42A) else Color(0xFF25232B),
                    textDecoration = textDecoration,
                    modifier = Modifier.weight(1f)
                )

                if (!isExpanded && !isMergeMode) {
                    Text(
                        text = "${shoppingList.items.filter { it.isChecked }.size}/${shoppingList.items.size}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                if (!isMergeMode) {
                    Icon(
                        painter = painterResource(R.drawable.edit),
                        contentDescription = "Переименовать",
                        tint = Color(0xFF878787),
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { showRenameDialog = true }
                    )
                }
            }

            if (isExpanded && !isMergeMode) {
                Spacer(modifier = Modifier.height(16.dp))


                shoppingList.items.forEach { item ->
                    ShoppingListItemRow(
                        item = item,
                        onToggle = { onToggleItem(item.id, !item.isChecked) },
                        onDelete = { onDeleteItem(item.id) }
                    )
                }

                if (shoppingList.items.isEmpty()) {
                    Text(
                        text = "Список пуст. Добавьте продукты.",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }


                if (shoppingList.items.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextButton(
                            onClick = onCheckAll,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Отметить всё", fontSize = 14.sp)
                        }
                        TextButton(
                            onClick = onUncheckAll,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Снять отметки", fontSize = 14.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))


                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(Color.White, RoundedCornerShape(28.dp))
                        .clickable { showAddItemDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Добавить продукт",
                        fontSize = 16.sp,
                        color = Color(0xFF25232B)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))


                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .clickable { onDeleteList() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Удалить список",
                        fontSize = 16.sp,
                        color = Color(0xFFEC221F)
                    )
                }
            }
        }
    }


    if (showRenameDialog) {
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("Переименовать список") },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Название списка") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newName.isNotBlank()) {
                            onRenameClick(newName)
                            showRenameDialog = false
                        }
                    }
                ) {
                    Text("Сохранить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }


    if (showAddItemDialog) {
        AlertDialog(
            onDismissRequest = { showAddItemDialog = false },
            title = { Text("Добавить продукт") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newItemDescription,
                        onValueChange = {
                            newItemDescription = it
                            showSuggestions = it.isNotBlank()
                        },
                        label = { Text("Название продукта") },
                        singleLine = true
                    )

                    if (showSuggestions && suggestions.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                        ) {
                            suggestions.filter {
                                it.contains(newItemDescription, ignoreCase = true)
                            }.take(5).forEach { suggestion ->
                                TextButton(
                                    onClick = {
                                        onAddItem(suggestion)
                                        newItemDescription = ""
                                        showSuggestions = false
                                        showAddItemDialog = false
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(text = suggestion, modifier = Modifier.fillMaxWidth())
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newItemDescription.isNotBlank()) {
                            onAddItem(newItemDescription)
                            newItemDescription = ""
                            showAddItemDialog = false
                        }
                    }
                ) {
                    Text("Добавить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddItemDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
fun ShoppingListItemRow(
    item: ShoppingListItem,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = item.isChecked,
            onCheckedChange = { onToggle() }
        )

        Text(
            text = item.description,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Black,
            textDecoration = if (item.isChecked) TextDecoration.LineThrough else null,
            modifier = Modifier.weight(1f)
        )

        Icon(
            painter = painterResource(R.drawable.cross),
            contentDescription = "Удалить",
            tint = Color(0xFF878787),
            modifier = Modifier
                .size(24.dp)
                .clickable { onDelete() }
        )
    }
}
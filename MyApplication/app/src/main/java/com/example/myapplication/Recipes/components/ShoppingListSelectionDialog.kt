package com.example.myapplication.Recipes.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.Recipes.model.ShoppingList

@Composable
fun ShoppingListSelectionDialog(
    shoppingLists: List<ShoppingList>,
    isCreatingNewList: Boolean,
    newListName: String,
    onListSelected: (Int) -> Unit,
    onCreateNewList: () -> Unit,
    onNewListNameChange: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Добавить в список покупок",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                if (!isCreatingNewList) {
                    Text(
                        text = "Выберите список:",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    if (shoppingLists.isEmpty()) {
                        Text(
                            text = "У вас пока нет списков. Создайте новый.",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 250.dp)
                        ) {
                            items(shoppingLists) { list ->
                                ShoppingListItemOption(
                                    list = list,
                                    onClick = { onListSelected(list.id) }
                                )
                                //Divider()
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(
                        onClick = { onCreateNewList() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("+ Создать новый список", color = Color(0xFF2670CC))
                    }
                } else {
                    Text(
                        text = "Название нового списка:",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = newListName,
                        onValueChange = onNewListNameChange,
                        label = { Text("Название списка") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        },
        confirmButton = {
            if (isCreatingNewList) {
                TextButton(
                    onClick = {
                        if (newListName.isNotBlank()) {
                            onListSelected(-1)
                        }
                    }
                ) {
                    Text("Создать", color = Color(0xFF2670CC))
                }
            } else {
                TextButton(onClick = onDismiss) {
                    Text("Отмена", color = Color.Gray)
                }
            }
        },
        dismissButton = {
            if (isCreatingNewList) {
                TextButton(onClick = { onCreateNewList() }) {
                    Text("Назад", color = Color.Gray)
                }
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun ShoppingListItemOption(
    list: ShoppingList,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = list.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${list.items.size} продуктов",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
        Text(
            text = "Добавить",
            fontSize = 14.sp,
            color = Color(0xFF2670CC)
        )
    }
}
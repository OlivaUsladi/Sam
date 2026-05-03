package com.example.myapplication.Recipes.ui.shoppinglist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.Recipes.components.ShoppingListCard
import org.koin.androidx.compose.koinViewModel

@Composable
fun ShoppingListScreen(
    navController: NavController,
    viewModel: ShoppingListViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showCreateDialog by remember { mutableStateOf(false) }
    var newListName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.onEvent(ShoppingListEvent.LoadShoppingLists)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1D1C22))
                .padding(top = 12.dp, bottom = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.arrow_back),
                        contentDescription = "Назад",
                        modifier = Modifier.size(24.dp),
                        tint = Color.White
                    )
                }

                Text(
                    text = "Списки покупок",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                IconButton(
                    onClick = { viewModel.onEvent(ShoppingListEvent.EnterMergeMode) },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.twoarrow),
                        contentDescription = "Объединить",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Text(
            text = "Списки",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF25232B),
            modifier = Modifier.padding(start = 48.dp, top = 20.dp, bottom = 16.dp)
        )

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFE4DB40))
                }
            }

            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Ошибка: ${uiState.error}",
                        color = Color.Red,
                        fontSize = 16.sp
                    )
                }
            }

            else -> {

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 16.dp)
                ) {
                    items(uiState.shoppingLists) { list ->
                        ShoppingListCard(
                            shoppingList = list,
                            isExpanded = uiState.expandedListId == list.id,
                            isMergeMode = uiState.isMergeMode,
                            isSelectedForMerge = uiState.selectedForMerge.contains(list.id),
                            onToggleExpand = {
                                viewModel.onEvent(ShoppingListEvent.ToggleListExpansion(list.id))
                            },
                            onToggleMergeSelection = {
                                viewModel.onEvent(ShoppingListEvent.ToggleMergeSelection(list.id))
                            },
                            onRenameClick = { newName ->
                                viewModel.onEvent(ShoppingListEvent.RenameList(list.id, newName))
                            },
                            onDeleteList = {
                                viewModel.onEvent(ShoppingListEvent.DeleteList(list.id))
                            },
                            onToggleItem = { itemId, isChecked ->
                                viewModel.onEvent(ShoppingListEvent.ToggleItem(itemId, isChecked))
                            },
                            onDeleteItem = { itemId ->
                                viewModel.onEvent(ShoppingListEvent.DeleteItem(itemId))
                            },
                            onAddItem = { description ->
                                viewModel.onEvent(ShoppingListEvent.AddItem(list.id, description))
                            },
                            onCheckAll = {
                                viewModel.onEvent(ShoppingListEvent.CheckAllItems(list.id))
                            },
                            onUncheckAll = {
                                viewModel.onEvent(ShoppingListEvent.UncheckAllItems(list.id))
                            },
                            suggestions = uiState.grocerySuggestions
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable { showCreateDialog = true },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Создать новый список",
                fontSize = 16.sp,
                color = Color(0xFF2670CC)
            )
        }
    }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Новый список") },
            text = {
                OutlinedTextField(
                    value = newListName,
                    onValueChange = { newListName = it },
                    label = { Text("Название списка") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newListName.isNotBlank()) {
                            viewModel.onEvent(ShoppingListEvent.CreateList(newListName))
                            newListName = ""
                            showCreateDialog = false
                        }
                    }
                ) {
                    Text("Создать")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }

    if (uiState.isMergeMode && uiState.selectedForMerge.size >= 2) {
        AlertDialog(
            onDismissRequest = {
                viewModel.onEvent(ShoppingListEvent.ExitMergeMode)
            },
            title = { Text("Объединить списки") },
            text = {
                Text("Вы выбрали ${uiState.selectedForMerge.size} списка. Объединить их в один?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onEvent(ShoppingListEvent.MergeLists(uiState.selectedForMerge.toList()))
                        viewModel.onEvent(ShoppingListEvent.ExitMergeMode)
                    }
                ) {
                    Text("Объединить")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.onEvent(ShoppingListEvent.ExitMergeMode)
                }) {
                    Text("Отмена")
                }
            }
        )
    }
}
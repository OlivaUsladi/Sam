package com.example.myapplication.Recipes.ui.grocery


import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.domain.Recipes.model.Grocery
import com.example.domain.Recipes.model.GroceryItem
import com.example.domain.Recipes.model.Recipe
import com.example.myapplication.Recipes.navigation.Routes
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroceriesScreen(
    navController: NavController,
    viewModel: GroceriesViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val brush = remember {
        Brush.linearGradient(
            colors = listOf(Color(0xFFE4DB40), Color(0xFF3AC42A))
        )
    }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RectangleShape,
                color = Color.Transparent
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(brush)
                        .padding(top = 12.dp, bottom = 8.dp)
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
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
                            text = "Выбор продуктов",
                            fontSize = 18.sp,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.weight(1f))

                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp)
                            .clickable {
                                viewModel.onEvent(GroceriesEvent.SelectOnly)
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = uiState.onlyGroceries,
                            onCheckedChange = {
                                viewModel.onEvent(GroceriesEvent.SelectOnly)
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color.White,
                                uncheckedColor = Color.White.copy(alpha = 0.7f)
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Только эти продукты",
                            fontSize = 14.sp,
                            color = Color.White
                        )

                    }
                }
            }
        },
        bottomBar = {
            Button(
                onClick = {
                    //Возможно тут надо проверить uiState.selectedItems.isNotEmpty()

                    val selectedItemsStr = uiState.selectedItems.joinToString(",")
                    val onlyGroceriesStr = if (uiState.onlyGroceries) "true" else "false"

                    Log.d("my message", "Переход на др страницу")
                    navController.navigate(
                        "${Routes.GroceryRecipes.route}/$selectedItemsStr/$onlyGroceriesStr"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(40.dp)
                    .clip(RoundedCornerShape(26.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                enabled = uiState.selectedItems.isNotEmpty(),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(brush),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Применить фильтр",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Ошибка: ${uiState.error}", color = Color.Red)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { viewModel.onEvent(GroceriesEvent.Reset) }) {
                                Text("Повторить")
                            }
                        }
                    }
                }


                else -> {
                    SelectionScreen(
                        groceries = uiState.groceries,
                        groceryItems = uiState.groceryItems,
                        selectedItems = uiState.selectedItems,
                        expandedGroceries = uiState.expandedCategories,
                        onItemClick = { itemId ->
                            viewModel.onEvent(GroceriesEvent.ToggleGroceryItem(itemId))
                        },
                        onGroceryClick = { categoryId ->
                            viewModel.onEvent(GroceriesEvent.ToggleGrocery(categoryId))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SelectionScreen(
    groceries: List<Grocery>,
    groceryItems: List<GroceryItem>,
    selectedItems: Set<Int>,
    expandedGroceries: Set<Int>,
    onGroceryClick: (Int) -> Unit,
    onItemClick: (Int) -> Unit
) {


    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        groceries.forEach { grocery ->
            val itemsInGroup = groceryItems.filter { it.groceryId == grocery.id }
            if (itemsInGroup.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                            .clickable { onGroceryClick(grocery.id) },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = grocery.name,
                                fontSize = 18.sp,
                                color = Color.Black
                            )
                            if (expandedGroceries.contains(grocery.id)) {
                                Spacer(modifier = Modifier.height(8.dp))

                                itemsInGroup.forEach { item ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        val isSelected = selectedItems.contains(item.id)
                                        FilterChip(
                                            onClick = { onItemClick(item.id) },
                                            label = {
                                                Text(
                                                    item.name,
                                                    fontSize = 12.sp
                                                )
                                            },
                                            selected = isSelected,
                                            modifier = Modifier.weight(1f),
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = Color(0xFF3AC42A),
                                                selectedLabelColor = Color.White
                                            ),
                                            leadingIcon = if (isSelected) {
                                                {
                                                    Icon(
                                                        imageVector = Icons.Filled.Done,
                                                        contentDescription = "Done icon",
                                                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                                                    )
                                                }
                                            } else {
                                                null
                                            },
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

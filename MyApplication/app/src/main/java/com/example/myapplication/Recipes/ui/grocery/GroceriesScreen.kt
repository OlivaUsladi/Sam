package com.example.myapplication.Recipes.ui.grocery


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


//Set превратить в строку с разделителем /
//убрать showResults
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
            //сюда добавить чек-бокс
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
                //onClick = { viewModel.onEvent(GroceriesEvent.SearchRecipes) },
                onClick = {
                    //Возможно тут надо проверить uiState.selectedItems.isNotEmpty()

                    val selectedItemsStr = uiState.selectedItems.joinToString("/")
                    val onlyGroceriesStr = if (uiState.onlyGroceries) "true" else "false"

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

                //uiState.showResults -> {
                    // Экран с результатами
//                    ResultsScreen(
//                        exactMatchRecipes = uiState.exactMatchRecipes,
//                        recipesWithMissing = uiState.recipesWithMissing,
//                        onBackClick = { viewModel.onEvent(GroceriesEvent.ClearSelection) },
//                        navController = navController
//                    )
                //}

                else -> {
                    // Экран выбора продуктов
                    SelectionScreen(
                        groceries = uiState.groceries,
                        groceryItems = uiState.groceryItems,
                        selectedItems = uiState.selectedItems,
                        onItemClick = { itemId ->
                            viewModel.onEvent(GroceriesEvent.ToggleGroceryItem(itemId))
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
    onItemClick: (Int) -> Unit
) {
    val groceriesStates = remember { mutableStateMapOf<Int, Boolean>() }

    LaunchedEffect(groceries) {
        groceries.forEach { grocery ->
            if (!groceriesStates.containsKey(grocery.id)) {
                groceriesStates[grocery.id] = false
            }
        }
    }

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
                        modifier = Modifier.fillMaxWidth().clickable(onClick = { groceriesStates[grocery.id]?.let { groceriesStates[grocery.id] = !it } }),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = grocery.name,
                                fontSize = 18.sp,
                                color = Color.Black
                            )
                            if (groceriesStates[grocery.id] == true) {
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

@Composable
fun ResultsScreen(
    exactMatchRecipes: List<Recipe>,
    recipesWithMissing: List<Recipe>,  // ← просто список
    onBackClick: () -> Unit,
    navController: NavController
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Кнопка назад
        item {
            Button(
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE4DB40))
            ) {
                Text("← Выбрать другие продукты", color = Color.Black)
            }
        }

        // Рецепты, которые можно приготовить из выбранных продуктов
        if (exactMatchRecipes.isNotEmpty()) {
            item {
                Text(
                    text = "Абсолютное совпадение продуктов",
                    fontSize = 18.sp,
                    color = Color(0xFF3AC42A)
                )
            }

            items(exactMatchRecipes) { recipe ->
                RecipeResultCard(
                    recipe = recipe,
                    showMissingLabel = false,
                    navController = navController
                )
            }
        }

        // Рецепты с дополнительными ингредиентами
        if (recipesWithMissing.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Нужно докупить продукты",
                    fontSize = 18.sp,
                    color = Color(0xFFE4DB40)
                )
            }

            items(recipesWithMissing) { recipe ->
                RecipeResultCard(
                    recipe = recipe,
                    showMissingLabel = true,
                    navController = navController
                )
            }
        }

        // Если ничего не найдено
        if (exactMatchRecipes.isEmpty() && recipesWithMissing.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxSize().height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Рецептов не найдено\nПопробуйте выбрать другие продукты",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun RecipeResultCard(
    recipe: Recipe,
    showMissingLabel: Boolean,
    navController: NavController
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate("recipe/${recipe.id}")
            },
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = recipe.title,
                fontSize = 16.sp,
                color = Color.Black
            )

            if (showMissingLabel) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Нужно докупить некоторые продукты :",
                    fontSize = 12.sp,
                    color = Color(0xFFE4DB40)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "⏱ ${recipe.cookingTimeMinutes} мин",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = "❤️ ${recipe.likesCount}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
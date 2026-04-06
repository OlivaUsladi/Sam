//package com.example.myapplication.Recipes.ui.grocery
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.height
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import androidx.navigation.NavController
//import com.example.myapplication.Recipes.ui.category.RecipeCategoriesViewModel
//import org.koin.androidx.compose.koinViewModel
//
//@Composable
//fun GroceriesScreen(
//    navController: NavController,
//    viewModel: GroceriesViewModel = koinViewModel()
//){
//    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
//
//    val brush = remember {
//        Brush.linearGradient(
//            colors = listOf(Color(0xFFE4DB40), Color(0xFF3AC42A))
//        )
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFFF5F5F5))
//    ) {
//        Spacer(modifier = Modifier.height(10.dp))
//
//    }
//}
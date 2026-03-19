package com.example.myapplication.Recipes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.Recipes.navigation.RecipeNavHost
import com.example.myapplication.Recipes.ui.theme.MyApplicationTheme

class RecipeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            Scaffold(
                topBar = { RecipeTopAppBar(navController = navController) },
                bottomBar = { RecipeBottomAppBar(navController) },
                content = { paddingValues ->
                    Box(modifier = Modifier.padding(paddingValues)) {
                        RecipeNavHost(navController = navController)
                    }
                }
            )
        }
    }
}



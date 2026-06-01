package com.example.myapplication.Recipes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.Finance.components.OfflineBanner
import com.example.myapplication.Recipes.navigation.RecipeNavHost

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
                    Column(modifier = Modifier.padding(paddingValues)) {
                        OfflineBanner()
                        RecipeNavHost(navController = navController)
                    }
                },
            )
        }
    }
}

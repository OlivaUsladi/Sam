package com.example.myapplication.Finance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.Finance.navigation.FinanceNavHost

class FinanceActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            Scaffold(
                topBar = { FinanceTopAppBar(navController = navController) },
                bottomBar = { FinanceBottomAppBar(navController) },
                content = { paddingValues ->
                    Box(modifier = Modifier.padding(paddingValues)) {
                        FinanceNavHost(navController = navController)
                    }
                }
            )
        }
    }
}

package com.example.myapplication.Hints

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.Hints.navigation.HintsNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            Surface {
                Scaffold(
                    topBar = { HintsTopAppBar(navController = navController) },
                    bottomBar = { HintsBottomAppBar(navController) },
                    content = { paddingValues ->
                        Box(modifier = Modifier.padding(paddingValues)) {
                            HintsNavHost(navController = navController)
                        }
                    }
                )
            }
        }
    }
}
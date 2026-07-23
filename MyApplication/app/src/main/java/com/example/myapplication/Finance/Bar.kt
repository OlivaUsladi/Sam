package com.example.myapplication.Finance

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Notifications
import com.example.myapplication.R
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.myapplication.Finance.navigation.FinanceRoutes
import com.example.myapplication.Finance.theme.FinanceColors
import com.example.myapplication.Hints.MainActivity
import com.example.myapplication.Profile.ProfileActivity
import com.example.myapplication.Recipes.RecipeActivity
import kotlinx.coroutines.launch

@Composable
fun FinanceTopAppBar(navController: NavController) {
    val items = listOf("Самоорганизация", "Кулинария", "Финансовый менеджер")
    val selectedItem = remember { mutableStateOf(items[2]) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val intentHints = Intent(context, MainActivity::class.java)
    val intentRecipe = Intent(context, RecipeActivity::class.java)

    Box(Modifier.fillMaxWidth().height(if (drawerState.isOpen) 250.dp else 90.dp)) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .clip(RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp))
                        .background(FinanceColors.HeaderBackground),
                    drawerContainerColor = Color.Transparent,
                ) {
                    items.forEach { item ->
                        TextButton(onClick = {
                            scope.launch { drawerState.close() }
                            selectedItem.value = item
                            when (item) {
                                "Самоорганизация" -> context.startActivity(intentHints)
                                "Кулинария"       -> context.startActivity(intentRecipe)
                                "Финансовый менеджер" -> navController.navigate(FinanceRoutes.Accounts.route)
                            }
                        }) {
                            Text(item, fontSize = 22.sp, color = Color.White)
                        }
                    }
                }
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(FinanceColors.HeaderBackground)
                ) {
                    Spacer(modifier = Modifier.height(40.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(Modifier.padding(start = 5.dp).clickable() {
                            scope.launch { drawerState.open() }
                        }) {
                            Image(
                                painter = painterResource(R.drawable.finane_ball),
                                contentDescription = "menu",
                                modifier = Modifier.size(30.dp)
                            )
                        }
                        Box() {
                            Column() {
                                Spacer(modifier = Modifier.height(5.dp))
                                Text(
                                    text = "Финансы",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Box(Modifier.padding(end = 5.dp).clickable {
                            context.startActivity(Intent(context, ProfileActivity::class.java))
                        }) {
                            Image(
                                painter = painterResource(R.drawable.img),
                                contentDescription = "man icon",
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun FinanceBottomAppBar(navController: NavController) {
    val backStack by navController.currentBackStackEntryAsState()
    val current = backStack?.destination?.route

    BottomAppBar(
        containerColor = Color.White,
        contentColor = Color.LightGray,
        modifier = Modifier.height(80.dp),
    ) {
        BottomTabItem(
            label = "Счета",
            icon = Icons.Default.AccountBalanceWallet,
            selected = current == FinanceRoutes.Accounts.route,
            onClick = {
                navController.navigate(FinanceRoutes.Accounts.route) {
                    launchSingleTop = true
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    restoreState = true
                }
            },
            modifier = Modifier.weight(1f),
        )
        Spacer(Modifier.weight(0.05f))
        BottomTabItem(
            label = "Аналитика",
            icon = Icons.Default.Search,
            selected = current == FinanceRoutes.Analytics.route,
            onClick = {
                navController.navigate(FinanceRoutes.Analytics.route) {
                    launchSingleTop = true
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    restoreState = true
                }
            },
            modifier = Modifier.weight(1f),
        )
        Spacer(Modifier.weight(0.05f))
        BottomTabItem(
            label = "Категории",
            icon = Icons.AutoMirrored.Filled.List,
            selected = current == FinanceRoutes.Categories.route,
            onClick = {
                navController.navigate(FinanceRoutes.Categories.route) {
                    launchSingleTop = true
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    restoreState = true
                }
            },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun BottomTabItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(if (selected) 24.dp else 20.dp),
            tint = if (selected) FinanceColors.TextPrimary else Color.LightGray,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = if (selected) 12.sp else 10.sp,
            color = if (selected) FinanceColors.TextPrimary else Color.LightGray,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
        )
    }
}
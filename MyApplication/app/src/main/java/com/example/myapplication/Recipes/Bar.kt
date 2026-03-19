package com.example.myapplication.Recipes

import android.content.Intent
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.Hints.MainActivity
import com.example.myapplication.R
import com.example.myapplication.Recipes.navigation.Routes
import kotlinx.coroutines.launch

@Composable
fun RecipeTopAppBar(navController: NavController) {
    val items = listOf("Самоорганизация", "Кулинария", "Финансовый менеджер")
    val selectedItem = remember { mutableStateOf(items[0]) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val brush = remember {
        Brush.linearGradient(
            colors = listOf(Color(0xFFE4DB40), Color(0xFF3AC42A))
        )
    }

    val intentHints = Intent(context, MainActivity::class.java)

    Box(Modifier.fillMaxWidth().height(if (drawerState.isOpen) 250.dp else 90.dp)) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet (modifier = Modifier
                    .fillMaxWidth(0.8f).clip(RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp))
                    .background(brush),
                    drawerContainerColor = Color.Transparent){
                    items.forEach { item ->
                        TextButton(
                            onClick = {
                                scope.launch { drawerState.close() }
                                selectedItem.value = item
                                when (selectedItem.value){
                                    "Самоорганизация" -> context.startActivity(intentHints)
                                    "Кулинария" -> navController.navigate("home")
                                    //"Финансовый менеджер" -> context.startActivity(intenttips)
                                }
                            },
                        ) { Text(item, fontSize = 22.sp, color = Color.White) }
                    }
                }


            },
            content={
                Column(modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF000000))) {
                    Spacer(modifier = Modifier.height(30.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(Modifier.padding(start = 5.dp).clickable() {
                            scope.launch { drawerState.open() }
                        }) {
                            Image(
                                painter = painterResource(R.drawable.img_4),
                                contentDescription = "menu_recipe",
                                modifier = Modifier.size(30.dp)
                            )
                        }
                        Box() {
                            Column() {
                                Spacer(modifier = Modifier.height(5.dp))
                                Text(
                                    text = "Кулинария",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Box() {
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
fun RecipeBottomAppBar(navController: NavController) {
    BottomAppBar(
        containerColor = Color.White,
        contentColor = Color.LightGray,
        modifier = Modifier.height(80.dp)
    ) {
        val selectedButton = remember { mutableStateOf(0) }
        Column( modifier = Modifier
            .weight(1f)
            .fillMaxSize()
            .clickable(onClick = {
                selectedButton.value = 0
                navController.navigate(Routes.Home.route)
            }),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Icon(painter = painterResource(R.drawable.icon_food), contentDescription = "Меню",
                modifier = Modifier.size(if  (selectedButton.value==0) 22.dp else 18.dp),
                tint = if  (selectedButton.value==0) Color.Black else Color.LightGray)
            Text(text="Рецепты",
                fontSize = if (selectedButton.value==0) 12.sp else 10.sp,
                color = if (selectedButton.value==0) Color.Black else Color.LightGray,
                fontWeight = if (selectedButton.value==0) FontWeight.Bold else FontWeight.Normal)
        }
        Spacer(Modifier.weight(1f, true))
        Column(modifier = Modifier
            .weight(1f)
            .fillMaxSize()
            .clickable(onClick = {
                selectedButton.value =1
                navController.navigate(Routes.Favourite.route)
            }),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Icon(
                painter = painterResource(R.drawable.icon_list),
                contentDescription = "Списки",
                modifier = Modifier.size(if  (selectedButton.value==1) 22.dp else 18.dp),
                tint = if  (selectedButton.value==1) Color.Black else Color.LightGray
            )
            Text(text="Списки",
                fontSize = if (selectedButton.value==1) 12.sp else 10.sp,
                color = if (selectedButton.value==1) Color.Black else Color.LightGray,
                fontWeight = if (selectedButton.value==1) FontWeight.Bold else FontWeight.Normal)
        }
        Spacer(Modifier.weight(1f, true))
        Column(modifier = Modifier
            .weight(1f)
            .fillMaxSize()
            .clickable(onClick = {
                selectedButton.value =1
                navController.navigate(Routes.Favourite.route)
            }),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Icon(
                painter = painterResource(R.drawable.favourite),
                contentDescription = "Bookmark",
                modifier = Modifier.size(if  (selectedButton.value==1) 22.dp else 18.dp),
                tint = if  (selectedButton.value==1) Color.Black else Color.LightGray
            )
            Text(text="Избранное",
                fontSize = if (selectedButton.value==1) 12.sp else 10.sp,
                color = if (selectedButton.value==1) Color.Black else Color.LightGray,
                fontWeight = if (selectedButton.value==1) FontWeight.Bold else FontWeight.Normal)
        }
    }
}
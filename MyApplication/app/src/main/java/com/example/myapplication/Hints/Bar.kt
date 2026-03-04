package com.example.myapplication.Hints

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
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.R
import kotlinx.coroutines.launch

@Composable
fun HintsTopAppBar(navController: NavController) {
    val items = listOf("Статьи по самоорганизации", "Кулинарная база", "Финансовый менеджер")
    val selectedItem = remember { mutableStateOf(items[0]) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    //val intentrec = Intent(context, Recipes::class.java)

    Box(Modifier.fillMaxWidth().height(if (drawerState.isOpen) 250.dp else 90.dp)) {
        //Вот здесь под стиль сделать
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet{
                    items.forEach { item ->
                        TextButton(
                            onClick = {
                                scope.launch { drawerState.close() }
                                selectedItem.value = item
                                when (selectedItem.value){
                                    "Статьи по самоорганизации" -> navController.navigate("home")
                                    //"Кулинарная база" -> context.startActivity(intentrec)
                                    //"Финансовый менеджер" -> context.startActivity(intenttips)
                                }
                            },
                        ) { Text(item, fontSize = 22.sp) }
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
                                painter = painterResource(R.drawable.img_1),
                                contentDescription = "menu",
                                modifier = Modifier.size(30.dp)
                            )
                        }
                        Box() {
                            Column() {
                                Spacer(modifier = Modifier.height(5.dp))
                                Text(
                                    text = "Самоорганизация",
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

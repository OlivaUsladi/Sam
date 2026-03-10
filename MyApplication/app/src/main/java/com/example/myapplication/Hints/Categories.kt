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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.domain.Hints.model.Category
import com.example.myapplication.R

@Composable
fun CategoriesScreen(navController: NavController){
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF000000))) {
        Spacer(modifier = Modifier.height(10.dp))
        var categories = remember { mutableStateListOf<Category>() }

        val brush = remember {
            Brush.linearGradient(
                colors = listOf(Color(0xFF2670CC), Color(0xFF26CCAD))
            )
        }

        categories.add(Category(
            id = 1,
            name = "Продуктивность",
            description = "Статьи об увеличении ритма жизни, чтобы больше успеть"
        ))
        categories.add(Category(
            id = 2,
            name = "Здоровье",
            description = "Статьи о здоровом образе жизни"
        ))
        categories.add(Category(
            id = 3,
            name = "Привычки",
            description = "Статьи о формировании полезных привычек"
        ))
        categories.add(Category(
            id = 4,
            name = "Организация",
            description = "Статьи о том, какие документы, куда, зачем предоставлять"
        ))
        categories.add(Category(
            id = 5,
            name = "ЖКХ",
            description = "Статьи о том, как работать с коммунальными услугами"
        ))

        Row(modifier = Modifier.fillMaxWidth().padding(start=10.dp)){
            Box(modifier = Modifier.clickable(onClick = {navController.navigate(Routes.Home.route)})){
                Image(painter = painterResource(R.drawable.arrow_back),
                    contentDescription = "Стрелка назад",
                    modifier = Modifier.size(24.dp))
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        LazyColumn() {
            items(categories){
                category ->
                Row(){
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                            .padding(horizontal = 20.dp, vertical = 3.dp)
                            .clip(RoundedCornerShape(40.dp))
                            .background(brush = brush)
                            .clickable(onClick = {
                                navController.navigate("categoryArticles/${category.id}/${category.name}")
                            })
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = category.name,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                            Image(
                                painter = painterResource(R.drawable.img_2),
                                contentDescription = "arrow",
                                modifier = Modifier
                                    .size(20.dp)
                                    .align(Alignment.CenterVertically)
                            )
                        }
                    }
                }
            }
        }

    }
}
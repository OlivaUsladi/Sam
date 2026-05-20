package com.example.myapplication.Auth.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.Auth.components.OrangePrimaryButton
import com.example.myapplication.Auth.components.PageDots
import com.example.myapplication.Auth.theme.AuthColors
import com.example.myapplication.R
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit
) {
    val pagerState = rememberPagerState(initialPage = 0) { 3 }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AuthColors.Background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(40.dp))

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { page ->
            when (page) {
                0 -> OnboardingPage1()
                1 -> OnboardingPage2()
                else -> OnboardingPage3(
                    onStartClick = onFinished
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        PageDots(
            count = 3,
            current = pagerState.currentPage,
            modifier = Modifier.padding(bottom = 24.dp)
        )
    }
}

@Composable
private fun OnboardingPage1() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "SAM",
            color = AuthColors.Orange,
            fontSize = 84.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic
        )
        Spacer(Modifier.height(48.dp))
        Text(
            text = "SAM поможет тебе решить вопросы, которые чаще всего встречаются в самостоятельной жизни",
            color = AuthColors.TextPrimary,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

@Composable
private fun OnboardingPage2() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = "Здесь собраны статьи, которые помогут разобраться во многих вопросах самостоятельно",
            color = AuthColors.TextPrimary,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(R.drawable.ellipse4),
                contentDescription = "",
                modifier = Modifier.size(200.dp)
            )
            Spacer(Modifier.width(8.dp))
            Bubble(110.dp, Brush.linearGradient(listOf(Color(0xFF42A5F5), Color(0xFF26C6DA))))
        }
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Bubble(110.dp, Brush.linearGradient(listOf(Color(0xFF9CCC65), Color(0xFFFFCA28))))
            Spacer(Modifier.width(8.dp))
            Image(
                painter = painterResource(R.drawable.ellipse5),
                contentDescription = "",
                modifier = Modifier.size(200.dp)
            )
        }

        Text(
            text = "А также инструменты для планирования своего питания",
            color = AuthColors.TextPrimary,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
}

@Composable
private fun Bubble(size: Dp, brush: Brush) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(50))
            .background(brush)
    )
}

@Composable
private fun OnboardingPage3(onStartClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Image(painter = painterResource(R.drawable.heart),
                    contentDescription = "heart",
                    modifier = Modifier
                        .size(96.dp)
                        .clip(RoundedCornerShape(16.dp)))
                Image(painter = painterResource(R.drawable.fork),
                    contentDescription = "fork",
                    modifier = Modifier
                        .size(96.dp)
                        .clip(RoundedCornerShape(16.dp)))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Image(painter = painterResource(R.drawable.woman),
                    contentDescription = "woman",
                    modifier = Modifier
                        .size(96.dp)
                        .clip(RoundedCornerShape(16.dp)))
                Image(painter = painterResource(R.drawable.bookmark_1),
                    contentDescription = "bookmark",
                    modifier = Modifier
                        .size(96.dp)
                        .clip(RoundedCornerShape(16.dp)))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Image(painter = painterResource(R.drawable.square_medical),
                    contentDescription = "square_medical",
                    modifier = Modifier
                        .size(96.dp)
                        .clip(RoundedCornerShape(16.dp)))
                Image(painter = painterResource(R.drawable.veget_1),
                    contentDescription = "vegetables",
                    modifier = Modifier
                        .size(96.dp)
                        .clip(RoundedCornerShape(16.dp)))
            }
        }

        Text(
            text = "Начни свой путь сам!\nА мы поможем",
            color = AuthColors.TextPrimary,
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )

        OrangePrimaryButton(
            text = "Начать!",
            onClick = onStartClick,
            modifier = Modifier
                .fillMaxWidth(0.6f)
        )
    }
}

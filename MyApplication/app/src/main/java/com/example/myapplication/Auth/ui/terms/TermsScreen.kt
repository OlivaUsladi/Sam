package com.example.myapplication.Auth.ui.terms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.Auth.theme.AuthColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsScreen(
    onBack: () -> Unit
) {
    Scaffold(
        containerColor = AuthColors.Background,
        topBar = {
            TopAppBar(
                title = { Text("Пользовательское соглашение", fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад",
                            tint = AuthColors.TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AuthColors.Background,
                    titleContentColor = AuthColors.TextPrimary
                )
            )
        }
    ) { padding ->
        TermsBody(padding)
    }
}

@Composable
private fun TermsBody(padding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AuthColors.Background)
            .padding(padding)
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Section(
            title = "1. Общие положения",
            body = "Настоящее Соглашение регулирует отношения между пользователем мобильного приложения SAM (далее — Приложение) и оператором персональных данных. Используя Приложение, вы подтверждаете, что прочли и приняли настоящее соглашение, а также даёте согласие на обработку своих персональных данных согласно ст. 9 Федерального закона от 27.07.2006 № 152-ФЗ «О персональных данных»."
        )

        Section(
            title = "2. Какие данные обрабатываются",
            body = "Оператор обрабатывает следующие персональные данные пользователя:\n\n• имя, указанное при регистрации;\n• адрес электронной почты;\n• хэш пароля (сам пароль оператору неизвестен);\n• техническая информация (дата регистрации, дата последнего входа)."
        )

        Section(
            title = "3. Цель обработки",
            body = "Персональные данные обрабатываются исключительно с целью:\n\n• идентификации пользователя для предоставления доступа к Приложению;\n• обеспечения работы функций сохранения избранных рецептов, статей и списков покупок;\n• восстановления доступа к учётной записи."
        )

        Section(
            title = "4. Как защищаются данные",
            body = "Оператор реализует следующие меры защиты:\n\n• пароль хранится только в виде криптографического хэша (алгоритм BCrypt с фактором 12);\n• адрес электронной почты хранится в зашифрованном виде (AES-256-GCM), для поиска используется отдельный HMAC-SHA-256 хэш, не подлежащий обратному восстановлению;\n• передача данных между мобильным приложением и сервером защищена, доступ к личным эндпоинтам возможен только после предъявления подписанного JWT-токена;\n• токены доступа хранятся в защищённой области приложения (EncryptedSharedPreferences, ключ шифрования — в Android Keystore)."
        )

        Section(
            title = "5. Сроки и удаление",
            body = "Персональные данные хранятся до момента отзыва согласия. Пользователь вправе в любое время потребовать удаления своей учётной записи и всех связанных данных, направив запрос оператору."
        )

        Section(
            title = "6. Права пользователя",
            body = "Пользователь имеет право:\n\n• запросить информацию о составе обрабатываемых данных;\n• потребовать уточнения, блокирования или уничтожения данных;\n• отозвать настоящее согласие в любой момент."
        )

        Section(
            title = "7. Изменения соглашения",
            body = "Оператор вправе изменять настоящее соглашение. При каждом изменении версия документа увеличивается. Пользователь будет повторно проинформирован и должен будет подтвердить согласие с новой версией."
        )

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun Section(title: String, body: String) {
    Text(
        text = title,
        color = AuthColors.TextPrimary,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
    )
    Text(
        text = body,
        color = AuthColors.TextPrimary,
        fontSize = 14.sp
    )
}

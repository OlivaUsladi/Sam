package com.example.myapplication.Finance.ui.bankreport

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.domain.Finance.model.ImportReport
import com.example.myapplication.Finance.components.*
import com.example.myapplication.Finance.theme.FinanceColors
import org.koin.androidx.compose.koinViewModel

@Composable
fun BankReportsScreen(
    navController: NavController,
    vm: BankReportsViewModel = koinViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        val name = queryFileName(context, uri)
        val bytes = try {
            context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
        } catch (_: Throwable) { null }
        if (bytes != null) {
            vm.onEvent(BankReportsEvent.Import(name, bytes))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FinanceColors.Background),
    ) {
        FinanceHeader(
            title = "Загрузка выписки",
            subtitle = "PDF от банка → транзакции",
            actionIcon = Icons.AutoMirrored.Filled.ArrowBack,
            onAction = { navController.navigateUp() },
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            FinanceCard {
                Text("На какой счёт записать операции",
                    color = FinanceColors.TextSecondary, fontSize = 12.sp)
                Spacer(Modifier.height(6.dp))
                AccountDropdown(
                    items = state.sources.map { it.id as Int? to it.name },
                    selectedId = state.selectedSourceId,
                    onSelect = { id -> id?.let { vm.onEvent(BankReportsEvent.SelectSource(it)) } },
                    placeholder = "Выбрать счёт",
                )
            }

            FinanceCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(FinanceColors.PrimarySoft),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Default.PictureAsPdf,
                            contentDescription = null,
                            tint = FinanceColors.PrimaryDark,
                            modifier = Modifier.size(28.dp),
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Выберите PDF-выписку",
                            color = FinanceColors.TextPrimary,
                            fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text("Файл не сохраняется, только распарсенные операции.",
                            color = FinanceColors.TextSecondary, fontSize = 11.sp)
                    }
                }
                Spacer(Modifier.height(10.dp))
                PrimaryButton(
                    text = if (state.isUploading) "Импортируем…" else "Выбрать файл",
                    onClick = { launcher.launch("application/pdf") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isUploading && state.selectedSourceId != null,
                )
            }

            state.lastReport?.let { ReportSummary(it) }

            state.error?.let {
                Text(it, color = FinanceColors.Expense, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun ReportSummary(r: ImportReport) {
    FinanceCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CheckCircle, contentDescription = null,
                tint = FinanceColors.Income, modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(r.fileName, fontWeight = FontWeight.SemiBold,
                    color = FinanceColors.TextPrimary, fontSize = 14.sp)
                Text("Импорт завершён", color = FinanceColors.TextSecondary, fontSize = 11.sp)
            }
        }
        Spacer(Modifier.height(10.dp))
        SummaryRow("Всего операций в файле", r.total.toString())
        SummaryRow("Добавлено новых", r.imported.toString(),
            highlight = FinanceColors.Income)
        SummaryRow("Пропущено как дубликаты", r.skipped.toString(),
            highlight = if (r.skipped > 0) FinanceColors.TextSecondary else null)
    }
}

@Composable
private fun SummaryRow(label: String, value: String, highlight: androidx.compose.ui.graphics.Color? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, color = FinanceColors.TextSecondary, fontSize = 13.sp)
        Text(value,
            color = highlight ?: FinanceColors.TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold)
    }
}

private fun queryFileName(context: android.content.Context, uri: Uri): String {
    var name = "statement.pdf"
    val cursor = context.contentResolver.query(uri, null, null, null, null) ?: return name
    cursor.use {
        if (it.moveToFirst()) {
            val idxName = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (idxName >= 0) name = it.getString(idxName) ?: name
        }
    }
    return name
}

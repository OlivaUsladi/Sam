package com.example.myapplication.Finance.ui.bankreport

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Refresh
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
import com.example.domain.Finance.model.BankReport
import com.example.myapplication.Finance.components.*
import com.example.myapplication.Finance.theme.FinanceColors
import org.koin.androidx.compose.koinViewModel
import java.time.format.DateTimeFormatter

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
        val (name, size) = queryFileName(context, uri)
        val bytes = try {
            context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
        } catch (_: Throwable) { null }
        if (bytes != null) {
            vm.onEvent(BankReportsEvent.Upload(name, size, bytes))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FinanceColors.Background),
    ) {
        FinanceHeader(
            title = "Отчёты из банков",
            subtitle = "Загрузите PDF-выписку",
            actionIcon = Icons.AutoMirrored.Filled.ArrowBack,
            onAction = { navController.navigateUp() },
        )

        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = FinanceColors.PrimaryDark)
            }
        } else if (state.reports.isEmpty()) {
            EmptyState(onPick = { launcher.launch("application/pdf") })
        } else {
            Column(modifier = Modifier.weight(1f)) {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    items(state.reports, key = { it.id }) { report ->
                        ReportCard(
                            report = report,
                            onDelete = { vm.onEvent(BankReportsEvent.Delete(report.id)) },
                            onProcess = { vm.onEvent(BankReportsEvent.Process(report.id)) },
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    SecondaryButton(
                        text = "Добавить ещё",
                        onClick = { launcher.launch("application/pdf") },
                        modifier = Modifier.weight(1f),
                    )
                    PrimaryButton(
                        text = "Обработать все",
                        onClick = { state.reports.forEach { vm.onEvent(BankReportsEvent.Process(it.id)) } },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        state.error?.let { Text(it, color = FinanceColors.Expense, modifier = Modifier.padding(16.dp)) }
    }
}

@Composable
private fun EmptyState(onPick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(40.dp))
                .background(FinanceColors.PrimarySoft),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Default.PictureAsPdf,
                contentDescription = null,
                tint = FinanceColors.PrimaryDark,
                modifier = Modifier.size(40.dp),
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            "Ещё нет загруженных отчётов",
            fontWeight = FontWeight.SemiBold,
            color = FinanceColors.TextPrimary,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Загрузите PDF-выписку из банка — мы разберём её на доходы и расходы.",
            color = FinanceColors.TextSecondary, fontSize = 13.sp,
        )
        Spacer(Modifier.height(20.dp))
        PrimaryButton(text = "Выбрать файл", onClick = onPick)
    }
}

@Composable
private fun ReportCard(
    report: BankReport,
    onDelete: () -> Unit,
    onProcess: () -> Unit,
) {
    FinanceCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(FinanceColors.PrimarySoft),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = FinanceColors.PrimaryDark)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(report.fileName, fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                    color = FinanceColors.TextPrimary)
                Text(
                    "${formatSize(report.sizeBytes)} · " +
                            report.uploadedAt.format(DateTimeFormatter.ofPattern("d MMM yyyy, HH:mm")),
                    color = FinanceColors.TextSecondary, fontSize = 11.sp,
                )
                Text(
                    if (report.processed) "Обработан" else "Не обработан",
                    color = if (report.processed) FinanceColors.Income else FinanceColors.Expense,
                    fontSize = 11.sp,
                )
            }
            if (!report.processed) {
                IconButton(onClick = onProcess) {
                    Icon(Icons.Default.Refresh, contentDescription = "Обработать",
                        tint = FinanceColors.PrimaryDark)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Удалить",
                    tint = FinanceColors.TextSecondary)
            }
        }
    }
}

private fun formatSize(bytes: Long): String =
    when {
        bytes >= 1_000_000 -> "%.1f МБ".format(bytes / 1_000_000.0)
        bytes >= 1_000     -> "%.1f КБ".format(bytes / 1_000.0)
        else               -> "$bytes Б"
    }

private fun queryFileName(context: android.content.Context, uri: Uri): Pair<String, Long> {
    var name = "report.pdf"
    var size = 0L
    val cursor = context.contentResolver.query(uri, null, null, null, null) ?: return name to size
    cursor.use {
        if (it.moveToFirst()) {
            val idxName = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val idxSize = it.getColumnIndex(OpenableColumns.SIZE)
            if (idxName >= 0) name = it.getString(idxName) ?: name
            if (idxSize >= 0) size = it.getLong(idxSize)
        }
    }
    return name to size
}

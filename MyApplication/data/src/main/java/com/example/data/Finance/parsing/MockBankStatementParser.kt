package com.example.data.Finance.parsing

import com.example.data.Finance.datasource.local.ParsedTransaction
import com.example.domain.Finance.model.TransactionType
import java.math.BigDecimal
import java.time.LocalDate

//Тут потом будет парсинг
object MockBankStatementParser {

    fun parse(fileName: String, content: ByteArray): List<ParsedTransaction> {
        val today = LocalDate.now()
        return listOf(
            ParsedTransaction(
                name = "Озон",
                amount = BigDecimal("1240.50"),
                type = TransactionType.EXPENSE,
                description = "из выписки $fileName",
                date = today.minusDays(8),
            ),
            ParsedTransaction(
                name = "Яндекс.Такси",
                amount = BigDecimal("320.00"),
                type = TransactionType.EXPENSE,
                description = "из выписки $fileName",
                date = today.minusDays(6),
            ),
            ParsedTransaction(
                name = "Пятёрочка",
                amount = BigDecimal("890.30"),
                type = TransactionType.EXPENSE,
                description = "из выписки $fileName",
                date = today.minusDays(4),
            ),
            ParsedTransaction(
                name = "Кэшбэк",
                amount = BigDecimal("215.00"),
                type = TransactionType.INCOME,
                description = "из выписки $fileName",
                date = today.minusDays(3),
            ),
            ParsedTransaction(
                name = "Аптека",
                amount = BigDecimal("470.00"),
                type = TransactionType.EXPENSE,
                description = "из выписки $fileName",
                date = today.minusDays(2),
            ),
        )
    }
}

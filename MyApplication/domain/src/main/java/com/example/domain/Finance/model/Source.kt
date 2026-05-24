package com.example.domain.Finance.model


//Источник (счёт)
data class Source(
    val id: Int,
    val name: String,
    val type: SourceType
)

enum class SourceType(val raw: String) {
    BANK("bank"),
    CASH("cash"),
    CARD("card");

    companion object {
        fun fromRaw(raw: String): SourceType =
            entries.firstOrNull { it.raw == raw } ?: BANK
    }
}

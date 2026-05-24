package com.example.domain.Finance.model

import java.math.BigDecimal

//Тэг расхода
data class Tag(
    val id: Int,
    val name: String,
    val totalAmountSpent: BigDecimal
)

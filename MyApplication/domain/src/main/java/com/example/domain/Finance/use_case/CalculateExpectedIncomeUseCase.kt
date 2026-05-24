package com.example.domain.Finance.use_case

import java.math.BigDecimal
import java.math.RoundingMode

//formula = sum * months * annualPercent / (12 * 100)
//БЕЗ КАПИТАЛИЗАЦИИ! Мб потом усложню
class CalculateExpectedIncomeUseCase {
    operator fun invoke(sum: BigDecimal, months: Int, annualPercent: BigDecimal): BigDecimal {
        if (months <= 0) return BigDecimal.ZERO
        if (sum.signum() <= 0) return BigDecimal.ZERO
        val rate = annualPercent.divide(BigDecimal(1200), 10, RoundingMode.HALF_UP)
        return sum.multiply(rate).multiply(BigDecimal(months))
            .setScale(2, RoundingMode.HALF_UP)
    }
}

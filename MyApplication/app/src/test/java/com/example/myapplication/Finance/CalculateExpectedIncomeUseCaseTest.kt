package com.example.domain.Finance.use_case

import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class CalculateExpectedIncomeUseCaseTest {

    private val useCase = CalculateExpectedIncomeUseCase()

    @Test
    fun `zero months returns zero`() {
        val result = useCase.invoke(BigDecimal("100000"), 0, BigDecimal("10"))
        assertEquals(0, BigDecimal.ZERO.compareTo(result))
    }

    @Test
    fun `negative months returns zero`() {
        val result = useCase.invoke(BigDecimal("100000"), -3, BigDecimal("10"))
        assertEquals(0, BigDecimal.ZERO.compareTo(result))
    }

    @Test
    fun `zero sum returns zero`() {
        val result = useCase.invoke(BigDecimal.ZERO, 12, BigDecimal("10"))
        assertEquals(0, BigDecimal.ZERO.compareTo(result))
    }

    @Test
    fun `negative sum returns zero`() {
        val result = useCase.invoke(BigDecimal("-100"), 12, BigDecimal("10"))
        assertEquals(0, BigDecimal.ZERO.compareTo(result))
    }

    @Test
    fun `100000 at 12 percent for 12 months returns 12000`() {
        val result = useCase.invoke(BigDecimal("100000"), 12, BigDecimal("12"))
        assertEquals(0, BigDecimal("12000.00").compareTo(result))
    }

    @Test
    fun `50000 at 8 percent for 6 months returns 2000`() {
        val result = useCase.invoke(BigDecimal("50000"), 6, BigDecimal("8"))
        assertEquals(0, BigDecimal("2000.00").compareTo(result))
    }

    @Test
    fun `result rounded to two decimals half up`() {
        val result = useCase.invoke(BigDecimal("12345"), 3, BigDecimal("7.5"))
        assertEquals(0, BigDecimal("231.47").compareTo(result))
    }
}

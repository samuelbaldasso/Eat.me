package com.samuelbaldasso.ifoodclone.core.domain.model

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class AppResultTest {

    @Test
    @DisplayName("GIVEN Success WHEN mapped THEN transformation is applied to data")
    fun testSuccessMap() {
        val result: AppResult<Int, AppError> = AppResult.Success(10)
        val mapped = result.map { it * 2 }

        assertThat(mapped).isInstanceOf(AppResult.Success::class.java)
        assertThat((mapped as AppResult.Success).data).isEqualTo(20)
    }

    @Test
    @DisplayName("GIVEN Error WHEN mapped THEN original error is preserved")
    fun testErrorMap() {
        val result: AppResult<Int, AppError> = AppResult.Error(AppError.Network)
        val mapped = result.map { it * 2 }

        assertThat(mapped).isInstanceOf(AppResult.Error::class.java)
        assertThat((mapped as AppResult.Error).error).isEqualTo(AppError.Network)
    }

    @Test
    @DisplayName("GIVEN Success and Error WHEN fold called THEN branches execute accordingly")
    fun testFold() {
        val success: AppResult<String, AppError> = AppResult.Success("Order #123")
        val successMsg = success.fold(
            onSuccess = { "OK: $it" },
            onError = { "ERR" }
        )
        assertThat(successMsg).isEqualTo("OK: Order #123")

        val error: AppResult<String, AppError> = AppResult.Error(AppError.Unauthorized)
        val errorMsg = error.fold(
            onSuccess = { "OK" },
            onError = { "ERR: $it" }
        )
        assertThat(errorMsg).isEqualTo("ERR: Unauthorized")
    }

    @Test
    @DisplayName("GIVEN Error with BusinessRule code WHEN checked THEN code and message are accessible")
    fun testBusinessRuleError() {
        val error: AppError = AppError.BusinessRule(code = "COUPON_EXPIRED", message = "O cupom informado expirou")
        assertThat((error as AppError.BusinessRule).code).isEqualTo("COUPON_EXPIRED")
        assertThat(error.message).isEqualTo("O cupom informado expirou")
    }
}

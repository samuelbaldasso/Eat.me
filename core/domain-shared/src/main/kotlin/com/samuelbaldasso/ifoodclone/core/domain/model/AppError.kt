package com.samuelbaldasso.ifoodclone.core.domain.model

sealed interface AppError {
    data object Network : AppError
    data object Timeout : AppError
    data object Unauthorized : AppError
    data class Validation(val message: String, val field: String? = null) : AppError
    data class BusinessRule(val code: String, val message: String? = null) : AppError
    data class Unknown(val throwable: Throwable? = null, val message: String? = null) : AppError
}

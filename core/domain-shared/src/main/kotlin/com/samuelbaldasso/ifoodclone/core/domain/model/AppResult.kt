package com.samuelbaldasso.ifoodclone.core.domain.model

sealed interface AppResult<out T, out E : AppError> {
    data class Success<out T>(val data: T) : AppResult<T, Nothing>
    data class Error<out E : AppError>(val error: E) : AppResult<Nothing, E>

    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error

    fun getOrNull(): T? = when (this) {
        is Success -> data
        is Error -> null
    }

    fun getOrDefault(defaultValue: @UnsafeVariance T): T = when (this) {
        is Success -> data
        is Error -> defaultValue
    }
}

inline fun <T, E : AppError, R> AppResult<T, E>.map(transform: (T) -> R): AppResult<R, E> = when (this) {
    is AppResult.Success -> AppResult.Success(transform(data))
    is AppResult.Error -> this
}

inline fun <T, E : AppError, R : AppError> AppResult<T, E>.mapError(transform: (E) -> R): AppResult<T, R> = when (this) {
    is AppResult.Success -> this
    is AppResult.Error -> AppResult.Error(transform(error))
}

inline fun <T, E : AppError, R> AppResult<T, E>.fold(
    onSuccess: (T) -> R,
    onError: (E) -> R
): R = when (this) {
    is AppResult.Success -> onSuccess(data)
    is AppResult.Error -> onError(error)
}

inline fun <T, E : AppError> AppResult<T, E>.onSuccess(action: (T) -> Unit): AppResult<T, E> {
    if (this is AppResult.Success) action(data)
    return this
}

inline fun <T, E : AppError> AppResult<T, E>.onError(action: (E) -> Unit): AppResult<T, E> {
    if (this is AppResult.Error) action(error)
    return this
}

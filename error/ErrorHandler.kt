// error/ErrorHandler.kt
package com.yourapp.core.error

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineExceptionHandler
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

sealed class AppError(
    open val message: String,
    open val cause: Throwable? = null
) {
    data class NetworkError(
        override val message: String = "Network error",
        override val cause: Throwable? = null
    ) : AppError(message, cause)

    data class ServerError(
        val code: Int,
        override val message: String = "Server error",
        override val cause: Throwable? = null
    ) : AppError(message, cause)

    data class ValidationError(
        override val message: String,
        override val cause: Throwable? = null
    ) : AppError(message, cause)

    data class AuthenticationError(
        override val message: String = "Authentication failed",
        override val cause: Throwable? = null
    ) : AppError(message, cause)

    data class UnknownError(
        override val message: String = "Unknown error",
        override val cause: Throwable? = null
    ) : AppError(message, cause)
}

@Singleton
class ErrorHandler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun getCoroutineExceptionHandler(
        onError: (AppError) -> Unit = {}
    ) = CoroutineExceptionHandler { _, exception ->
        val error = mapException(exception)
        onError(error)
        logError(error)
    }

    fun mapException(exception: Throwable): AppError {
        return when (exception) {
            is java.io.IOException -> {
                AppError.NetworkError(cause = exception)
            }
            is retrofit2.HttpException -> {
                when (exception.code()) {
                    401 -> AppError.AuthenticationError(cause = exception)
                    400 -> AppError.ValidationError(
                        message = exception.response()?.errorBody()?.string() ?: "Invalid request",
                        cause = exception
                    )
                    else -> AppError.ServerError(
                        code = exception.code(),
                        cause = exception
                    )
                }
            }
            else -> AppError.UnknownError(cause = exception)
        }
    }

    private fun logError(error: AppError) {
        Timber.e(error.cause, "App Error: ${error.message}")
    }

    fun getUserFriendlyMessage(error: AppError): String {
        return when (error) {
            is AppError.NetworkError -> "Please check your internet connection"
            is AppError.ServerError -> "Server error (${error.code}). Please try again later"
            is AppError.ValidationError -> error.message
            is AppError.AuthenticationError -> "Please log in again"
            is AppError.UnknownError -> "An unexpected error occurred"
        }
    }
}


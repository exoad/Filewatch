package net.exoad.filewatch.utils

sealed class Result<T, E> {
    data class Ok<T, E>(val value: T, val reason: E?) : Result<T, E>()

    data class Error<T, E>(val reason: E) : Result<T, E>()
}
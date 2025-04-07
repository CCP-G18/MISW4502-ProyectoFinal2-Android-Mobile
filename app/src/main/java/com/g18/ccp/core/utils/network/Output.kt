package com.g18.ccp.core.utils.network

sealed class Output<out T> {
    class Loading<out T> : Output<T>()
    data class Success<out T>(val data: T) : Output<T>()
    data class Failure<out Exception>(
        val exception: Exception,
        val message: String? = null,
        val codde: Int? = null) : Output<Nothing>()
}

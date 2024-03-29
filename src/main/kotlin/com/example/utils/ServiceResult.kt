package com.example.utils

sealed class ServiceResult<out T> {

    data class Success<out T>(val data: T): ServiceResult<T>()
    data class Error(val error: Errors): ServiceResult<Nothing>()
}

package com.fabirt.podcastapp.util

import com.fabirt.podcastapp.error.Failure

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val failure: Failure) : Resource<Nothing>()
    object Loading : Resource<Nothing>()
}
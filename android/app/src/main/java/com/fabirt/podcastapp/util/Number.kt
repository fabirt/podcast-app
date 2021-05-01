package com.fabirt.podcastapp.util

fun Long.toDurationMinutes(): String {
    val minutes = (this / 60).toInt()

    return "$minutes min"
}
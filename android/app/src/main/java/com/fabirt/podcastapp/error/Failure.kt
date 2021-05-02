package com.fabirt.podcastapp.error

import android.content.Context
import androidx.annotation.StringRes
import com.fabirt.podcastapp.R

sealed class Failure(@StringRes val key: Int) {

    object UnexpectedFailure : Failure(R.string.unexpected_error)

    fun translate(context: Context): String {
        return context.getString(key)
    }
}

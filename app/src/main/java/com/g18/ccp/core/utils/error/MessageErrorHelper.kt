package com.g18.ccp.core.utils.error

import android.content.Context
import androidx.core.content.ContextCompat.getString
import com.g18.ccp.R
import retrofit2.HttpException

private const val UNAUTHORIZED = 401
fun Exception?.getErrorMessage(context: Context): String {
    return when (this) {
        is HttpException -> if (code() == UNAUTHORIZED) {
            getString(context, R.string.invalid_credentials_text)
        } else {
            getString(context, R.string.login_error_message)
        }

        else -> getString(context, R.string.login_error_message)
    }
}

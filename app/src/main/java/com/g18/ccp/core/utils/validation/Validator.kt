package com.g18.ccp.core.utils.validation

import android.util.Patterns
import com.g18.ccp.core.constants.MIN_PASSWORD_LENGTH

object Validator {
    fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isPasswordValid(password: String): Boolean {
        return password.length >= MIN_PASSWORD_LENGTH
    }
}

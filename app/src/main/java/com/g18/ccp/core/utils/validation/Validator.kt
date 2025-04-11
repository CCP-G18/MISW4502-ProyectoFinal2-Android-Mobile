package com.g18.ccp.core.utils.validation

import com.g18.ccp.core.constants.MIN_PASSWORD_LENGTH

object Validator {
    fun isEmailValid(email: String): Boolean {
        return email.matches(Regex("^[A-Za-z0-9](\\.?[A-Za-z0-9_-])*@[A-Za-z0-9-]+(\\.[A-Za-z]{2,})+$"))

    }

    fun isPasswordValid(password: String): Boolean {
        return password.length >= MIN_PASSWORD_LENGTH
    }
}

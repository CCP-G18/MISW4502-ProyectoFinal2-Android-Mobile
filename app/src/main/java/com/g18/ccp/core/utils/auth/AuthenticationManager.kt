package com.g18.ccp.core.utils.auth

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

private const val PREFS_NAME = "secure_auth_prefs"
private const val KEY_ACCESS_TOKEN = "access_token"

class AuthenticationManager(private val context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val secureSharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveToken(token: String) {
        secureSharedPreferences.edit {
            putString(KEY_ACCESS_TOKEN, token)
        }
    }

    fun getToken(): String? {
        return secureSharedPreferences.getString(KEY_ACCESS_TOKEN, null)
    }

    fun clearToken() {
        secureSharedPreferences.edit {
            remove(KEY_ACCESS_TOKEN)
        }
    }
}

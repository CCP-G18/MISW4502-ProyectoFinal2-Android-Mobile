package com.g18.ccp.repository.auth

import com.g18.ccp.core.utils.network.Output

interface LoginRepository {
    suspend fun login(email: String, password: String): Output<Unit>
}

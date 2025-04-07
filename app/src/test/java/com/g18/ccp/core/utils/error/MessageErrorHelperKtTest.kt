package com.g18.ccp.core.utils.error

import android.content.Context
import androidx.core.content.ContextCompat
import com.g18.ccp.R
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException

class MessageErrorHelperKtTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = mockk()
        mockkStatic(ContextCompat::class)
    }

    @Test
    fun `given HttpException with 401 when getErrorMessage then returns invalid credentials message`() {
        // Given
        val exception = mockk<HttpException>()
        every { exception.code() } returns 401
        every {
            ContextCompat.getString(
                context,
                R.string.invalid_credentials_text
            )
        } returns "Credenciales inválidas"

        // When
        val result = exception.getErrorMessage(context)

        // Then
        assertEquals("Credenciales inválidas", result)
    }

    @Test
    fun `given HttpException with non-401 when getErrorMessage then returns generic login message`() {
        val exception = mockk<HttpException>()
        every { exception.code() } returns 500
        every {
            ContextCompat.getString(
                context,
                R.string.login_error_message
            )
        } returns "Error al iniciar sesión"

        val result = exception.getErrorMessage(context)

        assertEquals("Error al iniciar sesión", result)
    }

    @Test
    fun `given null exception when getErrorMessage then returns generic login message`() {
        every {
            ContextCompat.getString(
                context,
                R.string.login_error_message
            )
        } returns "Error al iniciar sesión"

        val result = null.getErrorMessage(context)

        assertEquals("Error al iniciar sesión", result)
    }

    @Test
    fun `given other exception when getErrorMessage then returns generic login message`() {
        val exception = Exception("algo salió mal")
        every {
            ContextCompat.getString(
                context,
                R.string.login_error_message
            )
        } returns "Error al iniciar sesión"

        val result = exception.getErrorMessage(context)

        assertEquals("Error al iniciar sesión", result)
    }
}


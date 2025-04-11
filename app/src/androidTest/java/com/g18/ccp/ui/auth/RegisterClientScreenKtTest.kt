package com.g18.ccp.ui.auth

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import com.g18.ccp.ui.MainActivity
import org.junit.Rule
import org.junit.Test

class RegisterClientScreenKtTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun showsButtonsAndNavigatesToLogin() {

        composeTestRule.onNodeWithText("Comercializadora de productos").assertIsDisplayed()
        composeTestRule.onNodeWithText("CCP").assertIsDisplayed()
        composeTestRule.onNodeWithText("Ingresar").assertIsDisplayed()

        composeTestRule.onNodeWithText("Ingresar").performClick()

    }

    @Test
    fun fullRegisterFlow_showsValidationErrors_andRegistersSuccessfully() {
        composeTestRule.onNodeWithText("Ingresar").performClick()
        composeTestRule.onNodeWithText("Registrase").performClick()


        composeTestRule.onNodeWithText("Registrar").performClick()


        composeTestRule.onNodeWithTag("register_name_field").performTextInput(" ")
        composeTestRule.onNodeWithText("Registrar", useUnmergedTree = true).performClick()
        composeTestRule.onNodeWithText("El nombre es obligatorio").assertExists()
        composeTestRule.onNodeWithTag("register_name_field").performTextInput("Ana")


        composeTestRule.onNodeWithTag("register_surname_field").performTextInput(" ")
        composeTestRule.onNodeWithText("Registrar").performClick()
        composeTestRule.onNodeWithText("El apellido es obligatorio").assertExists()
        composeTestRule.onNodeWithTag("register_surname_field").performTextInput("Pérez")


        composeTestRule.onNodeWithTag("register_id_type_field").performClick()
        composeTestRule.onNodeWithText("CC").performClick()


        composeTestRule.onNodeWithTag("register_id_field").performTextInput("ABC123")
        composeTestRule.onNodeWithText("Registrar").performClick()
        composeTestRule.onNodeWithText("Número de identificación inválido").assertExists()
        composeTestRule.onNodeWithTag("register_id_field").performTextClearance()
        composeTestRule.onNodeWithTag("register_id_field").performTextInput("123456789")


        composeTestRule.onNodeWithTag("register_email_field").performTextInput("correo@mal")
        composeTestRule.onNodeWithText("Registrar").performClick()
        composeTestRule.onNodeWithText("Correo inválido").assertExists()
        composeTestRule.onNodeWithTag("register_email_field").performTextClearance()
        composeTestRule.onNodeWithTag("register_email_field").performTextInput("correo@valido.com")


        composeTestRule.onNodeWithTag("register_password_field").performTextInput("123")
        composeTestRule.onNodeWithText("Registrar").performClick()
        composeTestRule.onNodeWithText("La contraseña no es válida").assertExists()
        composeTestRule.onNodeWithTag("register_password_field").performTextClearance()
        composeTestRule.onNodeWithTag("register_password_field").performTextInput("123456")


        composeTestRule.onNodeWithTag("register_confirm_password_field").performTextInput("abcdef")
        composeTestRule.onNodeWithText("Registrar").performClick()
        composeTestRule.onNodeWithText("Las contraseñas no coinciden").assertExists()
        composeTestRule.onNodeWithTag("register_confirm_password_field").performTextClearance()
        composeTestRule.onNodeWithTag("register_confirm_password_field").performTextInput("123456")

        composeTestRule.onNodeWithTag("register_country_field").performTextInput(" ")
        composeTestRule.onNodeWithText("El país es obligatorio").assertExists()
        composeTestRule.onNodeWithTag("register_country_field").performTextClearance()
        composeTestRule.onNodeWithTag("register_country_field").performTextInput("Colombia")

        composeTestRule.onNodeWithTag("register_city_field").performTextInput(" ")
        composeTestRule.onNodeWithText("La ciudad es obligatoria").assertExists()
        composeTestRule.onNodeWithTag("register_city_field").performTextClearance()
        composeTestRule.onNodeWithTag("register_city_field").performTextInput("Bogotá")

        composeTestRule.onNodeWithTag("register_address_field").performTextInput(" ")
        composeTestRule.onNodeWithText("La dirección es obligatoria").assertExists()
        composeTestRule.onNodeWithTag("register_address_field").performTextClearance()
        composeTestRule.onNodeWithTag("register_address_field").performTextInput("Calle 123")

        composeTestRule.onNodeWithText("Registrar").performClick()

    }
}

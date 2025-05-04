package com.g18.ccp.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class AppNavigationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()


    @Test
    fun givenWelcomeScreen_whenClickLogin_thenNavigatesToLogin() {
        // When
        composeTestRule.onNodeWithText("Ingresar").performClick()

        // Then
        composeTestRule.onNodeWithText("Correo").assertIsDisplayed()
    }

    @Test
    fun givenLoginScreen_whenClickRegister_thenNavigatesToRegisterScreen() {
        // Given
        composeTestRule.onNodeWithText("Ingresar").performClick()

        // When
        composeTestRule.onNodeWithText("Registrarse").performClick()

        // Then
        composeTestRule.onNodeWithText("Registro de Cliente").assertIsDisplayed()
    }

    @Test
    fun givenRegisterScreen_whenNavigateBack_thenGoesToWelcome() {
        // Given
        composeTestRule.onNodeWithText("Ingresar").performClick()
        composeTestRule.onNodeWithText("Registrarse").performClick()

        // When
        composeTestRule.onNodeWithTag("register_back_button").performClick()

        // Then
        composeTestRule.onNodeWithText("Ingresar").assertIsDisplayed()
    }
}
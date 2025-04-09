package com.g18.ccp.ui.auth

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.g18.ccp.ui.theme.CCPTheme
import org.junit.Rule
import org.junit.Test

class RegisterClientScreenKtTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun showsButtonsAndNavigatesToLogin() {
        composeTestRule.setContent {
            CCPTheme {
                WelcomeScreen(
                    onLoginClick = {

                    },
                )
            }
        }

        composeTestRule.onNodeWithText("Comercializadora de productos").assertIsDisplayed()
        composeTestRule.onNodeWithText("CCP").assertIsDisplayed()
        composeTestRule.onNodeWithText("Ingresar").assertIsDisplayed()

        composeTestRule.onNodeWithText("Ingresar").performClick()

    }
}

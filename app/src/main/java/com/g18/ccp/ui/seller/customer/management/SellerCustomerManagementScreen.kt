package com.g18.ccp.ui.seller.customer.management

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.g18.ccp.R
import com.g18.ccp.core.constants.SELLER_CUSTOMER_PERSONAL_INFO_BASE_ROUTE
import com.g18.ccp.core.constants.SELLER_CUSTOMER_RECOMMENDATIONS_BASE_ROUTE
import com.g18.ccp.core.constants.SELLER_CUSTOMER_VISITS_BASE_ROUTE
import com.g18.ccp.presentation.seller.customermanagement.CustomerManagementUiState
import com.g18.ccp.presentation.seller.customermanagement.SellerCustomerManagementViewModel
import com.g18.ccp.ui.theme.BackgroundColor
import com.g18.ccp.ui.theme.BlackColor
import com.g18.ccp.ui.theme.ButtonBackgroundColor
import com.g18.ccp.ui.theme.MainColor
import org.koin.androidx.compose.koinViewModel

@Composable
fun SellerCustomerManagementScreen(
    modifier: Modifier = Modifier,
    viewModel: SellerCustomerManagementViewModel = koinViewModel(),
    navController: NavHostController
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is CustomerManagementUiState.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MainColor)
            }
        }

        is CustomerManagementUiState.Success -> {
            val customerData = state.customer

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(BackgroundColor)
                    .padding(vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = BlackColor,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = customerData.name,
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        fontWeight = FontWeight.SemiBold,
                        color = BlackColor
                    )
                }

                ActionRow(
                    icon = Icons.Outlined.Badge,
                    text = stringResource(R.string.customer_detail_personal_info),
                    onClick = {
                        navController.navigate(
                            "$SELLER_CUSTOMER_PERSONAL_INFO_BASE_ROUTE/${customerData.id}"
                        )
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
                ActionRow(
                    icon = Icons.Outlined.ShoppingCart,
                    text = stringResource(R.string.customer_detail_make_order),
                    onClick = { /* TODO: */ }
                )
                Spacer(modifier = Modifier.height(12.dp))
                ActionRow(
                    icon = Icons.Outlined.CalendarToday,
                    text = stringResource(R.string.customer_detail_visits),
                    onClick = {
                        navController.navigate(
                            "$SELLER_CUSTOMER_VISITS_BASE_ROUTE/${customerData.id}"
                        )
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
                ActionRow(
                    icon = Icons.Outlined.Lightbulb,
                    text = stringResource(R.string.customer_detail_recommendations),
                    onClick = {
                        navController.navigate(
                            "$SELLER_CUSTOMER_RECOMMENDATIONS_BASE_ROUTE/${customerData.id}"
                        )
                    }
                )
            }
        }

        is CustomerManagementUiState.Error -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error: ${state.message}",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        is CustomerManagementUiState.NotFound -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(R.string.customer_not_found))
            }
        }
    }
}

@Composable
private fun ActionRow(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = ButtonBackgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = BlackColor,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = text,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = BlackColor
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
                contentDescription = null,
                tint = BlackColor
            )
        }
    }
}

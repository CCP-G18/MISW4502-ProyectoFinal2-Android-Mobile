package com.g18.ccp.ui.order.seller.order.createorder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.g18.ccp.R
import com.g18.ccp.data.remote.model.seller.order.product.order.SellerCustomerOrderData
import com.g18.ccp.presentation.seller.order.createorder.SellerCustomerOrdersUiState
import com.g18.ccp.presentation.seller.order.createorder.SellerCustomerOrdersViewModel
import com.g18.ccp.ui.theme.BackgroundColor
import com.g18.ccp.ui.theme.BlackColor
import com.g18.ccp.ui.theme.LightBeige
import org.koin.androidx.compose.koinViewModel


@Composable
fun SellerCustomerOrdersScreen(
    viewModel: SellerCustomerOrdersViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(16.dp)
    ) {

        when (uiState) {
            SellerCustomerOrdersUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is SellerCustomerOrdersUiState.Error -> {
                Text(
                    text = (uiState as SellerCustomerOrdersUiState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }

            is SellerCustomerOrdersUiState.Success -> {
                val orders = (uiState as SellerCustomerOrdersUiState.Success).orders
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(orders) { order ->
                        OrderSummaryCard(order = order)
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderSummaryCard(order: SellerCustomerOrderData) {
    val formattedTotal = String.format("$%,.2f", order.total)
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = LightBeige),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.order_summary_title),
                style = MaterialTheme.typography.titleMedium,
                color = BlackColor
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.order_summary_delivery_date, order.date),
                style = MaterialTheme.typography.bodyMedium,
                color = BlackColor
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = stringResource(R.string.order_summary_total, formattedTotal),
                style = MaterialTheme.typography.bodyMedium,
                color = BlackColor
            )
        }
    }
}

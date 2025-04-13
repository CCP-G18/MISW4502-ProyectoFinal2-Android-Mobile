package com.g18.ccp.ui.order.status

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.g18.ccp.R
import com.g18.ccp.core.utils.network.Output
import com.g18.ccp.data.remote.model.order.Order
import com.g18.ccp.presentation.order.OrdersViewModel
import com.g18.ccp.ui.theme.LightGray
import com.g18.ccp.ui.theme.MainColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(viewModel: OrdersViewModel) {
    val state by viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.loadOrders()
    }

    when (val currentState = state) {
        is Output.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MainColor)
            }
        }

        is Output.Success -> {
            val orders = currentState.data
            LazyColumn(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(orders) { order ->
                    OrderCard(order)
                }
            }
        }

        is Output.Failure<*> -> {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.generic_network_error),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun OrderCard(order: Order) {
    Card(
        colors = CardDefaults.cardColors(containerColor = LightGray),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.order_delivery_prefix) + "  " + order.date,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MainColor,
                textAlign = TextAlign.Center
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(order.items.first().imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Mi imagen de perfil",
                    placeholder = painterResource(R.drawable.ic_launcher_foreground),
                    error = painterResource(R.drawable.ic_launcher_background),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(140.dp)
                        .width(100.dp)
                        .padding(end = 12.dp)
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp)
                ) {
                    Text(
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.fillMaxWidth(),
                        text = order.summary,
                        fontSize = 20.sp,
                        color = MainColor,
                        textAlign = TextAlign.Start,
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            stringResource(R.string.order_total_label),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MainColor,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.width(88.dp))
                        Text(
                            "$" + order.total,
                            modifier = Modifier.fillMaxWidth(),
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            color = MainColor,
                            textAlign = TextAlign.Center
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            stringResource(R.string.order_status_label),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MainColor,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.width(72.dp))
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                stringResource(order.status.labelRes),
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp,
                                color = MainColor,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                            Icon(
                                imageVector = order.status.icon,
                                contentDescription = order.status.name,
                                tint = order.status.color
                            )
                        }
                    }
                }
            }
        }
    }
}

package com.g18.ccp.ui.order.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.g18.ccp.R
import com.g18.ccp.core.constants.SPLASH_CONGRATS_ROUTE
import com.g18.ccp.core.utils.format.formatPrice
import com.g18.ccp.core.utils.network.Output
import com.g18.ccp.presentation.order.create.ListProductViewModel
import com.g18.ccp.ui.product.ProductItem
import com.g18.ccp.ui.theme.BackgroundColor
import com.g18.ccp.ui.theme.LightGray
import com.g18.ccp.ui.theme.MainColor

@Composable
fun CartScreen(viewModel: ListProductViewModel, navController: NavController) {
    val cartItems by viewModel.cart
    val total = viewModel.getOrderTotal()
    val orderState by viewModel.orderState.collectAsState()

    LaunchedEffect(orderState) {
        if (orderState is Output.Success) {
            navController.navigate(SPLASH_CONGRATS_ROUTE)
            viewModel.clearOrderState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(cartItems) { cartItem ->
                ProductItem(cartItem = cartItem, viewModel = viewModel, isDeleteItemEnabled = true)
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = LightGray)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        stringResource(R.string.cart_total_label),
                        fontSize = 24.sp,
                        color = MainColor,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "$${formatPrice(total.toFloat())}",
                        fontSize = 24.sp,
                        color = MainColor,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.createOrder()
                    },
                    enabled = cartItems.isNotEmpty(),
                ) {
                    Text(
                        fontWeight = FontWeight.SemiBold,
                        text = stringResource(R.string.cart_create_order_label),
                        fontSize = 24.sp,
                        color = BackgroundColor,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

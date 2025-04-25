package com.g18.ccp.ui.product

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.g18.ccp.R
import com.g18.ccp.core.utils.format.formatPrice
import com.g18.ccp.core.utils.network.Output
import com.g18.ccp.data.local.model.cart.CartItem
import com.g18.ccp.presentation.order.create.ListProductViewModel
import com.g18.ccp.ui.theme.BackgroundColor
import com.g18.ccp.ui.theme.LightGray
import com.g18.ccp.ui.theme.MainColor

@Composable
fun ProductListScreen(viewModel: ListProductViewModel, onCartClick: () -> Unit) {
    val state by viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.loadProducts()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        when (val result = state) {
            is Output.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MainColor)
                }
            }

            is Output.Success -> {
                val products = result.data
                LazyColumn {
                    items(products) { product ->
                        ProductItem(product, viewModel)
                    }
                }
            }

            is Output.Failure<*> -> {
                Text(
                    stringResource(R.string.list_products_default_error_message),
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductItem(cartItem: CartItem, viewModel: ListProductViewModel, isDeleteItemEnabled: Boolean = false) {
    val quantity = viewModel.getQuantity(cartItem)

    Card(
        colors = CardDefaults.cardColors(containerColor = LightGray),
        modifier = Modifier
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                modifier = Modifier
                    .width(120.dp)
                    .height(150.dp),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(cartItem.product.imageUrl)
                    .crossfade(true)
                    .build(),
                contentScale = ContentScale.Crop,
                contentDescription = cartItem.product.name,
                placeholder = painterResource(R.drawable.ic_launcher_foreground),
                error = painterResource(R.drawable.ic_launcher_background),
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        cartItem.product.name,
                        fontSize = 16.sp,
                        color = MainColor,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "$ ${formatPrice(cartItem.product.price)}",
                        modifier = Modifier.padding(top = 8.dp),
                        fontSize = 14.sp,
                        color = MainColor,
                        lineHeight = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(BackgroundColor, shape = RoundedCornerShape(12.dp))
                        .fillMaxWidth()
                        .height(49.dp)
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(onClick = { viewModel.addProduct(cartItem) }) {
                        Icon(
                            Icons.Outlined.AddCircleOutline,
                            contentDescription = null,
                            tint = MainColor
                        )
                    }
                    if (quantity == 0) {
                        Text(
                            stringResource(R.string.add_products_cart),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = MainColor
                        )
                    } else {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "$quantity und",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = MainColor
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = { viewModel.removeProduct(cartItem) }) {
                            Icon(
                                Icons.Outlined.RemoveCircleOutline,
                                contentDescription = null,
                                tint = MainColor
                            )
                        }
                    }
                }

                if (isDeleteItemEnabled) {
                    Text(
                        stringResource(R.string.delete_product),
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(top = 8.dp)
                            .clickable { viewModel.removeAllProduct(cartItem) },
                        fontSize = 14.sp,
                        color = Color.Red
                    )
                }
            }
        }
    }
}

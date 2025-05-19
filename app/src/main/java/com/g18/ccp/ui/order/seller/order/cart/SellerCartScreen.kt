package com.g18.ccp.ui.order.seller.order.cart

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.g18.ccp.R
import com.g18.ccp.data.local.model.cart.seller.SellerCartItem
import com.g18.ccp.presentation.seller.order.category.products.cart.SellerCartViewModel
import com.g18.ccp.ui.theme.BackgroundColor
import com.g18.ccp.ui.theme.BlackColor
import com.g18.ccp.ui.theme.ErrorColor
import com.g18.ccp.ui.theme.LightGray
import com.g18.ccp.ui.theme.MainColor
import com.g18.ccp.ui.theme.WhiteColor
import kotlinx.coroutines.launch

@Composable
fun SellerCartScreen(
    viewModel: SellerCartViewModel,
    onOrderConfirmed: () -> Unit
) {
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    // Calcula total
    val total = cartItems.sumOf { it.quantity * it.product.price.toDouble() }
    val formattedTotal = String.format("$%.2f", total)

    val lifecycle = LocalLifecycleOwner.current.lifecycle

    DisposableEffect(lifecycle) {
        val obs = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                viewModel.loadCartItems()
            }
        }
        lifecycle.addObserver(obs)
        onDispose { lifecycle.removeObserver(obs) }
    }

    LaunchedEffect(viewModel.confirmResult) {
        viewModel.confirmResult.collect { result ->
            if (result.isSuccess) {
                // show a Snackbar or navigate away:
                onOrderConfirmed()
            } else {
                // show error message
                val msg = result.exceptionOrNull()?.message ?: "Unknown error"
                // e.g. scaffoldState.snackbarHostState.showSnackbar(msg)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(16.dp)
    ) {
        if (cartItems.isEmpty()) {
            Text(
                text = stringResource(R.string.cart_empty),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(cartItems) { item ->
                    CartItemCard(
                        cartItem = item,
                        onIncrement = {
                            coroutineScope.launch {
                                viewModel.updateCartItem(item.product.id, item.quantity + 1)
                            }
                        },
                        onDecrement = {
                            coroutineScope.launch {
                                viewModel.updateCartItem(item.product.id, item.quantity - 1)
                            }
                        },
                        onRemove = {
                            coroutineScope.launch {
                                viewModel.updateCartItem(item.product.id, 0)
                            }
                        }
                    )
                }
            }

            Text(
                text = stringResource(R.string.total_to_pay, formattedTotal),
                color = BlackColor,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            Button(
                onClick = {
                    coroutineScope.launch {
                        viewModel.confirmOrder()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MainColor)
            ) {
                Text(
                    text = stringResource(R.string.confirm_order_button),
                    color = WhiteColor,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun CartItemCard(
    cartItem: SellerCartItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = rememberAsyncImagePainter(cartItem.product.imageUrl),
                    contentDescription = cartItem.product.name,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(LightGray.copy(alpha = 0.3f)),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = "$${"%,.0f".format(cartItem.product.price).replace(',', '.')}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MainColor
                    )
                    Text(
                        text = cartItem.product.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = BlackColor,
                        maxLines = 2
                    )
                }
                IconButton(onClick = onRemove) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = stringResource(R.string.remove_item),
                        tint = ErrorColor
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedIconButton(
                    onClick = onDecrement,
                    shape = CircleShape,
                    border = BorderStroke(1.dp, BlackColor)
                ) {
                    Icon(
                        Icons.Default.Remove,
                        contentDescription = stringResource(R.string.remove_item),
                        modifier = Modifier.size(18.dp),
                        tint = BlackColor
                    )
                }
                Text(
                    text = cartItem.quantity.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = BlackColor,
                    modifier = Modifier.widthIn(min = 24.dp)
                )
                OutlinedIconButton(
                    onClick = onIncrement,
                    shape = CircleShape,
                    border = BorderStroke(1.dp, MainColor)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_to_cart_button),
                        modifier = Modifier.size(18.dp),
                        tint = MainColor
                    )
                }
            }
        }
    }
}

package com.g18.ccp.ui.order.seller.listproducts

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
import com.g18.ccp.data.remote.model.seller.order.product.SellerProductData
import com.g18.ccp.presentation.seller.order.category.products.SellerCategoryProductsUiState
import com.g18.ccp.presentation.seller.order.category.products.SellerCategoryProductsViewModel
import com.g18.ccp.ui.theme.BackgroundColor
import com.g18.ccp.ui.theme.BlackColor
import com.g18.ccp.ui.theme.ErrorColor
import com.g18.ccp.ui.theme.LightBeige
import com.g18.ccp.ui.theme.LightGray
import com.g18.ccp.ui.theme.MainColor
import com.g18.ccp.ui.theme.WhiteColor
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerCategoryProductsScreen(
    modifier: Modifier = Modifier,
    viewModel: SellerCategoryProductsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle(initialValue = emptyList())

    val lifecycle = LocalLifecycleOwner.current.lifecycle

    DisposableEffect(lifecycle) {
        val obs = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                viewModel.observeCartItems()
                viewModel.loadAndObserveProducts()
                viewModel.startPolling()
                Log.d("SellerCategoryProductsScreen", "Polling started")
            }
            if (event == Lifecycle.Event.ON_STOP) {
                viewModel.stopPolling()
                Log.d("SellerCategoryProductsScreen", "Polling stopped")
            }
        }
        lifecycle.addObserver(obs)
        onDispose { lifecycle.removeObserver(obs) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.onSearchQueryChanged(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text(stringResource(R.string.search_products_placeholder)) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = MainColor) },
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = BlackColor),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = WhiteColor,
                unfocusedContainerColor = LightGray,
                cursorColor = MainColor
            )
        )

        when (val state = uiState) {
            is SellerCategoryProductsUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MainColor)
                }
            }

            is SellerCategoryProductsUiState.Error -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.message, color = ErrorColor, textAlign = TextAlign.Center)
                }
            }

            is SellerCategoryProductsUiState.Success -> {
                val allProducts = state.products
                val filtered = if (searchQuery.isBlank()) allProducts
                else allProducts.filter { it.name.contains(searchQuery, ignoreCase = true) }

                if (filtered.isEmpty()) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (searchQuery.isNotBlank()) stringResource(R.string.search_no_results)
                            else stringResource(R.string.no_products_in_category),
                            textAlign = TextAlign.Center,
                            color = BlackColor.copy(alpha = 0.7f)
                        )
                    }
                } else {
                    LazyColumn(
                        Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filtered) { product ->
                            // Determine quantity in cart
                            val inCart =
                                cartItems.find { it.product.id == product.id }?.quantity ?: 0
                            ProductItemCard(
                                product = product,
                                cartQuantity = inCart,
                                onQuantityIncrement = {
                                    viewModel.updateCartItem(
                                        product.id,
                                        inCart + 1
                                    )
                                },
                                onQuantityDecrement = {
                                    viewModel.updateCartItem(
                                        product.id,
                                        maxOf(0, inCart - 1)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductItemCard(
    product: SellerProductData,
    cartQuantity: Int,
    onQuantityIncrement: () -> Unit,
    onQuantityDecrement: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = LightBeige),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = product.imageUrl,
                    placeholder = painterResource(id = R.drawable.ic_account_icon),
                    error = painterResource(id = R.drawable.ic_account_icon)
                ),
                contentDescription = product.name,
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(LightGray.copy(alpha = 0.3f)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = "$ ${"%,.0f".format(product.price).replace(",", ".")}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 24.sp,
                    color = MainColor,
                    lineHeight = 24.sp
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = BlackColor,
                    maxLines = 2,
                    lineHeight = 20.sp
                )
            }
            Spacer(Modifier.width(8.dp))
            // Quantity controls reflect cart contents
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedIconButton(
                    onClick = onQuantityDecrement,
                    modifier = Modifier.size(32.dp),
                    shape = CircleShape,
                    border = BorderStroke(1.dp, BlackColor),
                    colors = IconButtonDefaults.outlinedIconButtonColors(
                        contentColor = BlackColor
                    )
                ) {
                    Icon(
                        Icons.Default.Remove,
                        contentDescription = "-",
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = "$cartQuantity",
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .widthIn(min = 24.dp),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MainColor,
                )
                OutlinedIconButton(
                    onClick = onQuantityIncrement,
                    modifier = Modifier.size(32.dp),
                    shape = CircleShape,
                    border = BorderStroke(1.dp, MainColor),
                    colors = IconButtonDefaults.outlinedIconButtonColors(contentColor = MainColor)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "+",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

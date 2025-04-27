package com.g18.ccp.ui.seller.customer

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.g18.ccp.R
import com.g18.ccp.core.constants.SELLER_CUSTOMER_MANAGEMENT_BASE_ROUTE
import com.g18.ccp.data.remote.model.seller.CustomerData
import com.g18.ccp.presentation.seller.customerslist.CustomerListUiState
import com.g18.ccp.presentation.seller.customerslist.SellerCustomersViewModel
import com.g18.ccp.ui.theme.BackgroundColor
import com.g18.ccp.ui.theme.BlackColor
import com.g18.ccp.ui.theme.ErrorColor
import com.g18.ccp.ui.theme.LightBeige
import com.g18.ccp.ui.theme.LightGray
import com.g18.ccp.ui.theme.MainColor

@Composable
fun SellerCustomerListScreen(
    modifier: Modifier = Modifier,
    viewModel: SellerCustomersViewModel,
    navController: NavHostController
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val filteredCustomers by viewModel.filteredCustomers.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.onSearchQueryChanged(it) },
            placeholder = {
                Text(
                    stringResource(R.string.search_placeholder),
                    color = BlackColor.copy(alpha = 0.6f),
                    style = TextStyle(
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize
                    )
                )
            },
            textStyle = TextStyle(
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                color = BlackColor
            ),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(R.string.search_placeholder),
                    tint = BlackColor.copy(alpha = 0.8f),
                    modifier = Modifier.size(24.dp)
                )
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = LightGray,
                unfocusedContainerColor = LightGray,
                disabledContainerColor = LightGray,
                focusedBorderColor = BlackColor.copy(alpha = 0.5f),
                unfocusedBorderColor = LightGray,
                focusedTextColor = BlackColor,
                unfocusedTextColor = MainColor,
            )
        )

        when (val state = uiState) {
            is CustomerListUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MainColor)
                }
            }

            is CustomerListUiState.Success -> {
                if (filteredCustomers.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        val messageResId = if (searchQuery.isBlank()) {
                            R.string.no_customers_found
                        } else {
                            R.string.search_no_results
                        }
                        Text(
                            stringResource(messageResId),
                            color = MainColor,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(filteredCustomers, key = { it.id }) { customer ->
                            CustomerCard(
                                customer = customer,
                                onClick = {
                                    navController.navigate(
                                        "$SELLER_CUSTOMER_MANAGEMENT_BASE_ROUTE/${customer.id}"
                                    )
                                })
                        }
                    }
                }
            }

            is CustomerListUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.error_loading_customers, state.message),
                        color = ErrorColor,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun CustomerCard(
    customer: CustomerData,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = LightBeige),
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                    tint = BlackColor,
                    modifier = Modifier.size(50.dp)
                )
                Spacer(modifier = Modifier.width(32.dp))
                Column(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .align(Alignment.CenterVertically)
                ) {
                    Text(
                        text = customer.name,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = BlackColor,
                        lineHeight = 16.sp
                    )
                    Text(
                        text = customer.address,
                        fontSize = 14.sp,
                        color = BlackColor.copy(alpha = 0.8f),
                        lineHeight = 16.sp
                    )
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
                contentDescription = stringResource(R.string.view_customer_details),
                tint = MainColor
            )
        }
    }
}

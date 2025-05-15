package com.g18.ccp.ui.seller.customer.customervisit.list

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.g18.ccp.R
import com.g18.ccp.presentation.seller.customervisit.list.SellerCustomerVisitsViewModel
import com.g18.ccp.presentation.seller.customervisit.list.VisitDisplayItem
import com.g18.ccp.presentation.seller.customervisit.list.VisitsScreenUiState
import com.g18.ccp.ui.theme.BackgroundColor
import com.g18.ccp.ui.theme.BlackColor
import com.g18.ccp.ui.theme.ButtonBackgroundColor
import com.g18.ccp.ui.theme.ErrorColor
import com.g18.ccp.ui.theme.LightGray
import com.g18.ccp.ui.theme.MainColor
import com.g18.ccp.ui.theme.WhiteColor
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun SellerCustomerVisitsScreen(
    modifier: Modifier = Modifier,
    viewModel: SellerCustomerVisitsViewModel = koinViewModel(),
    onRegisterVisitClick: () -> Unit,
    // navController: NavHostController // Para el título y back arrow
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val lifecycle = LocalLifecycleOwner.current.lifecycle

    DisposableEffect(lifecycle) {
        val obs = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                viewModel.loadInitialData()
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
        when (val state = uiState) {
            is VisitsScreenUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MainColor)
                }
            }

            is VisitsScreenUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.message, color = ErrorColor)
                }
            }

            is VisitsScreenUiState.Success -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    CustomerHeader(customerName = state.customerName)
                    Button(
                        onClick = onRegisterVisitClick,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(vertical = 16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MainColor),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(stringResource(R.string.register_visit_button), color = WhiteColor)
                    }

                    if (state.visits.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                stringResource(R.string.no_visits_yet),
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            items(state.visits, key = { it.id }) { visit ->
                                VisitItem(
                                    visitData = visit,
                                    onDeleteClick = { viewModel.deleteVisit(visit.id) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomerHeader(customerName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(WhiteColor)
            .padding(horizontal = 32.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.Person,
            contentDescription = null,
            tint = MainColor,
            modifier = Modifier.size(50.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = customerName,
            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
            lineHeight = MaterialTheme.typography.titleLarge.fontSize,
            color = BlackColor
        )
    }
    HorizontalDivider(color = LightGray)
}

@Composable
private fun VisitItem(visitData: VisitDisplayItem, onDeleteClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = ButtonBackgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.CheckCircleOutline,
                contentDescription = "Visit completed",
                tint = MainColor,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    try {
                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        sdf.format(visitData.registerDate)
                    } catch (e: Exception) {
                        visitData.registerDate.toString()
                    },
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    lineHeight = MaterialTheme.typography.bodyLarge.fontSize,
                    color = BlackColor
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    visitData.sellerName,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    lineHeight = MaterialTheme.typography.bodyMedium.fontSize,
                    color = BlackColor.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    visitData.sellerEmail,
                    fontSize = MaterialTheme.typography.bodySmall.fontSize,
                    lineHeight = MaterialTheme.typography.bodySmall.fontSize,
                    color = BlackColor.copy(alpha = 0.8f)
                )
            }
            IconButton(onClick = onDeleteClick) {
                Icon(
                    modifier = Modifier.size(32.dp),
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete_visit_content_description),
                    tint = ErrorColor.copy(alpha = 0.7f)
                )
            }
        }
    }
}

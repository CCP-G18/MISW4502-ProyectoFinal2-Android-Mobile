package com.g18.ccp.ui.seller.customer.personalinfo

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.g18.ccp.R
import com.g18.ccp.core.constants.enums.getDisplayName
import com.g18.ccp.data.remote.model.seller.CustomerData
import com.g18.ccp.presentation.seller.personalinfo.CustomerInfoUiState
import com.g18.ccp.presentation.seller.personalinfo.SellerCustomerPersonalInfoViewModel
import com.g18.ccp.ui.theme.BackgroundColor
import com.g18.ccp.ui.theme.BlackColor
import com.g18.ccp.ui.theme.ButtonBackgroundColor
import com.g18.ccp.ui.theme.ErrorColor
import com.g18.ccp.ui.theme.MainColor
import org.koin.androidx.compose.koinViewModel

@Composable
fun SellerCustomerPersonalInfoScreen(
    modifier: Modifier = Modifier,
    viewModel: SellerCustomerPersonalInfoViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is CustomerInfoUiState.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MainColor)
            }
        }

        is CustomerInfoUiState.Success -> {
            PersonalInfoContent(
                modifier = modifier,
                customerData = state.customer
            )
        }

        is CustomerInfoUiState.Error -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Error: ${state.message}", color = ErrorColor)
            }
        }

        is CustomerInfoUiState.NotFound -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.customer_not_found))
            }
        }
    }
}

@Composable
private fun PersonalInfoContent(
    modifier: Modifier = Modifier,
    customerData: CustomerData
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .verticalScroll(scrollState)
            .padding(bottom = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
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
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = BlackColor
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = ButtonBackgroundColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InfoItem(
                    label = stringResource(R.string.personal_info_id_type),
                    value = customerData.identificationType.getDisplayName(LocalContext.current)
                )
                InfoItem(
                    label = stringResource(R.string.personal_info_id_number),
                    value = customerData.identificationNumber
                )
                InfoItem(
                    label = stringResource(R.string.personal_info_email),
                    value = customerData.email
                )
                InfoItem(
                    label = stringResource(R.string.personal_info_address),
                    value = customerData.address
                )
                InfoItem(
                    label = stringResource(R.string.personal_info_city),
                    value = customerData.city
                )
                InfoItem(
                    label = stringResource(R.string.personal_info_neighborhood),
                    value = customerData.city
                )

            }
        }
    }
}

@Composable
private fun InfoItem(label: String, value: String) {
    Column(modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)) {
        Text(
            text = label,
            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            lineHeight = MaterialTheme.typography.bodyMedium.fontSize,
            fontWeight = FontWeight.Normal,
            color = BlackColor.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            lineHeight = MaterialTheme.typography.bodyMedium.fontSize,
            fontWeight = FontWeight.Medium,
            color = BlackColor
        )
    }
}

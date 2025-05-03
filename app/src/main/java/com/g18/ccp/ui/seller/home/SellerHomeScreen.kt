package com.g18.ccp.ui.seller.home

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.g18.ccp.R
import com.g18.ccp.presentation.seller.home.SellerHomeViewModel
import com.g18.ccp.ui.theme.LightGray
import com.g18.ccp.ui.theme.MainColor

@Composable
fun SellerHomeScreen(
    onCustomersClick: () -> Unit,
    onRoutesClick: () -> Unit,
    viewModel: SellerHomeViewModel
) {
    val userName by viewModel.userName.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Avatar
        Icon(
            painter = painterResource(id = R.drawable.ic_account_icon),
            contentDescription = "Profile Picture",
            tint = MainColor,
            modifier = Modifier
                .size(100.dp)
                .padding(bottom = 12.dp)
        )

        Text(
            text = userName.orEmpty(),
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            color = MainColor
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clickable { onRoutesClick() },
            colors = CardDefaults.cardColors(containerColor = LightGray),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Map,
                    contentDescription = "Ruta de visitas",
                    tint = MainColor
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(R.string.seller_visits_route),
                    fontSize = 16.sp,
                    color = MainColor
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clickable { onCustomersClick() },
            colors = CardDefaults.cardColors(containerColor = LightGray),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Groups,
                    contentDescription = "Clientes",
                    tint = MainColor
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(R.string.customers_text),
                    fontSize = 16.sp,
                    color = MainColor
                )
            }
        }
    }
}

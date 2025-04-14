package com.g18.ccp.ui.order.status.detail

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.g18.ccp.data.remote.model.order.Order
import com.g18.ccp.ui.theme.BackgroundColor
import com.g18.ccp.ui.theme.LightGray
import com.g18.ccp.ui.theme.MainColor
import com.g18.ccp.ui.theme.WhiteColor

@Composable
fun OrderDetailScreen(order: Order) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WhiteColor)
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(order.items) { item ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = LightGray),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(item.imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = item.title,
                            placeholder = painterResource(R.drawable.ic_launcher_foreground),
                            error = painterResource(R.drawable.ic_launcher_background),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .width(113.dp)
                                .height(160.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                item.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MainColor
                            )
                            Text("${item.quantity} Und", fontSize = 16.sp, color = MainColor)
                            Row {
                                Text(
                                    "Total",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MainColor
                                )
                                Text("$ ${item.price}", fontSize = 16.sp, color = MainColor)
                            }
                        }
                    }
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = BackgroundColor),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 48.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Total:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MainColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "$${order.total}",
                        fontSize = 20.sp,
                        color = MainColor,
                        textAlign = TextAlign.End
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 32.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Status",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MainColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(order.status.labelRes),
                            fontSize = 14.sp,
                            color = MainColor
                        )
                        Icon(
                            order.status.icon,
                            contentDescription = order.status.name,
                            tint = order.status.color
                        )
                    }
                }
            }
        }
    }
}

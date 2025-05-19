package com.g18.ccp.data.remote.model.seller.order.product

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StockUpdateResponse(
    val type: String,
    val products: List<StockUpdate>
) : Parcelable

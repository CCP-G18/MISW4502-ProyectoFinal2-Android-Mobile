package com.g18.ccp.presentation.seller.customervisit.list

import java.util.Date

data class VisitDisplayItem(
    val id: String,
    val registerDate: Date,
    val displayDate: String,
    val observations: String?,
    val customerId: String,
    val sellerId: String,
    val sellerName: String,
    val sellerEmail: String
)

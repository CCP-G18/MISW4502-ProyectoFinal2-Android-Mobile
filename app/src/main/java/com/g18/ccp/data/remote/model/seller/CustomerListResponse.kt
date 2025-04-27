package com.g18.ccp.data.remote.model.seller

data class CustomerListResponse(
    val code: Int,
    val data: List<CustomerData>,
    val message: String,
    val status: String
)

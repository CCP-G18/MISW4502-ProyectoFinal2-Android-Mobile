package com.g18.ccp.core.constants

const val HOME_ROUTE = "home"
const val WELCOME_ROUTE = "welcome"
const val ORDERS_ROUTE = "orders"
const val CART_ROUTE = "cart"
const val DELIVERIES_ROUTE = "deliveries"
const val LOGIN_ROUTE = "login"
const val REGISTER_ROUTE = "register"
const val ORDER_DETAIL_ROUTE = "orderDetail"
const val SPLASH_CONGRATS_ROUTE = "splashCongratsOrder"
const val SELLER_HOME_ROUTE = "sellerHome"
const val SELLER_CUSTOMERS_ROUTE = "seller/customers"
const val CUSTOMER_ID_ARG = "customerId"
const val SELLER_CUSTOMER_MANAGEMENT_BASE_ROUTE = "seller/customers/management"
const val SELLER_CUSTOMER_MANAGEMENT_ROUTE =
    "$SELLER_CUSTOMER_MANAGEMENT_BASE_ROUTE/{$CUSTOMER_ID_ARG}"
const val SELLER_CUSTOMER_PERSONAL_INFO_BASE_ROUTE = "seller_customer_personal_info"
const val SELLER_CUSTOMER_PERSONAL_INFO_ROUTE =
    "$SELLER_CUSTOMER_PERSONAL_INFO_BASE_ROUTE/{$CUSTOMER_ID_ARG}"

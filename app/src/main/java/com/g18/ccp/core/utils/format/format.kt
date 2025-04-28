package com.g18.ccp.core.utils.format

import java.text.NumberFormat
import java.util.Locale

fun formatPrice(value: Float): String {
    val formatter = NumberFormat.getNumberInstance(Locale("es", "CO"))
    return formatter.format(value)
}

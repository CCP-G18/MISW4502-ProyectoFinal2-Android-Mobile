package com.g18.ccp.core.constants.enums

import android.content.Context
import com.g18.ccp.R

enum class IdentificationType {
    CC,
    NIT,
    CE,
    DNI,
    PASSPORT
}

fun IdentificationType.getDisplayName(context: Context): String {
    return when (this) {
        IdentificationType.CC -> context.getString(R.string.register_id_type_cc)
        IdentificationType.NIT -> context.getString(R.string.register_id_type_nit)
        IdentificationType.CE -> context.getString(R.string.register_id_type_ce)
        IdentificationType.DNI -> context.getString(R.string.register_id_type_dni)
        IdentificationType.PASSPORT -> context.getString(R.string.register_id_type_passport)
    }
}

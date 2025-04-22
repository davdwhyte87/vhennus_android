package com.vhennus.general.utils

import java.math.BigDecimal
import java.math.BigInteger
import java.text.NumberFormat
import java.util.Locale

fun formatBigDecimalWithCommas(value: BigDecimal): String {
    val formatter = NumberFormat.getNumberInstance(Locale.US)
    return formatter.format(value)
}
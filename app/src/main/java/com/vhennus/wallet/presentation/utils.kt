package com.vhennus.wallet.presentation

import com.vhennus.general.domain.SystemData
import com.vhennus.general.utils.formatBigDecimalWithCommas
import java.math.BigDecimal


fun calculateConvertedBalance(amount:String,currency: String, systemData: SystemData): BigDecimal{
    var data: BigDecimal = BigDecimal.ZERO
    try {
       val amount = BigDecimal(amount)
        when (currency){
            "NGN"-> {
                data = (amount*systemData.price)*systemData.ngn
            }
            "USD" -> {
                data = amount*systemData.price
            }
        }
    }catch(e: Exception){
       return BigDecimal.ZERO
    }

    return data
}
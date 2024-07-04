package com.amorgens.wallet.domain

data class Transaction(
    val id:String,
    val receiverAddress:String,
    val amount: String,
    val dateTime:String,

)
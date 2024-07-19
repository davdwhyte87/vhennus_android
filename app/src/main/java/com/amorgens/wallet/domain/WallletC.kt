package com.amorgens.wallet.domain

import java.math.BigDecimal

data class WalletC(
    val id: String,
    val address: String,
    val wallet_name: String,
    val password_hash:String,
    val created_at: String,
    val vallic_key: String,
    val is_private: Boolean,
    val chain:Chain
)

data class Chain(
    val chain:List<Block>
)
data class  Block(
    val id:String,
    val transaction_id:String,
    val sender_address:String,
    val receiver_address:String,
    val date_created:String,
    val hash:String,
    val prev_hash:String,
    val amount: BigDecimal,
    val vallic_key: String,
    val balance:BigDecimal,
    val trx_h:String?
)
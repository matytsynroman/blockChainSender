package com.roman.dto

data class SegwitBitcoinTransactionRequestDTO(
    val amount: Long,
    val fee: Long,
    val sourceAddress: String,
    val destinationAddress: String,
    var utxo: List<UTXO>
)
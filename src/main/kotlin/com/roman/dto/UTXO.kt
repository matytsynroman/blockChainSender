package com.roman.dto

data class UTXO(
    val outputIndex: Long,
    val prevHash: String,
    val outputValue: Long
)
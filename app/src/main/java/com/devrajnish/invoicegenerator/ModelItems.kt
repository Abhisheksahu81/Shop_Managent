package com.devrajnish.invoicegenerator

data class ModelItems(
    val itemName: String,
    val itemDesc: String,
    var quantity: Int,
    var disAmount : Int,
    val vat: Int,
    var netAmount: Int
) {
}
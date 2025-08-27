package com.example.nearstore.Data

data class OrderData(
    val storeName: String = "",
    val storeLocation: String = "",
    val itemtotal: Int = 0,
    val deliveryfee: Int = 0,
    val grandtotal: Int = 0,
    val orderid: Int = 0,
    val ordertime: String = "",
    val orderstatus: String = "ongoing",
    val useraddress: String = "",
    val timing: String = "Arriving in 35 min",
    val deliveryagentname: String = "",
    val deliveryagentphone: Int = 999999999,
    val itemsList: List<Product> = emptyList(),
    val timestamp: Long,
    val storeimage: String
)

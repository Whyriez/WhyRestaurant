package com.example.whyrestaurant.mod

data class OrderDetail(
    val quantity: Int,
    val subTotal: Int,
    val menu: Menu
)

package com.example.whyrestaurant.mod

data class Order(
    val orderId: String,
    val status: String,
    val createdAt: String,
    val orderDetails: List<OrderDetail>
)

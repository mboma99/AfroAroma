package com.example.afroaroma.model

import java.util.Date

data class Order (val orderId: String,
                  val userId: String,
                  val drinksList: List<Drink>,
                  val orderDate: Date,
                  val orderStatus: String?) {
    fun calculateTotalCost(): Double {
        return drinksList.sumByDouble { it.quantity?.times(it.calculatePrice()!!) ?: 0.00 }
    }
}
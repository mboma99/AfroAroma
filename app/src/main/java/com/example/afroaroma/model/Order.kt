package com.example.afroaroma.model

import java.util.Date
import android.os.Parcel
import android.os.Parcelable

data class Order(
    val orderId: String,
    val userId: String,
    val drinksList: List<Drink>,
    val orderDate: Date,
    val orderStatus: String?
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        mutableListOf<Drink>().apply {
            parcel.readList(this, Drink::class.java.classLoader)
        },
        Date(parcel.readLong()),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(orderId)
        parcel.writeString(userId)
        parcel.writeList(drinksList)
        parcel.writeLong(orderDate.time)
        parcel.writeString(orderStatus)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun calculateTotalCost(): Double {
        return drinksList.sumByDouble { it.quantity?.times(it.calculatePrice() ?: 0.0) ?: 0.0 }
    }

    companion object CREATOR : Parcelable.Creator<Order> {
        override fun createFromParcel(parcel: Parcel): Order {
            return Order(parcel)
        }

        override fun newArray(size: Int): Array<Order?> {
            return arrayOfNulls(size)
        }
    }
}

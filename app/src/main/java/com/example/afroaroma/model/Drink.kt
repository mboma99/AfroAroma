package com.example.afroaroma.model

import android.os.Parcel
import android.os.Parcelable

data class Drink(
    var drinkId: String,
    var name: String,
    var drinkDescription: String? = null,
    var price: Double?,
    var quantity: Long? = 5L,
    var imageUrl: String? = null ): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?:"",
        parcel.readDouble(),
        parcel.readLong(),
        parcel.readString() ?:""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(drinkId)
        parcel.writeString(name)
        parcel.writeString(drinkDescription)
        price?.let { parcel.writeDouble(it) }
        quantity?.let { parcel.writeLong(it) }
        parcel.writeString(imageUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun calculatePrice(): Double? {
        return price
    }

    companion object CREATOR : Parcelable.Creator<Drink> {
        override fun createFromParcel(parcel: Parcel): Drink {
            return Drink(parcel)
        }

        override fun newArray(size: Int): Array<Drink?> {
            return arrayOfNulls(size)
        }
    }
}




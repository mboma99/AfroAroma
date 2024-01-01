package com.example.afroaroma.model

import android.os.Parcel
import android.os.Parcelable
import java.util.Date

data class Feedback(
    val feedbackId: String,
    val feedback: String,
    val feedbackHeadline: String,
    val orderId: String,
    val rating: Int,
    val readStatus: Boolean
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt()?: 0,
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(feedbackId)
        parcel.writeString(feedback)
        parcel.writeString(feedbackHeadline)
        parcel.writeString(orderId)
        parcel.writeInt(rating)
        parcel.writeByte(if (readStatus) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Feedback> {
        override fun createFromParcel(parcel: Parcel): Feedback {
            return Feedback(parcel)
        }

        override fun newArray(size: Int): Array<Feedback?> {
            return arrayOfNulls(size)
        }
    }
}
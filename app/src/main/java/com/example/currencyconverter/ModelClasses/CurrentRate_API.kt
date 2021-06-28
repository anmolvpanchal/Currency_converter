package com.example.currencyconverter.ModelClasses

import android.os.Parcel
import android.os.Parcelable

class CurrentRate_API(val base_currency: String, val quote_currency: String, val quote: Float, val date: String) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readFloat(),
        parcel.readString().toString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(base_currency)
        parcel.writeString(quote_currency)
        parcel.writeFloat(quote)
        parcel.writeString(date)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CurrentRate_API> {
        override fun createFromParcel(parcel: Parcel): CurrentRate_API {
            return CurrentRate_API(parcel)
        }

        override fun newArray(size: Int): Array<CurrentRate_API?> {
            return arrayOfNulls(size)
        }
    }

}

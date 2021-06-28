package com.example.currencyconverter.ModelClasses

import android.os.Parcel
import android.os.Parcelable

class TotalSelectedCurrenciesList (val code : String, val enteredAmmount : String) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(code)
        parcel.writeString(enteredAmmount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TotalSelectedCurrenciesList> {
        override fun createFromParcel(parcel: Parcel): TotalSelectedCurrenciesList {
            return TotalSelectedCurrenciesList(parcel)
        }

        override fun newArray(size: Int): Array<TotalSelectedCurrenciesList?> {
            return arrayOfNulls(size)
        }
    }

}
package com.example.currencyconverter.ModelClasses

import android.os.Parcel
import android.os.Parcelable

class CurrentCountries_API(val code : String, val name : String) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(code)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CurrentCountries_API> {
        override fun createFromParcel(parcel: Parcel): CurrentCountries_API {
            return CurrentCountries_API(parcel)
        }

        override fun newArray(size: Int): Array<CurrentCountries_API?> {
            return arrayOfNulls(size)
        }
    }

}

package com.vvvlad42.amusetime.data

import android.os.Parcel
import android.os.Parcelable

class LocationParcel constructor(var lat: Double, var lng: Double):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readDouble(),
        parcel.readDouble()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(lat)
        parcel.writeDouble(lng)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LocationParcel> {
        override fun createFromParcel(parcel: Parcel): LocationParcel {
            return LocationParcel(parcel)
        }

        override fun newArray(size: Int): Array<LocationParcel?> {
            return arrayOfNulls(size)
        }
    }
}
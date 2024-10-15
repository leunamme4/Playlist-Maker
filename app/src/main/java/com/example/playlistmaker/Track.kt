package com.example.playlistmaker

import android.os.Parcel
import android.os.Parcelable
import java.text.SimpleDateFormat
import java.util.Locale

class Track (val trackName: String, val artistName: String, var trackTimeMillis: String, val artworkUrl100: String): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(trackName)
        parcel.writeString(artistName)
        parcel.writeString(trackTimeMillis)
        parcel.writeString(artworkUrl100)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Track> {
        override fun createFromParcel(parcel: Parcel): Track {
            return Track(parcel)
        }

        override fun newArray(size: Int): Array<Track?> {
            return arrayOfNulls(size)
        }
    }
}
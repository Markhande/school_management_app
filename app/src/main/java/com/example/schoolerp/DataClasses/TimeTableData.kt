package com.example.schoolerp.DataClasses

import android.os.Parcel
import android.os.Parcelable

data class TimeTableData(
    val id: String,
    val classes: String,
    val picture: String,
    val school_id: String,
    val created_at: String,
    val class_name: String?
// Nullable
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(classes)
        parcel.writeString(picture)
        parcel.writeString(school_id)
        parcel.writeString(class_name)
        parcel.writeString(created_at)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<TimeTableData> {
        override fun createFromParcel(parcel: Parcel): TimeTableData {
            return TimeTableData(parcel)
        }

        override fun newArray(size: Int): Array<TimeTableData?> {
            return arrayOfNulls(size)
        }
    }
}

package com.example.schoolerp.DataClasses

import android.os.Parcel
import android.os.Parcelable

data class GetMessageData(
    val id: String,
    val recipient_type: String,
    val search_specific: String,
    val message: String,
    val attachment: String?,
    val school_id: String,
    val class_name: String,
    val role: String,
    val created_at: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readString(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(recipient_type)
        parcel.writeString(search_specific)
        parcel.writeString(message)
        parcel.writeString(attachment)
        parcel.writeString(school_id)
        parcel.writeString(created_at)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GetMessageData> {
        override fun createFromParcel(parcel: Parcel): GetMessageData {
            return GetMessageData(parcel)
        }

        override fun newArray(size: Int): Array<GetMessageData?> {
            return arrayOfNulls(size)
        }
    }
}

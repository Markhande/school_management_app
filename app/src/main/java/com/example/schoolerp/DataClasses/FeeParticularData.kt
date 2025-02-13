package com.example.schoolerp.DataClasses

import android.os.Parcel
import android.os.Parcelable

data class FeeParticularData(
    val id: String,
    val classes: String,
    val student_id: String,
    val search_specific: String,
    val particular_label: List<String>,
    val prefix_amount: List<String>,
    val previous_deposit_amount: String,
    val discount_fee: String,
    val row_total_amount: String,
    val due_balance: String?,
    val deposite_amount: String?,
    val school_id: String,
    val created_at: String,
    val updated_at: String,
    val registration_no: String,
    val class_name: String,
    val st_name: String) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.createStringArrayList()!!,
        parcel.createStringArrayList()!!,
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),


        parcel.readString().toString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(classes)
        parcel.writeString(search_specific)
        parcel.writeStringList(particular_label)
        parcel.writeStringList(prefix_amount)
        parcel.writeString(school_id)
        parcel.writeString(created_at)
        parcel.writeString(updated_at)
        parcel.writeString(st_name)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FeeParticularData> {
        override fun createFromParcel(parcel: Parcel): FeeParticularData {
            return FeeParticularData(parcel)
        }

        override fun newArray(size: Int): Array<FeeParticularData?> {
            return arrayOfNulls(size)
        }
    }
}

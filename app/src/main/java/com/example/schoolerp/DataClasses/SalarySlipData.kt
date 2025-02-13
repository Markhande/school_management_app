package com.example.schoolerp.DataClasses

import android.os.Parcel
import android.os.Parcelable

class SalarySlipData(
    val id: String?,
    val employee_id: String?,
    val employee_name: String?,
    val employee_role: String?,
    val f_h_name: String?,
    val salary_month: String?,
    val salary_amount: String?,
    val date_of_pay: String?,
    val bouns: String?,
    val deduction: String?,
    val picture: String?,
    val net_paid: String?,
    val school_id: String?,
    val created_at: String?,
    val updated_at: String?,
    val school_name: String?,
    val address: String?,
    val institute_logo: String?

    ) : Parcelable {

    // Constructor for creating object from Parcel
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    // Method to write the object data into the Parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(employee_id)
        parcel.writeString(employee_name)
        parcel.writeString(employee_role)
        parcel.writeString(f_h_name)
        parcel.writeString(salary_month)
        parcel.writeString(salary_amount)
        parcel.writeString(date_of_pay)
        parcel.writeString(bouns)
        parcel.writeString(deduction)
        parcel.writeString(net_paid)
        parcel.writeString(school_id)
        parcel.writeString(address)
        parcel.writeString(created_at)
        parcel.writeString(updated_at)
    }

    // Required for Parcelable, returns a bitmask for object serialization
    override fun describeContents(): Int {
        return 0
    }

    // Companion object to recreate the object from Parcel
    companion object CREATOR : Parcelable.Creator<SalarySlipData> {
        override fun createFromParcel(parcel: Parcel): SalarySlipData {
            return SalarySlipData(parcel)
        }

        override fun newArray(size: Int): Array<SalarySlipData?> {
            return arrayOfNulls(size)
        }
    }
}

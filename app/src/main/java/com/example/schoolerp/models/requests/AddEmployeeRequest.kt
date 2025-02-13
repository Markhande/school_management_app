package com.example.schoolerp.models.requests

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class AddEmployeeRequest(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("employee_name")
    val employeeName: String,

    @SerializedName("mobile")
    val mobile: String,

    @SerializedName("employee_role")
    val employeeRole: String,

    @SerializedName("picture")
    val picture: String,

    @SerializedName("date_of_joining")
    val dateOfJoining: String,

    @SerializedName("monthly_salary")
    val monthlySalary: Int,

    @SerializedName("f_h_name")
    val fHName: String,

    /*
    @SerializedName("national_id")
    val nationalId: String,
    */

    @SerializedName("education")
    val education: String,

    @SerializedName("gender")
    val gender: String,

    @SerializedName("religion")
    val religion: String,

    @SerializedName("blood_group")
    val bloodGroup: String,

    @SerializedName("experience")
    val experience: String,

    @SerializedName("date_of_birth")
    val dateOfBirth: String,

    @SerializedName("home_address")
    val homeAddress: String,

    @SerializedName("school_id")
    val schoolId: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readInt(),
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
        parcel.writeValue(id)
        parcel.writeString(employeeName)
        parcel.writeString(mobile)
        parcel.writeString(employeeRole)
        parcel.writeString(picture)
        parcel.writeString(dateOfJoining)
        parcel.writeInt(monthlySalary)
        parcel.writeString(fHName)
        parcel.writeString(education)
        parcel.writeString(gender)
        parcel.writeString(religion)
        parcel.writeString(bloodGroup)
        parcel.writeString(experience)
        parcel.writeString(dateOfBirth)
        parcel.writeString(homeAddress)
        parcel.writeString(schoolId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AddEmployeeRequest> {
        override fun createFromParcel(parcel: Parcel): AddEmployeeRequest {
            return AddEmployeeRequest(parcel)
        }

        override fun newArray(size: Int): Array<AddEmployeeRequest?> {
            return arrayOfNulls(size)
        }
    }
}

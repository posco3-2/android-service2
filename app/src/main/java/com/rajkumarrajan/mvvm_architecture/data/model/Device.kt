package com.rajkumarrajan.mvvm_architecture.data.model

import com.google.android.gms.tasks.Task
import com.google.gson.annotations.SerializedName
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class Device(

    @SerializedName("id")
    var id: Int?=null,
    @SerializedName("deviceId")
    val deviceId: String? = null,
    @SerializedName("phoneNumber")
    val phoneNumber: String? = null,
    @SerializedName("socialToken")
    val socialToken: String? = null,
    @SerializedName("userId")
    val userId: Int? =null,
    @SerializedName("userName")
    val userName: String? = null,
    @SerializedName("fcmActive")
    val fcmActive: Int? =null,
    @SerializedName("deviceOs")
    val deviceOs: Char? = null,
    @SerializedName("deviceModel")
    val deviceModel: String? = null,
    @SerializedName("fcmToken")
    val fcmToken: String? = null,
    @SerializedName("regDate")
    val regDate: String? = null,
    @SerializedName("updateDate")
    val updateDate: String? = null,
    @SerializedName("deviceOsType")
    val deviceOsType: Char? = null,
    @SerializedName("updateFcmActive")
    val updateFcmActive: Int? =null,
    @SerializedName("carrier")
    val carrier: String? = null,


    )
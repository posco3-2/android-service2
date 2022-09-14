package com.posco.posco_store.data.model

import com.google.gson.annotations.SerializedName

data class LoginDto (

    @SerializedName("userId")
    val userId: String? = null,

    @SerializedName("password")
    val password: String? = null,

    @SerializedName("type")
    val type: String? = null,

    @SerializedName("deviceId")
    val deviceId: String? = null,

    @SerializedName("phoneNumber")
    val phoneNumber: String? = null,

    @SerializedName("deviceModel")
    val deviceModel: String? = null,

    @SerializedName("deviceOs")
    val deviceOs: String? = null,

    @SerializedName("deviceOsType")
    val deviceOsType: String? = null,

    @SerializedName("fcmToken")
    val fcmToken: String? = null,

    @SerializedName("carrier")
    val carrier: String? = null,



)
package com.rajkumarrajan.mvvm_architecture.data.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class Device(

        @SerializedName("id")
        var id: Int?=null ,

        @SerializedName("device_id")
        val device_id: String? = null,
        @SerializedName("phone_number")
        val phone_number: String? = null,
        @SerializedName("social_token")
        val social_token: String? = null,
        @SerializedName("user_id")
        val user_id: Int? =null,
        @SerializedName("user_name")
        val user_name: String? = null,
        @SerializedName("fcm_active")
        val fcm_active: Int? =null,
        @SerializedName("device_os")
        val device_os: Char? = null,
        @SerializedName("device_model")
        val device_model: String? = null,
        @SerializedName("fcm_token")
        val fcm_token: String? = null,
        @SerializedName("reg_date")
        val reg_date: Date? = null,
        @SerializedName("update_date")
        val update_date: Date? = null,
        @SerializedName("device_os_type")
        val device_os_type: Char? = null,
        @SerializedName("update_fcm_active")
        val update_fcm_active: Int? =null,
        @SerializedName("carrier")
        val carrier: String? = null,


        )
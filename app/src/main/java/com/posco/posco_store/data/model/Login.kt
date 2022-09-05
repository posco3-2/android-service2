package com.posco.posco_store.data.model

import com.google.gson.annotations.SerializedName

data class Login (
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("userId")
    val userId: String? = null,

    @SerializedName("password")
    val password: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("deviceId")
    val deviceId: String? = null
)
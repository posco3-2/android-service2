package com.posco.posco_store.data.model

import com.google.gson.annotations.SerializedName

data class LoginResultDto (
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("userId")
    val userId: String? = null,


    @SerializedName("name")
    val name: String? = null,

    @SerializedName("deviceId")
    val deviceId: String? = null,

    @SerializedName("token")
    val token: String? = null

)
package com.posco.posco_store.data.model

import com.google.gson.annotations.SerializedName


data class User(

    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("userId")
    val userId: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("ranked")
    val ranked: String? = null,

    @SerializedName("company")
    val company: String? = null,

    @SerializedName("team")
    val team: String? = null,

    @SerializedName("tester")
    val tester: Int? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("password")
    val password: String? = null,

    @SerializedName("phoneNumber")
    val phoneNumber: String? = null
)
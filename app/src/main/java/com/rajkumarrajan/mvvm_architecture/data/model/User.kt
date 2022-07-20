package com.rajkumarrajan.mvvm_architecture.data.model

import com.google.gson.annotations.SerializedName

data class User(

    @SerializedName("id")
    val id: Int,

    @SerializedName("user_Id")
    val user_id: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("rank")
    val rank: String? = null,

    @SerializedName("company")
    val company: String? = null,

    @SerializedName("team")
    val team: String? = null,

    @SerializedName("tester")
    val tester: Int? = 0

)
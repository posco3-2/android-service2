package com.rajkumarrajan.mvvm_architecture.data.model

import com.google.gson.annotations.SerializedName

data class FileInfoDto (
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("seq")
    val seq: Int? = null,
    @SerializedName("originalName")
    val originalName: String? = null,
    @SerializedName("changedName")
    val changedName: String? = null,
    @SerializedName("location")
    val location : String? = null
    )
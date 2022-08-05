package com.posco.posco_store.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

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
    ) : Serializable
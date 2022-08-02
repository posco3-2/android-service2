package com.posco.posco_store.data.model

import com.google.gson.annotations.SerializedName

data class App (
    @SerializedName("id")
    var id: String,
    @SerializedName("appName")
    val appName: String? = null,
    @SerializedName("iconFile")
    val iconFile: Int? = null,
    @SerializedName("version")
    val version: String? = null,
    @SerializedName("os")
    val os: String? = null,
    @SerializedName("iconFileInfo")
    val iconFileInfoDto: FileInfoDto? =null

    )
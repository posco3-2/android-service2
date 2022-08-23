package com.posco.posco_store.data.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class App (

    val id: String,

    val appName: String? = null,

    val admin: String? = null,

    val desc: String? = null,

    val updateDesc: String? = null,

    val updateDate: String? = null,

    val iconFile: Int? = null,

    val installFile: Int? = null,

    val detailFile: Int? =null,

    val version: String? = null,

    val os: String? = null,

    val iconFileInfo: FileInfoDto? =null,

    val installFileInfo: FileInfoDto?= null,

    val detailFilesInfo: List<FileInfoDto>? = null,

    val packageName : String?= null,

    var scheme: String? = null,

    ): Serializable

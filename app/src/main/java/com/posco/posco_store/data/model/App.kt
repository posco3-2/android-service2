package com.posco.posco_store.data.model


import java.io.Serializable


data class App (

    val id: String,

    val appName: String? = null,

    val admin: String? = null,

    val desc: String? = null,

    val updateDesc: String? = null,

    val updateDate: String? = null,

    val iconFile: Int? = null,

    val version: String? = null,

    val os: String? = null,

    val iconFileInfoDto: FileInfoDto? =null

    ): Serializable


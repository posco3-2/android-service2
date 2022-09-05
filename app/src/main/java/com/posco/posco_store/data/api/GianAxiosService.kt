package com.posco.posco_store.data.api

import com.posco.posco_store.di.LogModule
import com.posco.posco_store.ui.main.view.*

import dagger.Component

@Component(modules = [LogModule::class] )
interface GianAxiosService {
    fun inject(mainActivity: MainActivity)
    fun inject(loginActivity: LoginActivity)
    fun inject(detailActivity: DetailActivity)
    fun inject(myPageActivity: MyPageActivity)
    fun inject(downloadActivity: DownloadActivity)

}
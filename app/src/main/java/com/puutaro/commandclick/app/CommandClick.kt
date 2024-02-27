package com.puutaro.commandclick.app

import android.app.Application
import android.content.Context


class CommandClick: Application() {

    private val self: CommandClick = this
    private var sContext: Context? = null


    override fun onCreate() {
        super.onCreate()
        sContext = this
    }

    fun getContext(): Context? {
        return sContext
    }
}
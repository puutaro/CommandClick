package com.puutaro.commandclick.service.lib

import android.content.IntentFilter

object IntentFilterCreator {
    fun create(
        action: String,
    ): IntentFilter{
        val intentFilter = IntentFilter()
        intentFilter.addAction(
            action
        )
        return intentFilter
    }
}
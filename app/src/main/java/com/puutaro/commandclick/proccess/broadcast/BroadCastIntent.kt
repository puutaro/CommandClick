package com.puutaro.commandclick.proccess.broadcast

import android.content.Context
import android.content.Intent
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeTerm

object BroadCastIntent {
    fun sendUrlCon(
        context: Context?,
        urlCon: String?
    ) {
        if(
            urlCon.isNullOrEmpty()
        ) return
        val jsIntent = Intent()
        jsIntent.action = BroadCastIntentSchemeTerm.ULR_LAUNCH.action
        jsIntent.putExtra(
            BroadCastIntentSchemeTerm.ULR_LAUNCH.scheme,
            urlCon
        )
        context?.sendBroadcast(jsIntent)
    }
}
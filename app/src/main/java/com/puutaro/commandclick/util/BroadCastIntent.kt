package com.puutaro.commandclick.util

import android.content.Intent
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeTerm

object BroadCastIntent {
    fun sendUrlCon(
        fragment: Fragment,
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
        fragment.activity?.sendBroadcast(jsIntent)
    }
}
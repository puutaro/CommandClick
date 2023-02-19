package com.puutaro.commandclick.util

import android.content.Intent
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.BroadCastIntentScheme

class BroadCastIntent {
    companion object {
        fun send(
            fragment: Fragment,
            urlCon: String?
        ) {
            if(
                urlCon.isNullOrEmpty()
            ) return
            val jsIntent = Intent()
            jsIntent.action = BroadCastIntentScheme.ULR_LAUNCH.action
            jsIntent.putExtra(
                BroadCastIntentScheme.ULR_LAUNCH.scheme,
                urlCon
            )
            fragment.activity?.sendBroadcast(jsIntent)
        }
    }
}
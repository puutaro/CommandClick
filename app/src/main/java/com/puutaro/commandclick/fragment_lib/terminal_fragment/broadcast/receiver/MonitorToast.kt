package com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver

import android.content.Intent
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.common.variable.intent.scheme.BroadCastIntentSchemeTerm

object MonitorToast {

    fun launch(
        intent: Intent,
    ){
        val message = intent.getStringExtra(
            BroadCastIntentSchemeTerm.MONITOR_TOAST.scheme
        )
        if(
            message.isNullOrEmpty()
        ) return
        ToastUtils.showShort(message)
    }
}
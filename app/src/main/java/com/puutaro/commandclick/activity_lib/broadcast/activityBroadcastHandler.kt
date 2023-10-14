package com.puutaro.commandclick.activity_lib.broadcast

import android.content.Intent
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.intent.BroadCastIntentScheme

object activityBroadcastHandler {
    fun handle(
        activity: MainActivity,
        intent: Intent
    ){
        val action = intent.action
        when(action){
            BroadCastIntentScheme.RESTART_UBUNTU_SERVICE_FROM_ACTIVITY.action -> {

            }
        }
    }
}
package com.puutaro.commandclick.activity_lib.init

import android.app.ActivityManager
import android.content.Context
import com.puutaro.commandclick.activity.MainActivity

object ActivityFinisher {
    fun finish(
        activity: MainActivity
    ){
        activity.finish()
        val mngr = activity.getSystemService(
            Context.ACTIVITY_SERVICE
        ) as? ActivityManager
        mngr?.appTasks?.forEach {
            it.finishAndRemoveTask()
        }
    }
}
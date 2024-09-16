package com.puutaro.commandclick.activity_lib.event.lib.common

import android.content.Intent
import com.puutaro.commandclick.activity.MainActivity

object ExecSimpleRestartActivity {
    fun execSimpleRestartActivity(
        activity: MainActivity,
    ){
        val execIntent = Intent(activity, activity::class.java)
        execIntent.setAction(Intent.ACTION_MAIN)
            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        activity.finish()
        activity.startActivity(execIntent)
    }
}
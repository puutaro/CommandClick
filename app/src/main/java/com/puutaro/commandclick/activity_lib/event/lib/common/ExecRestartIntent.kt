package com.puutaro.commandclick.activity_lib.event.lib.common

import android.content.Intent
import com.puutaro.commandclick.activity.MainActivity

object ExecRestartIntent {
    fun execRestartIntent(
        activity: MainActivity
    ) {
        val execIntent = Intent(
            activity, activity::class.java
        )
        execIntent.setAction(Intent.ACTION_VIEW)
            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        activity.startActivity(execIntent)
        activity.finish()
    }
}
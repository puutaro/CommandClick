package com.puutaro.commandclick.service.lib.ubuntu.libs

import android.content.Context
import android.content.Intent
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.settings.FannelInfoSetting

object IntentManager {
    fun launchFannelIntent(
        context: Context?,
//        fannelDirPath: String,
        fannelName: String,
    ){
        val execIntent = Intent(context, MainActivity::class.java)
        execIntent
            .setAction(Intent.ACTION_MAIN)
            .flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
//        execIntent.putExtra(
//            FannelInfoSetting.current_app_dir.name,
//            fannelDirPath
//        )
//        execIntent.putExtra(
//            FannelInfoSetting.current_fannel_name.name,
//            fannelName
//        )
        context?.startActivity(execIntent)
    }
}
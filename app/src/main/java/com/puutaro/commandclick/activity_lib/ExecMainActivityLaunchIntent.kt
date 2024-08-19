package com.puutaro.commandclick.activity_lib

import android.content.Intent
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.settings.FannelInfoSetting
import com.puutaro.commandclick.util.state.FannelInfoTool

object ExecMainActivityLaunchIntent {
    fun launch(
        activity: MainActivity,
    ){
        val execIntent = Intent(activity, activity::class.java)
        execIntent.action = Intent.ACTION_MAIN
        execIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        val startUpPref = FannelInfoTool.getSharePref(activity)
        val currentAppDirPath = FannelInfoTool.getStringFromFannelInfo(
            startUpPref,
            FannelInfoSetting.current_app_dir
        )
        FannelInfoTool.putAllFannelInfo(
            startUpPref,
            currentAppDirPath = currentAppDirPath,
            currentFannelName = FannelInfoSetting.current_fannel_name.defalutStr,
            onShortcutValue = FannelInfoSetting.on_shortcut.defalutStr,
            currentFannelState = FannelInfoSetting.current_fannel_state.defalutStr
        )
        activity.finish()
        activity.startActivity(execIntent)
    }
}
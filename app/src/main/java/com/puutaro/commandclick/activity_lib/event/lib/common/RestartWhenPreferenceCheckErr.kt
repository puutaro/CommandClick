package com.puutaro.commandclick.activity_lib.event.lib.common

import android.content.Context
import android.content.Intent
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.settings.FannelInfoSetting
import com.puutaro.commandclick.util.state.FannelInfoTool

class RestartWhenPreferenceCheckErr {
    companion object {
        fun restartWhenPreferenceCheckErr(
            activity: MainActivity?
        ){
            val sharePref =  activity?.getPreferences(Context.MODE_PRIVATE)
            if(sharePref == null) return
            FannelInfoTool.putAllFannelInfo(
                sharePref,
                FannelInfoSetting.current_app_dir.defalutStr,
                FannelInfoSetting.current_fannel_name.defalutStr,
                FannelInfoSetting.on_shortcut.defalutStr,
                FannelInfoSetting.current_fannel_state.defalutStr
            )
            val execIntent = Intent(activity, activity::class.java)
            execIntent.setAction(Intent.ACTION_MAIN)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            activity.finish()
            activity.startActivity(execIntent)
        }
    }
}
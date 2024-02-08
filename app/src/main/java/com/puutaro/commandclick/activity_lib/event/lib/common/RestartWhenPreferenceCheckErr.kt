package com.puutaro.commandclick.activity_lib.event.lib.common

import android.content.Context
import android.content.Intent
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.util.state.SharePreferenceMethod

class RestartWhenPreferenceCheckErr {
    companion object {
        fun restartWhenPreferenceCheckErr(
            activity: MainActivity?
        ){
            val sharePref =  activity?.getPreferences(Context.MODE_PRIVATE)
            if(sharePref == null) return
            SharePreferenceMethod.putAllSharePreference(
                sharePref,
                SharePrefferenceSetting.current_app_dir.defalutStr,
                SharePrefferenceSetting.current_fannel_name.defalutStr,
                SharePrefferenceSetting.on_shortcut.defalutStr,
                SharePrefferenceSetting.current_fannel_state.defalutStr
            )
            val execIntent = Intent(activity, activity::class.java)
            execIntent.setAction(Intent.ACTION_MAIN)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            activity.finish()
            activity.startActivity(execIntent)
        }
    }
}
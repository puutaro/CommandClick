package com.puutaro.commandclick.activity_lib.event.lib.common

import android.content.Context
import android.content.Intent
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.util.SharePreffrenceMethod

class RestartWhenPreferenceCheckErr {
    companion object {
        fun restartWhenPreferenceCheckErr(
            activity: MainActivity?
        ){
            val sharePref =  activity?.getPreferences(Context.MODE_PRIVATE)
            if(sharePref == null) return
            SharePreffrenceMethod.putSharePreffrence(
                sharePref,
                mapOf(
                    SharePrefferenceSetting.current_app_dir.name
                            to SharePrefferenceSetting.current_app_dir.defalutStr,
                    SharePrefferenceSetting.current_shell_file_name.name
                            to CommandClickShellScript.EMPTY_STRING,
                    SharePrefferenceSetting.on_shortcut.name
                            to SharePrefferenceSetting.on_shortcut.defalutStr
                )
            )
            val execIntent = Intent(activity, activity::class.java)
            execIntent.setAction(Intent.ACTION_MAIN)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            activity.startActivity(execIntent)
            activity.finish()
        }
    }
}
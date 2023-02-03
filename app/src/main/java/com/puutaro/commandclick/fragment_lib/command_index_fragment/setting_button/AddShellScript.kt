package com.puutaro.commandclick.fragment_lib.command_index_fragment.setting_button

import android.content.SharedPreferences
import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.util.Editor
import com.puutaro.commandclick.util.SharePreffrenceMethod


class AddShellScript {
    companion object {
        fun addShellScript (
            cmdIndexFragment: CommandIndexFragment,
            sharedPref: SharedPreferences?,
            currentAppDirPath: String,
            shellScriptName: String,
        ){

            val context = cmdIndexFragment.context
            SharePreffrenceMethod.putSharePreffrence(
                sharedPref,
                mapOf(
                    SharePrefferenceSetting.current_shell_file_name.name
                            to shellScriptName,
                )
            )

            val cmdClickShibanStr = cmdIndexFragment.shiban
            CommandClickShellScript.makeShellFile(
                cmdClickShibanStr,
                currentAppDirPath,
                shellScriptName
            )
            val editor = Editor(
                currentAppDirPath,
                shellScriptName,
                context
            )
            editor.open()
        }
    }
}
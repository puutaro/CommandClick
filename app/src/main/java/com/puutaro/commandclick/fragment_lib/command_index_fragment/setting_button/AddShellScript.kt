package com.puutaro.commandclick.fragment_lib.command_index_fragment.setting_button

import android.content.SharedPreferences
import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.common.variable.LanguageTypeSelects
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.util.Editor
import com.puutaro.commandclick.util.SharePreffrenceMethod


class AddShellScript {
    companion object {
        fun addShellOrJavaScript (
            cmdIndexFragment: CommandIndexFragment,
            sharedPref: SharedPreferences?,
            currentAppDirPath: String,
            shellScriptName: String,
            languageTypeSelects: LanguageTypeSelects
        ){

            val context = cmdIndexFragment.context
            SharePreffrenceMethod.putSharePreffrence(
                sharedPref,
                mapOf(
                    SharePrefferenceSetting.current_script_file_name.name
                            to shellScriptName,
                )
            )

            val cmdClickShibanStr = cmdIndexFragment.shiban
            CommandClickShellScript.makeShellOrJsFile(
                cmdClickShibanStr,
                currentAppDirPath,
                shellScriptName,
                shellOrJs = languageTypeSelects
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
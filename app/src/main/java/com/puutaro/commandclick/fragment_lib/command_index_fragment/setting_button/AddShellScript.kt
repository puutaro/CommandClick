package com.puutaro.commandclick.fragment_lib.command_index_fragment.setting_button

import android.content.SharedPreferences
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.LanguageTypeSelects
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.util.Editor
import com.puutaro.commandclick.util.SharePreffrenceMethod


class AddShellScript {
    companion object {
        fun addShellOrJavaScript (
            cmdIndexCommandIndexFragment: CommandIndexFragment,
            sharedPref: SharedPreferences?,
            currentAppDirPath: String,
            shellScriptName: String,
            languageTypeSelects: LanguageTypeSelects
        ){

            val context = cmdIndexCommandIndexFragment.context
            SharePreffrenceMethod.putSharePreffrence(
                sharedPref,
                mapOf(
                    SharePrefferenceSetting.current_script_file_name.name
                            to shellScriptName,
                )
            )

            val cmdClickShibanStr = cmdIndexCommandIndexFragment.shiban
            CommandClickScriptVariable.makeShellOrJsFile(
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
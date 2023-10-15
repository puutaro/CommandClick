package com.puutaro.commandclick.fragment_lib.command_index_fragment.setting_button

import android.content.SharedPreferences
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.LanguageTypeSelects
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.util.Editor
import com.puutaro.commandclick.util.SharePreffrenceMethod


object AddShellScript {
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
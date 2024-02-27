package com.puutaro.commandclick.fragment_lib.command_index_fragment.setting_button

import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.LanguageTypeSelects
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.util.editor.EditorByIntent


object AddShellScript {
    fun addShellOrJavaScript (
        cmdIndexFragment: CommandIndexFragment,
        currentAppDirPath: String,
        shellScriptName: String,
        languageTypeSelects: LanguageTypeSelects
    ){

        val context = cmdIndexFragment.context
        CommandClickScriptVariable.makeShellOrJsFile(
            currentAppDirPath,
            shellScriptName,
            shellOrJs = languageTypeSelects
        )
        val editorByIntent = EditorByIntent(
            currentAppDirPath,
            shellScriptName,
            context
        )
        editorByIntent.byIntent()
    }
}
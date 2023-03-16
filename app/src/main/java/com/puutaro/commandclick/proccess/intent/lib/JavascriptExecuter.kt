package com.puutaro.commandclick.proccess.intent.lib

import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.common.variable.WebUrlVariables
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import java.io.File

object JavascriptExecuter {
    fun exec(
        terminalViewModel: TerminalViewModel,
        substituteSettingVariableList: List<String>?,
        onUrlLaunchMacro: String,
    ) {
        if(
            onUrlLaunchMacro
            != SettingVariableSelects.Companion.OnUrlLaunchMacroSelects.OFF.name
        ) return
        if(
            substituteSettingVariableList.isNullOrEmpty()
        ) return
        val execJsOrHtmlPath = CommandClickVariables.substituteCmdClickVariable(
            substituteSettingVariableList,
            CommandClickShellScript.EXEC_JS_OR_HTML_PATH
        ) ?: return
        if(
            execJsOrHtmlPath.endsWith(
                CommandClickShellScript.JS_FILE_SUFFIX
            )
            || execJsOrHtmlPath.endsWith(
                CommandClickShellScript.JSX_FILE_SUFFIX
            )
        ) {
            terminalViewModel.launchUrl = JavaScriptLoadUrl.make(
                execJsOrHtmlPath,
            )
            return
        }
        val enableHtmlSuffix = execJsOrHtmlPath.endsWith(
            CommandClickShellScript.HTML_FILE_SUFFIX
        )
                || execJsOrHtmlPath.endsWith(
            CommandClickShellScript.HTM_FILE_SUFFIX
        )
        val enableHtml =
            execJsOrHtmlPath.startsWith(
                WebUrlVariables.slashPrefix
            ) && enableHtmlSuffix
        if(!enableHtml) return
        val jsOrHtmlFileObj = File(execJsOrHtmlPath)
        if(!jsOrHtmlFileObj.isFile) return
        val currentAppDir = jsOrHtmlFileObj.parent
        if(
            currentAppDir.isNullOrEmpty()
        ) return
        terminalViewModel.launchUrl = "${currentAppDir}/${jsOrHtmlFileObj.name}"
    }
}
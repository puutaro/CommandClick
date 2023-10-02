package com.puutaro.commandclick.proccess.intent.lib

import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.WebUrlVariables
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

object JavascriptExecuter {
    fun exec(
        fragment: Fragment,
        terminalViewModel: TerminalViewModel,
        substituteSettingVariableList: List<String>?,
        onUrlLaunchMacro: String,
    ) {
        val context = fragment.context
        if(
            onUrlLaunchMacro
            != SettingVariableSelects.OnUrlLaunchMacroSelects.OFF.name
        ) return
        if(
            substituteSettingVariableList.isNullOrEmpty()
        ) return
        val execJsOrHtmlPath = CommandClickVariables.substituteCmdClickVariable(
            substituteSettingVariableList,
            CommandClickScriptVariable.EXEC_JS_OR_HTML_PATH
        ) ?: return
        if(
            execJsOrHtmlPath.endsWith(
                UsePath.JS_FILE_SUFFIX
            )
            || execJsOrHtmlPath.endsWith(
                UsePath.JSX_FILE_SUFFIX
            )
        ) {
            ExecJsLoad.jsUrlLaunchHandler(
                fragment,
                JavaScriptLoadUrl.make(
                    context,
                    execJsOrHtmlPath,
                ) ?: String()
            )
//            terminalViewModel.launchUrl = JavaScriptLoadUrl.make(
//                context,
//                execJsOrHtmlPath,
//            )
            return
        }
        val enableHtmlSuffix = execJsOrHtmlPath.endsWith(
            UsePath.HTML_FILE_SUFFIX
        )
                || execJsOrHtmlPath.endsWith(
            UsePath.HTM_FILE_SUFFIX
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
        val tempOnDisplayUpdate = terminalViewModel.onDisplayUpdate
        enableJsLoadInWebView(
            terminalViewModel
        )
        ExecJsLoad.jsUrlLaunchHandler(
            fragment,
            "${currentAppDir}/${jsOrHtmlFileObj.name}"
        )
        cleanUpAfterJsExc(
            terminalViewModel,
            tempOnDisplayUpdate,
        )
//        terminalViewModel.launchUrl = "${currentAppDir}/${jsOrHtmlFileObj.name}"
    }

    fun enableJsLoadInWebView(
        terminalViewModel: TerminalViewModel
    ){
        terminalViewModel.onDisplayUpdate = false
    }

    fun cleanUpAfterJsExc(
        terminalViewModel: TerminalViewModel,
        tempOnDisplayUpdate: Boolean,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            delay(1000)
            terminalViewModel.onDisplayUpdate = tempOnDisplayUpdate
        }
    }
}
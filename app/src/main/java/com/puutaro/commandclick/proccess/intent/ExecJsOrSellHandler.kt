package com.puutaro.commandclick.proccess.intent

import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.LanguageTypeSelects
import com.puutaro.commandclick.util.JsOrShellFromSuffix

object ExecJsOrSellHandler {
    fun handle(
        currentFragment: Fragment,
        recentAppDirPath: String,
        selectedScriptFileName: String,
        scriptContentsListSource: List<String>? = null
    ){
        when(
            JsOrShellFromSuffix.judge(
                selectedScriptFileName
            )
        ){
            LanguageTypeSelects.SHELL_SCRIPT -> {
                ExecTerminalDo.execTerminalDo(
                    currentFragment,
                    recentAppDirPath,
                    selectedScriptFileName,
                    scriptContentsListSource
                )
            }
            else -> {
                ExecJsLoad.execJsLoad(
                    currentFragment,
                    recentAppDirPath,
                    selectedScriptFileName,
                    scriptContentsListSource
                )
            }
        }
    }
}
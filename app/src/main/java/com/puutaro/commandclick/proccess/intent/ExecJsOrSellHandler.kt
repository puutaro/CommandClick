package com.puutaro.commandclick.proccess.intent

import androidx.fragment.app.Fragment
import com.puutaro.commandclick.util.CommandClickVariables

object ExecJsOrSellHandler {
    fun handle(
        currentFragment: Fragment,
//        recentAppDirPath: String,
        selectedScriptFileName: String,
        scriptContentsListSource: List<String>? = null,
        args: String = String()
    ){
//        when(
//            CommandClickVariables.judgeJsOrShellFromSuffix(
//                selectedScriptFileName
//            )
//        ){
//            LanguageTypeSelects.SHELL_SCRIPT -> {
//                ExecShellScript.execShellScript(
//                    currentFragment,
////                    recentAppDirPath,
//                    selectedScriptFileName,
//                    scriptContentsListSource
//                )
//            }
//            else -> {
            ExecJsLoad.execJsLoad(
                currentFragment,
//                    recentAppDirPath,
                selectedScriptFileName,
                scriptContentsListSource,
                args
            )
//            }
//        }
    }
}
package com.puutaro.commandclick.proccess.intent.lib

import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.url.WebUrlVariables
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.edit.lib.SettingFile
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionTool
import com.puutaro.commandclick.proccess.tool_bar_button.JsActionHandler
import com.puutaro.commandclick.proccess.broadcast.BroadCastIntent
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.EnableTerminalWebView
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDateTime

object JavascriptExecuter {
    fun exec(
        fragment: Fragment,
        terminalViewModel: TerminalViewModel,
        substituteSettingVariableList: List<String>?,
        onUrlLaunchMacro: String,
    ) {
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
            jsOrActionHandler(
                fragment,
                execJsOrHtmlPath,
                ReadText(execJsOrHtmlPath).textToList()
            )
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
        jsUrlLaunchHandler(
            fragment,
            "${currentAppDir}/${jsOrHtmlFileObj.name}"
        )
        cleanUpAfterJsExc(
            terminalViewModel,
            tempOnDisplayUpdate,
        )
//        terminalViewModel.launchUrl = "${currentAppDir}/${jsOrHtmlFileObj.name}"
    }

    fun jsOrActionHandler(
        fragment: Fragment,
        execJsPath: String,
        execJsConListSrc: List<String>,
        extraMapCon: Map<String, String>? = null,
        webView: WebView? = null
    ){
//        val validateFragment = validateFragment(
//            fragment,
//        )
        val context = fragment.context
        val execJsConList = execJsConListSrc.ifEmpty {
            ReadText(
                execJsPath
            ).textToList()
        }
        val jsType = JsActionTool.JsActionJudge.judge(
            execJsConList
        )
        val fannelInfoMap = FannelInfoTool.getFannelInfoMap(
            fragment,
            execJsPath
        )
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "jsexecLoad000_.txt").absolutePath,
//            listOf(
//                "execJsPath: ${execJsPath}",
//                "jsContentsListSource: ${execJsConList}",
//                "extraMapCon: ${extraMapCon}",
//                "fannelInfoMap: ${fannelInfoMap}",
//                "jsType: ${jsType.key}",
//            ).joinToString("\n\n")
//        )
        when(jsType){
            JsActionTool.JsActionJudge.JsType.JS_ACTION
            -> execJsAction(
                fragment,
                execJsPath,
                execJsConList,
                fannelInfoMap,
                extraMapCon,
                webView
            )
            JsActionTool.JsActionJudge.JsType.FANNEL_JS_ACTION
            -> {
                val execExcludeJsConList = JsActionTool.ExcludeSettingVariable.exclude(
                    execJsConList
                )
//                FileSystems.writeFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "jsexecLoad000_.txt").absolutePath,
//                    listOf(
//                        "execJsPath: ${execJsPath}",
//                        "jsContentsListSource: ${execJsConList}",
//                        "extraMapCon: ${extraMapCon}",
//                        "fannelInfoMap: ${fannelInfoMap}",
//                        "execExcludeJsConList: ${execExcludeJsConList}",
//                        "jsType: ${jsType.key}",
//                    ).joinToString("\n\n")
//                )
                execJsAction(
                    fragment,
                    execJsPath,
                    execExcludeJsConList,
                    fannelInfoMap,
                    extraMapCon,
                    webView
                )
            }
            JsActionTool.JsActionJudge.JsType.NORMAL_JS -> {
                val execJsCon = JavaScriptLoadUrl.make(
                    context,
                    execJsPath,
                    execJsConList,
                    extraRepValMap = extraMapCon
                ) ?: String()
//                FileSystems.writeFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "js_button.txt").absolutePath,
//                    listOf(
//                        "execJsPath: ${execJsPath}",
//                        "execJsConList: ${execJsConList}",
//                        "execJsCon: ${execJsCon}",
//                        "extraMapCon: ${extraMapCon}"
//                    ).joinToString("\n")
//                )
                val separator = "----------"
                val logSrcCon = CheckTool.LogVisualManager.makeSpanTagHolder(
                    CheckTool.LogVisualManager.logGreenPair,
                    execJsCon.replace(";", ";\n"),
                )
                CheckTool.DebugMapManager.writeDebugCons(
                    debugTopBoardConArg = listOf(
                        "${CheckTool.JsOrActionMark.NORMAL_JS.mark}\n",
                        separator,
                    ).joinToString("\n"),
                    jsConWithDetailTagArg = logSrcCon,
                )
                jsUrlLaunchHandler(
                    fragment,
                    execJsCon,
                    webView
                )
            }
        }
    }

    private fun execJsAction(
        fragment: Fragment,
        execJsPath: String,
        execJsConList: List<String>,
        fannelInfoMap: Map<String, String>,
        extraMapCon: Map<String, String>?,
        webView: WebView?
    ){
        val setReplaceVariableMap =
            SetReplaceVariabler.makeSetReplaceVariableMapFromSubFannel(
                fragment.context,
                execJsPath
            )
        val jsKeyToSubKeyListCon = SettingFile.readFromList(
            execJsConList,
            execJsPath,
            setReplaceVariableMap
        )
        JsActionHandler.handle(
            fragment,
            fannelInfoMap,
            execJsPath,
            setReplaceVariableMap,
            jsKeyToSubKeyListCon,
            extraMapCon,
            webView
        )
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

    fun jsUrlLaunchHandler(
        currentFragment: Fragment,
        launchUrlString: String,
        webView: WebView? = null
    ){
        if(
            launchUrlString.isEmpty()
        ) return
        when (currentFragment) {
            is CommandIndexFragment -> {
                currentFragment.jsExecuteJob?.cancel()
                currentFragment.jsExecuteJob = CoroutineScope(Dispatchers.IO).launch {
                    val onLaunchUrl = EnableTerminalWebView.check(
                        currentFragment,
                        currentFragment.context?.getString(
                            R.string.index_terminal_fragment
                        )
                    )
                    launchUrlByWebView(
                        currentFragment,
                        onLaunchUrl,
                        launchUrlString
                    )
                }
            }
            is EditFragment -> {
                currentFragment.jsExecuteJob?.cancel()
                currentFragment.jsExecuteJob = CoroutineScope(Dispatchers.IO).launch {
                    val onLaunchUrl = EnableTerminalWebView.check(
                        currentFragment,
                        currentFragment.context?.getString(
                            R.string.edit_terminal_fragment
                        )
                    )
                    launchUrlByWebView(
                        currentFragment,
                        onLaunchUrl,
                        launchUrlString
                    )
                }
            }
            is TerminalFragment -> {
                if(webView != null){
                    webView.loadUrl(launchUrlString)
                    return
                }
                BroadCastIntent.sendUrlCon(
                    currentFragment.context,
                    launchUrlString
                )
            }
        }
    }

    private suspend fun launchUrlByWebView(
        currentFragment: Fragment,
        onLaunchUrl: Boolean,
        launchUrlString: String
    ){
        if(!onLaunchUrl) return
        withContext(Dispatchers.Main) {
            when(currentFragment) {
                is CommandIndexFragment -> {
                    val listener =
                        currentFragment.context as? CommandIndexFragment.OnLaunchUrlByWebViewListener
                    listener?.onLaunchUrlByWebView(
                        launchUrlString,
                    )
                }
                is EditFragment -> {
                    val listener = currentFragment.context as? EditFragment.OnLaunchUrlByWebViewForEditListener
                    listener?.onLaunchUrlByWebViewForEdit(
                        launchUrlString
                    )
                }
                else -> {}
            }
        }
    }
}
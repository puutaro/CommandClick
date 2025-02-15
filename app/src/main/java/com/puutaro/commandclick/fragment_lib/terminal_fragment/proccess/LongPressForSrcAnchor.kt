package com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess

import android.content.Context
import android.webkit.ValueCallback
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.libs.long_press.LongPressMenuTool
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.intent.lib.JavascriptExecuter
import com.puutaro.commandclick.util.SettingVariableReader
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.str.QuoteTool
import java.io.File
import java.lang.ref.WeakReference

class LongPressForSrcAnchor(
    private val terminalFragmentRef: WeakReference<TerminalFragment>,
    private val context: Context?,
    private val srcAnchorMenuFilePath: String,
)  {
    private val srcAnchorLongPressMenuFilePathObj = File(srcAnchorMenuFilePath)
    private val srcAnchorLongPressMenuDirPath = srcAnchorLongPressMenuFilePathObj.parent
    private val srcAnchorLongPressMenuFileName = srcAnchorLongPressMenuFilePathObj.name


    fun launch(
        title: String?,
        longPressLinkUrl: String,
        currentUrl: String,
    ) {
        if(
            context == null
        ) return
        if(
            srcAnchorLongPressMenuDirPath.isNullOrEmpty()
        ) return
        if(
            !File(srcAnchorMenuFilePath).isFile
        ) return
        if(
            srcAnchorMenuFilePath.endsWith(UsePath.JS_FILE_SUFFIX)
        ){
            execJsFile(
                srcAnchorMenuFilePath,
                longPressLinkUrl,
                currentUrl,
            )
            return
        }
        val terminalFragment = terminalFragmentRef.get() ?: return
        val longPressScriptList = LongPressMenuTool.makeLongPressScriptList(
            terminalFragment,
            srcAnchorLongPressMenuDirPath,
            srcAnchorLongPressMenuFileName,
        ).joinToString("\n").let {
            val currentValidFannelName =
                ValidFannelNameGetterForTerm.get(
                    terminalFragment
                )
            SetReplaceVariabler.execReplaceByReplaceVariables(
                it,
                terminalFragment.setReplaceVariableMap,
                currentValidFannelName
            )
        }.split("\n")
        val longPressMenuMapList = LongPressMenuTool.LongPressInfoMapList.makeMenuMapList(
            terminalFragment,
            terminalFragment.busyboxExecutor,
            terminalFragment.settingActionAsyncCoroutine,
            terminalFragment.imageActionAsyncCoroutine,
            longPressScriptList,
            LongPressMenuTool.LongPressType.SRC_ANCHOR,
            listOf(longPressLinkUrl)
        )
        val menuList = LongPressMenuTool.LongPressInfoMapList.extractTitleIconOathList(
            longPressMenuMapList
        )
        val titleKey = LongPressMenuTool.LongPressKey.TITLE
        if(
            menuList.size == 1
        ){
            val firstMenuTitle = menuList.first().first
            val jsPath = longPressMenuMapList.firstOrNull {
                it.get(titleKey) == firstMenuTitle
            }?.get(LongPressMenuTool.LongPressKey.JS_PATH) ?: return
            execJsFile(
                jsPath,
                longPressLinkUrl,
                currentUrl,
            )
            return
        }
        val longPressSelectJsScript = LongPressMenuTool.LongPressJsDialogScript.make(
            terminalFragment,
            title,
            menuList.reversed(),
            "srcAnchor",
        )
        val terminalWebView = terminalFragment.binding.terminalWebView
        terminalWebView.evaluateJavascript(
            longPressSelectJsScript,
            ValueCallback<String?> { selectedTitleSrc ->
                val selectedTitle =
                    QuoteTool.trimBothEdgeQuote(selectedTitleSrc)
                if(
                    selectedTitle.isEmpty()
                ) return@ValueCallback
                val selectedJsPath = longPressMenuMapList.firstOrNull {
                    it.get(titleKey) == selectedTitle
                }?.get(LongPressMenuTool.LongPressKey.JS_PATH) ?: return@ValueCallback
                if(
                    !File(selectedJsPath).isFile
                ) return@ValueCallback
                execJsFile(
                    selectedJsPath,
                    longPressLinkUrl,
                    currentUrl,
                )
            })
    }

    private fun execJsFile(
        selectedJsPath: String,
        longPressLinkUrl: String,
        currentUrl: String,
    ){
        val terminalFragment = terminalFragmentRef.get() ?: return
        val selectedScriptNameOrPathObj = File(selectedJsPath)
        val execJsPath = LongPressMenuTool.makeExecJsPath(
            terminalFragment,
            selectedScriptNameOrPathObj,
        )
        val settingValList = LongPressMenuTool.extractSettingValList(
            terminalFragment,
            execJsPath,
        )
        val srcAnchorLongPressJsPath =  SettingVariableReader.getStrValue(
            settingValList,
            CommandClickScriptVariable.SRC_ANCHOR_LONG_PRESS_JS_PATH,
            execJsPath
        )
        val srcAnchorLongPressRepValMap = mapOf(
            CommandClickScriptVariable.CMDCLICK_LONG_PRESS_LINK_URL
                    to longPressLinkUrl,
            CommandClickScriptVariable.CMDCLICK_CURRENT_PAGE_URL
                    to currentUrl
        )
        JavascriptExecuter.jsOrActionHandler(
            terminalFragment,
            srcAnchorLongPressJsPath,
            ReadText(srcAnchorLongPressJsPath).textToList(),
            srcAnchorLongPressRepValMap
        )
    }
}

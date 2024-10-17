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

class LongPressForSrcImageAnchor(
    private val terminalFragmentRef: WeakReference<TerminalFragment>,
    private val context: Context?,
    private val srcImageAnchorMenuFilePath: String,
)  {
    private val srcImageAnchorLongPressMenuFilePathObj = File(srcImageAnchorMenuFilePath)
    private val srcImageAnchorLongPressMenuDirPath = srcImageAnchorLongPressMenuFilePathObj.parent
    private val srcImageAnchorLongPressMenuFileName = srcImageAnchorLongPressMenuFilePathObj.name

    fun launch(
        title: String?,
        longPressLinkUrl: String,
        longPressImageUrl: String,
        currentUrl: String,
    ) {
        if(
            context == null
        ) return
        if(
            srcImageAnchorLongPressMenuDirPath.isNullOrEmpty()
        ) return
        if(
            !File(srcImageAnchorMenuFilePath).isFile
        ) return
        if(
            srcImageAnchorMenuFilePath.endsWith(UsePath.JS_FILE_SUFFIX)
        ){
            execJsFile(
                srcImageAnchorMenuFilePath,
                longPressLinkUrl,
                longPressImageUrl,
                currentUrl,
            )
            return
        }
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val longPressScriptList = LongPressMenuTool.makeLongPressScriptList(
            terminalFragment,
            srcImageAnchorLongPressMenuDirPath,
            srcImageAnchorLongPressMenuFileName,
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
            context,
            longPressScriptList,
            LongPressMenuTool.LongPressType.SRC_IMAGE_ANCHOR,
            listOf(longPressLinkUrl, longPressImageUrl)
        )
        val menuList = LongPressMenuTool.LongPressInfoMapList.extractTitleIconOathList(
            longPressMenuMapList
        )
        val menuListSize = menuList.size
        if(
            menuListSize == 0
        ) return
        val titleKey = LongPressMenuTool.LongPressKey.TITLE
        if(
            menuListSize == 1
        ){
            val firstMenuTitle = menuList.first().first
            val jsPath = longPressMenuMapList.firstOrNull {
                it.get(titleKey) == firstMenuTitle
            }?.get(LongPressMenuTool.LongPressKey.JS_PATH) ?: return
            execJsFile(
                jsPath,
                longPressLinkUrl,
                longPressImageUrl,
                currentUrl,
            )
            return
        }
        val longPressSelectJsScript = LongPressMenuTool.LongPressJsDialogScript.make(
            terminalFragment,
            title,
            menuList.reversed()
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
//                FileSystems.writeFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "longpress.txt").absolutePath,
//                    listOf(
//                        "longPressScriptList: ${longPressScriptList}",
//                        "longPressMenuMapList: ${longPressMenuMapList}",
//                        "selectedTitleSrc: ${selectedTitleSrc}",
//                        "selectedTitle: ${selectedTitle}",
//                        "selectedMap: ${longPressMenuMapList.firstOrNull {
//                            it.get(titleKey) == selectedTitle
//                        }}",
//                        "selectedJsPath: ${longPressMenuMapList.firstOrNull {
//                            it.get(titleKey) == selectedTitle
//                        }?.get(LongPressMenuTool.LongPressKey.JS_PATH)}",
//                    ).joinToString("\n\n")
//                )
                val selectedJsPath = longPressMenuMapList.firstOrNull {
                    it.get(titleKey) == selectedTitle
                }?.get(LongPressMenuTool.LongPressKey.JS_PATH)
                    ?: return@ValueCallback
                if(
                    !File(selectedJsPath).isFile
                ) return@ValueCallback
                execJsFile(
                    selectedJsPath,
                    longPressLinkUrl,
                    longPressImageUrl,
                    currentUrl,
                )
            })
        return
    }

    private fun execJsFile(
        selectedJsPath: String,
        longPressLinkUrl: String,
        longPressImageUrl: String,
        currentUrl: String,
    ){
        val terminalFragment = terminalFragmentRef.get() ?: return
        val selectedScriptNameOrPathObj = File(selectedJsPath)
        val execJsPath = LongPressMenuTool.makeExecJsPath(
            terminalFragment,
//            currentAppDirPath,
            selectedScriptNameOrPathObj,
        )
        val settingValList = LongPressMenuTool.extractSettingValList(
            context,
            execJsPath,
        )
        val srcImageAnchorLongPressJsPath =  SettingVariableReader.getStrValue(
            settingValList,
            CommandClickScriptVariable.SRC_IMAGE_ANCHOR_LONG_PRESS_JS_PATH,
            execJsPath
        )
        val srcImageAnchorLongPressRepValMap = mapOf(
            CommandClickScriptVariable.CMDCLICK_LONG_PRESS_LINK_URL
                    to longPressLinkUrl,
            CommandClickScriptVariable.CMDCLICK_LONG_PRESS_IMAGE_URL
                    to longPressImageUrl,
            CommandClickScriptVariable.CMDCLICK_CURRENT_PAGE_URL
                    to currentUrl,
        )
        JavascriptExecuter.jsOrActionHandler(
            terminalFragment,
            srcImageAnchorLongPressJsPath,
            ReadText(srcImageAnchorLongPressJsPath).textToList(),
            srcImageAnchorLongPressRepValMap
        )
    }
}


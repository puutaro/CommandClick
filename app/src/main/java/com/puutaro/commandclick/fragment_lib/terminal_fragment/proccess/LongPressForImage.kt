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

class LongPressForImage(
    private val terminalFragmentRef: WeakReference<TerminalFragment>,
    private val context: Context?,
    private val imageMenuFilePath: String,
)  {
    private val imageLongPressMenuFilePathObj = File(imageMenuFilePath)
    private val imageLongPressMenuDirPath = imageLongPressMenuFilePathObj.parent
    private val imageLongPressMenuFileName = imageLongPressMenuFilePathObj.name

    fun launch(
        title: String?,
        longPressImageUrl: String,
        currentUrl: String,
    ) {
        if(
            context == null
        ) return
        if(
            imageLongPressMenuDirPath.isNullOrEmpty()
        ) return
        if(
            !File(imageMenuFilePath).isFile
        ) return
        if(
            imageMenuFilePath.endsWith(UsePath.JS_FILE_SUFFIX)
        ){
            execJsFile(
                imageMenuFilePath,
                longPressImageUrl,
                currentUrl,
            )
            return
        }
       val terminalFragment = terminalFragmentRef.get()
           ?: return
       val longPressScriptList = LongPressMenuTool.makeLongPressScriptList(
            terminalFragment,
            imageLongPressMenuDirPath,
            imageLongPressMenuFileName,
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
            LongPressMenuTool.LongPressType.IMAGE,
            listOf(longPressImageUrl)
//            longPressScriptList
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
//            val jsPath = LongPressMenuTool.extractJsPathFromLongPressMenuList(
//                menuList.first().first,
//                longPressScriptList,
//            )?: return
            execJsFile(
                jsPath,
                longPressImageUrl,
                currentUrl,
            )
            return
        }
        val longPressSelectJsScript = LongPressMenuTool.LongPressJsDialogScript.make(
            terminalFragment,
            title,
            menuList.reversed(),
            "image",
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
                    longPressImageUrl,
                    currentUrl,
                )
            })
    }


    private fun execJsFile(
        selectedJsPath: String,
        longPressImageUrl: String,
        currentUrl: String,
    ){
        val terminalFragment = terminalFragmentRef.get()
            ?: return
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
        val imageLongPressJsPath =  SettingVariableReader.getStrValue(
            settingValList,
            CommandClickScriptVariable.IMAGE_LONG_PRESS_JS_PATH,
            execJsPath
        )
        val imageLongPressRepValMap = mapOf(
            CommandClickScriptVariable.CMDCLICK_LONG_PRESS_IMAGE_URL
                to longPressImageUrl,
            CommandClickScriptVariable.CMDCLICK_CURRENT_PAGE_URL
                to currentUrl
        )
        JavascriptExecuter.jsOrActionHandler(
            terminalFragment,
            imageLongPressJsPath,
            ReadText(imageLongPressJsPath).textToList(),
            imageLongPressRepValMap
        )
    }
}

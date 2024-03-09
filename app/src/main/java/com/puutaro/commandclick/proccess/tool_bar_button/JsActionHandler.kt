package com.puutaro.commandclick.proccess.tool_bar_button

import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.proccess.import.CmdVariableReplacer
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionTool
import com.puutaro.commandclick.proccess.tool_bar_button.libs.JsPathHandlerForToolbarButton
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import com.puutaro.commandclick.util.QuoteTool
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.SharePrefTool

object JsActionHandler {
    fun handle(
        fragment: Fragment,
        readSharePreferenceMap: Map<String, String>,
        mainOrSubFannelPath: String,
        jsActionPairListCon: String,
        extraRepValMap: Map<String, String>? = null,
        webView: WebView? = null
    ){
        val jsAcKeyToSubKeyCon = QuoteTool.replaceBySurroundedIgnore(
            jsActionPairListCon,
            ',',
            "\n"
        )
        val setReplaceVariableMapSrc = SharePrefTool.getReplaceVariableMap(
            fragment,
            mainOrSubFannelPath
        )
        //        val jsRepValMapBeforeConcat = CmdVariableReplacer.replace(
//            mainOrSubFannelPath,
//            jsRepValMapBeforeCmdValRepValConcat
//        )
        val setReplaceVariableMap = CmdClickMap.concatRepValMap(
            setReplaceVariableMapSrc,
            extraRepValMap,
        )
//        val jsRepValMapBeforeConcat = CmdVariableReplacer.replace(
//            mainOrSubFannelPath,
//            setReplaceVariableMapSrc
//        )
//        val setReplaceVariableMap = JsActionTool.makeSetRepValMap(
//            fragment,
//            readSharePreferenceMap,
//            extraRepValMap,
//        )
//        val jsRepValMap = concatRepValMap(
//            jsRepValHolderMap,
//            extraRepValMap
//        )
        val jsActionMap = JsActionTool.makeJsActionMap(
            fragment,
            readSharePreferenceMap,
            jsAcKeyToSubKeyCon,
            setReplaceVariableMap,
//            extraRepValMap
        )
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "jsAC.txt").absolutePath,
//            listOf(
//                "jsAcKeyToSubKeyCon: ${jsAcKeyToSubKeyCon}",
//                "mainOrSubFannelPath: ${mainOrSubFannelPath}",
//                "jsActionPairListCon: ${jsActionPairListCon}",
//                "jsActionMap: ${jsActionMap}",
//                "UsePath.execDebugJsPath,: ${UsePath.execDebugJsPath}",
//            ).joinToString("\n\n")
//        )
        if(
            jsActionMap.isNullOrEmpty()
        ) return
        JsPathHandlerForToolbarButton.handle(
            fragment,
            mainOrSubFannelPath,
            null,
            jsActionMap,
            webView
        )
    }
}
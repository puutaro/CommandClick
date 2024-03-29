package com.puutaro.commandclick.proccess.tool_bar_button

import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.import.CmdVariableReplacer
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionTool
import com.puutaro.commandclick.proccess.tool_bar_button.libs.JsPathHandlerForToolbarButton
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import com.puutaro.commandclick.util.QuoteTool
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.SharePrefTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

object JsActionHandler {
    fun handle(
        fragment: Fragment,
        readSharePreferenceMap: Map<String, String>,
        mainOrSubFannelPath: String,
        setReplaceVariableMapSrc: Map<String, String>?,
        jsActionPairListCon: String,
        extraRepValMap: Map<String, String>? = null,
        webView: WebView? = null
    ){
        val jsAcKeyToSubKeyCon = QuoteTool.replaceBySurroundedIgnore(
            jsActionPairListCon,
            ',',
            "\n"
        )
//        val setReplaceVariableMapSrc = SetReplaceVariabler.makeSetReplaceVariableMapFromSubFannel(
//            fragment.context,
//            mainOrSubFannelPath
//        )
//            SharePrefTool.getReplaceVariableMap(
//            fragment,
//            mainOrSubFannelPath
//        )
        val setReplaceVariableMap = CmdClickMap.concatRepValMap(
            setReplaceVariableMapSrc,
            extraRepValMap,
        )
        val jsActionMap = JsActionTool.makeJsActionMap(
            fragment,
            readSharePreferenceMap,
            jsAcKeyToSubKeyCon,
            setReplaceVariableMap,
            mainOrSubFannelPath,
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
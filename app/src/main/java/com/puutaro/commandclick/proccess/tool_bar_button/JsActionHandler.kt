package com.puutaro.commandclick.proccess.tool_bar_button

import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.LogVal
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionTool
import com.puutaro.commandclick.proccess.tool_bar_button.libs.JsPathHandlerForToolbarButton
import com.puutaro.commandclick.util.QuoteTool
import com.puutaro.commandclick.util.file.FileSystems
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
        val setReplaceVariableMap = SharePrefTool.getReplaceVariableMap(
            fragment,
            mainOrSubFannelPath
        )
        val jsActionMap = JsActionTool.makeJsActionMap(
            fragment,
            readSharePreferenceMap,
            jsAcKeyToSubKeyCon,
            setReplaceVariableMap,
            extraRepValMap
        )
        jsActionLog(
            jsAcKeyToSubKeyCon,
            jsActionMap
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

    private fun jsActionLog(
        jsAcKeyToSubKeyCon: String,
        jsActionMap: Map<String, String>?
    ){
        var times = 0
        val preTagHolder = LogVal.preTagHolder
        val jsActionMapLogCon = jsActionMap?.filterKeys {
            it.isNotEmpty()
        }?.map {
            val colorStr =
                LogVal.makeColorCode(times)
            times++
            preTagHolder.format(
                colorStr,
                "${it.key}: ${it.value}",
            )
        }?.joinToString("\n")
        val srcPreTag = preTagHolder.format(
            LogVal.makeColorCode(times),
            "src: ${jsAcKeyToSubKeyCon}"
        )
        FileSystems.writeFile(
            UsePath.jsDebugReportPath,
            listOf(
                "[JsAction]\n",
                jsActionMapLogCon,
                srcPreTag,
            ).joinToString("\n")
        )
    }
}
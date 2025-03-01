package com.puutaro.commandclick.proccess.tool_bar_button

import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionTool
import com.puutaro.commandclick.proccess.tool_bar_button.libs.JsPathHandlerForToolbarButton
import com.puutaro.commandclick.util.str.QuoteTool
import com.puutaro.commandclick.util.map.CmdClickMap

object JsActionHandler {
    fun handle(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
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
        val setReplaceVariableMap = CmdClickMap.concatRepValMap(
            setReplaceVariableMapSrc,
            extraRepValMap,
        )
        val jsActionMap =
            JsActionTool.makeJsActionMap(
                fragment,
                fannelInfoMap,
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
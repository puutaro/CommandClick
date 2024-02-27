package com.puutaro.commandclick.proccess.tool_bar_button

import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionTool
import com.puutaro.commandclick.proccess.tool_bar_button.libs.JsPathHandlerForToolbarButton
import com.puutaro.commandclick.util.QuoteTool
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.state.SharePrefTool
import java.io.File

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
        FileSystems.writeFile(
            File(UsePath.cmdclickDefaultAppDirPath, "jsAC.txt").absolutePath,
            listOf(
                "jsAcKeyToSubKeyCon: ${jsAcKeyToSubKeyCon}",
                "mainOrSubFannelPath: ${mainOrSubFannelPath}",
                "jsActionPairListCon: ${jsActionPairListCon}",
                "jsActionMap: ${jsActionMap}"
            ).joinToString("\n\n")
        )
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
package com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionDataMapKeyObj
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import com.puutaro.commandclick.util.state.SharePrefTool
import com.puutaro.commandclick.util.url.HistoryUrlContents

object AddUrl {

    private enum class AddUrlKey(val key: String){
        URL("url"),
        ON_SEARCH_BTN("onSearchBtn"),
    }

    fun add(
        editFragment: EditFragment,
        jsActionMap: Map<String, String>,
    ){
        val argsMap = JsActionDataMapKeyObj.getJsMacroArgs(
            jsActionMap
        ) ?: emptyMap()

        val urlStringOrMacro = argsMap.get(AddUrlKey.URL.key) ?: String()
        val readSharePreferenceMap = editFragment.readSharePreferenceMap
        val currentAppDirPath = SharePrefTool.getCurrentAppDirPath(
            readSharePreferenceMap
        )
        val urlString = HistoryUrlContents.extract(
            currentAppDirPath,
            urlStringOrMacro
        ) ?: String()
        val onSearchBtn =
            argsMap.get(AddUrlKey.ON_SEARCH_BTN.key)
                ?: String()
        ExecJsLoad.execExternalJs(
            editFragment,
            UsePath.cmdclickSystemAppDirPath,
            UsePath.savePageUrlDialogFannelName,
            listOf(
                urlString,
                onSearchBtn,
            ),
        )
    }
}
package com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionDataMapKeyObj
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import com.puutaro.commandclick.util.state.SharePreferenceMethod
import com.puutaro.commandclick.util.url.HistoryUrlContents

object AddUrl {
    private const val urlExtraKey = "url"
    private const val onSearchBtnKey = "onSearchBtn"


    fun add(
        toolbarButtonArgsMaker: ToolbarButtonArgsMaker,
        jsActionMap: Map<String, String>,
    ){
        val editFragment = toolbarButtonArgsMaker.editFragment
        val argsMap = JsActionDataMapKeyObj.getJsMacroArgs(
            jsActionMap
        ) ?: emptyMap()

        val urlStringOrMacro = argsMap.get(urlExtraKey) ?: String()
        val readSharePreferenceMap = editFragment.readSharePreferenceMap
        val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreferenceMap,
            SharePrefferenceSetting.current_app_dir
        )
        val urlString = HistoryUrlContents.extract(
            currentAppDirPath,
            urlStringOrMacro
        ) ?: String()
        val onSearchBtn = argsMap.get(onSearchBtnKey) ?: String()
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
package com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionDataMapKeyObj
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import com.puutaro.commandclick.util.state.FannelPrefGetter
import com.puutaro.commandclick.util.url.HistoryUrlContents

object AddUrlCon {

    private const val urlExtraKey = "url"
    private const val onSearchBtnKey = "onSearchBtn"
    private const val urlConSaveParentDirPathKey = "urlConSaveParentDirPath"
    private const val compSuffixKey = "compSuffix"


    fun add(
        editFragment: EditFragment,
        jsActionMap: Map<String, String>,
    ){

        val argsMap = JsActionDataMapKeyObj.getJsMacroArgs(
            jsActionMap
        ) ?: emptyMap()
        val urlStringOrMacro = argsMap.get(urlExtraKey)
            ?: String()
        val readSharePreferenceMap = editFragment.readSharePreferenceMap
        val currentAppDirPath = FannelPrefGetter.getCurrentAppDirPath(
            readSharePreferenceMap
        )
        val urlString = HistoryUrlContents.extract(
            currentAppDirPath,
            urlStringOrMacro
        ) ?: String()
        val onSearchBtn =  argsMap.get(onSearchBtnKey) ?: String()
        val urlConSaveParentDirPath = argsMap.get(urlConSaveParentDirPathKey) ?: String()
        val compSuffix = argsMap.get(compSuffixKey) ?: String()
        ExecJsLoad.execExternalJs(
            editFragment,
            UsePath.cmdclickSystemAppDirPath,
            UsePath.saveWebConDialogFannelName,
            listOf(
                urlString,
                onSearchBtn,
                urlConSaveParentDirPath,
                compSuffix
            ),
        )
    }
}
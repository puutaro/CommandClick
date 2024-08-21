package com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar.AddUrlConKey
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionDataMapKeyObj
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import com.puutaro.commandclick.util.url.HistoryUrlContents

object AddUrlCon {

    private val urlExtraKey = AddUrlConKey.URL_STRING_OR_MACRO.key
    private val onSearchBtnKey = AddUrlConKey.ON_SEARCH_BTN.key
    private val urlConSaveParentDirPathKey = AddUrlConKey.URL_CON_SAVE_PARENT_DIR_PATH.key
    private val compSuffixKey = AddUrlConKey.COMP_SUFFIX.key
    private val onSaveUrlHistoryKey = AddUrlConKey.ON_SAVE_URL_HISTORY.key



    fun add(
        editFragment: EditFragment,
        jsActionMap: Map<String, String>,
    ){

        val argsMap = JsActionDataMapKeyObj.getJsMacroArgs(
            jsActionMap
        ) ?: emptyMap()
        val urlStringOrMacro = argsMap.get(urlExtraKey)
            ?: String()
//        val fannelInfoMap = editFragment.fannelInfoMap
//        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//            fannelInfoMap
//        )
        val urlString = HistoryUrlContents.extract(
//            currentAppDirPath,
            urlStringOrMacro
        ) ?: String()
        val onSearchBtn =
            argsMap.get(onSearchBtnKey) ?: "-"
        val urlConSaveParentDirPath =
            argsMap.get(urlConSaveParentDirPathKey) ?: String()
        val compSuffix =
            argsMap.get(compSuffixKey) ?: String()
        val onSaveUrlHistory =
            argsMap.get(onSaveUrlHistoryKey) ?: "-"
        ExecJsLoad.execExternalJs(
            editFragment,
//            UsePath.cmdclickDefaultAppDirPath,
            UsePath.saveWebConDialogFannelName,
            listOf(
                urlString,
                onSearchBtn,
                urlConSaveParentDirPath,
                compSuffix,
                onSaveUrlHistory
            ),
        )
    }
}

package com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionDataMapKeyObj
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.url.HistoryUrlContents

object AddGmailCon {

    private const val urlExtraKey = "url"
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
        val fannelInfoMap = editFragment.fannelInfoMap
        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
            fannelInfoMap
        )
        val urlString = HistoryUrlContents.extract(
            currentAppDirPath,
            urlStringOrMacro
        ) ?: String()
        val urlConSaveParentDirPath = argsMap.get(urlConSaveParentDirPathKey) ?: String()
        val compSuffix = argsMap.get(compSuffixKey) ?: String()
        ExecJsLoad.execExternalJs(
            editFragment,
            UsePath.cmdclickSystemAppDirPath,
            UsePath.saveWebConDialogFannelName,
            listOf(
                urlString,
                urlConSaveParentDirPath,
                compSuffix
            ),
        )
    }
}
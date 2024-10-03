package com.puutaro.commandclick.proccess.qr.qr_dialog_config

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionTool
import com.puutaro.commandclick.proccess.js_macro_libs.macros.JsMacroForQr
import com.puutaro.commandclick.util.state.FannelInfoTool
import java.io.File

object JsActionCompleterForQr {

    fun makeJsActionMap(
        editFragment: EditFragment,
        clickConfigListCon: String?,
        isLongClick: Boolean,
    ): Map<String, String> {
        val fannelInfoMap =
            editFragment.fannelInfoMap
        val mainFannelPath = File(
            UsePath.cmdclickDefaultAppDirPath,
            FannelInfoTool.getCurrentFannelName(fannelInfoMap)
        ).absolutePath
        val jsActionMap = JsActionTool.makeJsActionMap(
            editFragment,
            fannelInfoMap,
            clickConfigListCon,
            editFragment.setReplaceVariableMap,
            mainFannelPath
        )
        val defaultMacroStr = JsMacroForQr.FILE_CONTENTS.name
//        when(isLongClick){
//            true -> JsMacroForQr.EDIT_LOGO.name
//            else -> JsMacroForQr.FILE_CONTENTS.name
//        }
        return JsActionTool.compJsActionMacro(
            jsActionMap,
            defaultMacroStr,
        )
    }
}
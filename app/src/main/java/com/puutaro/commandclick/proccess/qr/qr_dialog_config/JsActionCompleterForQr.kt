package com.puutaro.commandclick.proccess.qr.qr_dialog_config

import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.EditSettingJsTool
import com.puutaro.commandclick.proccess.js_macro_libs.macros.JsMacroForQr

object JsActionCompleterForQr {

    fun makeJsActionMap(
        editFragment: EditFragment,
        clickConfigListCon: String?,
        isLongClick: Boolean,
    ): Map<String, String> {
        val jsActionMap = EditSettingJsTool.makeJsActionMap(
            editFragment,
            clickConfigListCon
        )
        val defaultMacroStr = when(isLongClick){
            true -> JsMacroForQr.EDIT_LOGO.name
            else -> JsMacroForQr.FILE_CONTENTS.name
        }
        return EditSettingJsTool.compJsActionMacro(
            jsActionMap,
            defaultMacroStr,
        )
    }
}
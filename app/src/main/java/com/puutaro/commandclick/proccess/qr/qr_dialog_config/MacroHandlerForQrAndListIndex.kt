package com.puutaro.commandclick.proccess.qr.qr_dialog_config

import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionDataMapKeyObj
import com.puutaro.commandclick.proccess.js_macro_libs.macros.JsPathMacroForListIndex
import com.puutaro.commandclick.proccess.js_macro_libs.exec_handler.ExecMacroHandlerForListIndex
import com.puutaro.commandclick.proccess.js_macro_libs.exec_handler.ExecMacroHandlerForQr
import com.puutaro.commandclick.proccess.js_macro_libs.macros.JsMacroForQr

object MacroHandlerForQrAndListIndex {

    fun handle(
        editFragment: EditFragment,
        jsActionMap: Map<String, String>?,
        clickFileName: String,
        listIndexPosition: Int,
    ){
        if(
            jsActionMap.isNullOrEmpty()
        ) return
        val macroStr =
            jsActionMap.get(
                JsActionDataMapKeyObj.JsActionDataMapKey.JS_CON.key
            )
        val macroForQr = JsMacroForQr.values().firstOrNull {
            it.name == macroStr
        }
        if(
            macroForQr != null
        ){
            ExecMacroHandlerForQr.handle(
                editFragment,
                macroForQr,
                clickFileName,
            )
            return
        }
        val macroForListIndex = JsPathMacroForListIndex.values().firstOrNull {
            it.name == macroStr
        }
        if(
            macroForListIndex != null
        ){
            ExecMacroHandlerForListIndex.handle(
                editFragment,
                jsActionMap,
                clickFileName,
                listIndexPosition,
            )
            return
        }
    }
}
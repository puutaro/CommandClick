package com.puutaro.commandclick.proccess.qr.qr_dialog_config

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionDataMapKeyObj
import com.puutaro.commandclick.proccess.js_macro_libs.macros.JsPathMacroForListIndex
import com.puutaro.commandclick.proccess.js_macro_libs.exec_handler.ExecMacroHandlerForListIndex
import com.puutaro.commandclick.proccess.js_macro_libs.exec_handler.ExecMacroHandlerForQr
import com.puutaro.commandclick.proccess.js_macro_libs.macros.JsMacroForQr
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor

object MacroHandlerForQrAndListIndex {

    fun handle(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        editListRecyclerView: RecyclerView?,
        jsActionMap: Map<String, String>?,
        selectecdLineMap: Map<String, String>,
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
                fragment,
                macroForQr,
                selectecdLineMap,
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
                fragment,
                fannelInfoMap,
                setReplaceVariableMap,
                busyboxExecutor,
                editListRecyclerView,
                jsActionMap,
                selectecdLineMap,
                listIndexPosition,
            )
            return
        }
    }
}
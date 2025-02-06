package com.puutaro.commandclick.proccess.js_macro_libs.exec_handler

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionDataMapKeyObj
import com.puutaro.commandclick.proccess.intent.lib.JavascriptExecuter
import com.puutaro.commandclick.proccess.edit_list.libs.ListIndexReplacer
import com.puutaro.commandclick.proccess.qr.qr_dialog_config.MacroHandlerForQrAndListIndex
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.JavaScriptLoadUrl

object JsPathHandlerForQrAndEditList {

    fun handle(
        fragment: Fragment,
        fannelInfoMap: HashMap<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        editListRecyclerView: RecyclerView?,
        jsActionMap: Map<String, String>?,
        selectedItemLineMap: Map<String, String>,
        listIndexPosition: Int
    ){
        if(
            jsActionMap.isNullOrEmpty()
        ) return
        val actionType = jsActionMap.get(
            JsActionDataMapKeyObj.JsActionDataMapKey.TYPE.key
        ) ?: return
        val jsActionType =
            JsActionDataMapKeyObj.JsActionDataTypeKey.values()
                .firstOrNull{
                    it.key == actionType
                } ?: return
        when (jsActionType) {
            JsActionDataMapKeyObj.JsActionDataTypeKey.MACRO
            -> MacroHandlerForQrAndListIndex.handle(
                fragment,
                fannelInfoMap,
                setReplaceVariableMap,
                busyboxExecutor,
                editListRecyclerView,
                jsActionMap,
                selectedItemLineMap,
                listIndexPosition,
            )
            JsActionDataMapKeyObj.JsActionDataTypeKey.JS_CON
            -> execJs(
                fragment,
                jsActionMap,
                selectedItemLineMap,
                listIndexPosition
            )
        }
    }


    private fun execJs(
        fragment: Fragment,
        jsActionMap: Map<String, String>?,
        selectedItemLineMap: Map<String, String>,
        listIndexListPosition: Int,
    ){
        if(
            jsActionMap.isNullOrEmpty()
        ) return
        val jsConSrcBeforeReplace = jsActionMap.get(
            JsActionDataMapKeyObj.JsActionDataMapKey.JS_CON.key
        )
        val jsConSrc = ListIndexReplacer.replace(
//            fragment,
            jsConSrcBeforeReplace,
            selectedItemLineMap,
            listIndexListPosition,
        ) ?: return
        val jsCon = jsConSrc
            .replace(Regex("\n[ \t]*//.*"), "")
            .replace(Regex("^[ \t]*//.*"), "")
        if(
            jsCon.isEmpty()
        ) return
        JavascriptExecuter.jsUrlLaunchHandler(
            fragment,
            JavaScriptLoadUrl.makeLastJsCon(jsCon)
        )
    }
}
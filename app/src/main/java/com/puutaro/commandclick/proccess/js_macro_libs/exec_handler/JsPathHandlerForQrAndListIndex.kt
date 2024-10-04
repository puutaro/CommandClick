package com.puutaro.commandclick.proccess.js_macro_libs.exec_handler

import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionDataMapKeyObj
import com.puutaro.commandclick.proccess.intent.lib.JavascriptExecuter
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.libs.ListIndexReplacer
import com.puutaro.commandclick.proccess.qr.qr_dialog_config.MacroHandlerForQrAndListIndex
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import java.time.LocalDateTime

object JsPathHandlerForQrAndListIndex {

    fun handle(
        editFragment: EditFragment,
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
                editFragment,
                jsActionMap,
                selectedItemLineMap,
                listIndexPosition,
            )
            JsActionDataMapKeyObj.JsActionDataTypeKey.JS_CON
            -> execJs(
                editFragment,
                jsActionMap,
                selectedItemLineMap,
                listIndexPosition
            )
        }
    }


    private fun execJs(
        editFragment: EditFragment,
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
            editFragment,
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
            editFragment,
            JavaScriptLoadUrl.makeLastJsCon(jsCon)
        )
    }
}
package com.puutaro.commandclick.proccess.js_macro_libs.exec_handler

import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionDataMapKeyObj
import com.puutaro.commandclick.proccess.intent.lib.JavascriptExecuter
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.proccess.qr.qr_dialog_config.MacroHandlerForQrAndListIndex
import com.puutaro.commandclick.util.JavaScriptLoadUrl

object JsPathHandlerForQrAndListIndex {

    fun handle(
        editFragment: EditFragment,
        jsActionMap: Map<String, String>?,
        selectedItem: String,
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
                selectedItem,
                listIndexPosition,
            )
            JsActionDataMapKeyObj.JsActionDataTypeKey.JS_CON
            -> execJs(
                editFragment,
                jsActionMap,
                selectedItem,
                listIndexPosition
            )
        }
    }


    private fun execJs(
        editFragment: EditFragment,
        jsActionMap: Map<String, String>?,
        selectedItem: String,
        listIndexListPosition: Int,
    ){
        if(
            jsActionMap.isNullOrEmpty()
        ) return
        val filterDir = ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
            editFragment,
            ListIndexForEditAdapter.indexListMap,
            ListIndexForEditAdapter.listIndexTypeKey
        )
        val selectedFileNameOrPath =
            selectedItem
                .split("\t")
                .lastOrNull()
                ?: String()
        val selectedTitle =
            selectedItem
                .split("\t")
                .firstOrNull()
                ?: String()
        val jsConSrc = jsActionMap.get(
            JsActionDataMapKeyObj.JsActionDataMapKey.JS_CON.key
        )?.replace(
            "\${ITEM_TITLE}",
            selectedTitle,
        )?.replace(
            "\${ITEM_NAME}",
            selectedFileNameOrPath,
        )?.replace(
            "\${INDEX_LIST_DIR_PATH}",
            filterDir,
        )?.replace(
            "\${POSITION}",
            listIndexListPosition.toString()
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
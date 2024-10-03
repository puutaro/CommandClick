package com.puutaro.commandclick.proccess.qr.qr_dialog_config

import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.js_macro_libs.exec_handler.JsPathHandlerForQrAndListIndex
import com.puutaro.commandclick.proccess.qr.QrDialogConfig
import com.puutaro.commandclick.proccess.qr.qr_dialog_config.config_settings.ClickSettingsForQrDialog

object QrDialogClickHandler {
//    fun handle(
//        isLongClick: Boolean,
//        editFragment: EditFragment,
//        selectedItemLineMap: Map<String, String>,
//        listIndexPosition: Int
//    ){
//        val clickKey = when(isLongClick){
//            true -> QrDialogConfig.QrDialogConfigKey.LONG_CLICK.key
//            false -> QrDialogConfig.QrDialogConfigKey.CLICK.key
//        }
//        val qrDialogConfigMap = editFragment.qrDialogConfig
//            ?: emptyMap()
//        val isThisClickEnable = ClickSettingsForQrDialog.howEnableClick(
//            clickKey,
//            qrDialogConfigMap,
//        )
//        if(!isThisClickEnable){
//            return
//        }
//
//        val clickConfigListCon = ClickSettingsForQrDialog.makeClickConfigListStr(
//            qrDialogConfigMap,
//            clickKey,
//        )
//        val jsActionMap = JsActionCompleterForQr.makeJsActionMap(
//            editFragment,
//            clickConfigListCon,
//            isLongClick,
//        )
//        if(
//            jsActionMap.isEmpty()
//        ) return
//        JsPathHandlerForQrAndListIndex.handle(
//            editFragment,
//            jsActionMap,
//            selectedItemLineMap,
//            listIndexPosition,
//        )
//        editFragment.binding.editListSearchEditText.setText(String())
//    }
}
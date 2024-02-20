package com.puutaro.commandclick.proccess.qr.qr_dialog_config

import android.widget.Toast
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.js_macro_libs.exec_handler.JsPathHandlerForQrAndListIndex
import com.puutaro.commandclick.proccess.qr.QrDialogConfig

object QrDialogClickHandler {
    fun handle(
        isLongClick: Boolean,
        editFragment: EditFragment,
        clickFileName: String,
        listIndexPosition: Int
    ){
        val context = editFragment.context
        if(
            clickFileName.isEmpty()
            || clickFileName == "-"
        ) {
            Toast.makeText(
                context,
                "no file",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val clickKey = when(isLongClick){
            true -> QrDialogConfig.QrDialogConfigKey.LONG_CLICK.key
            false -> QrDialogConfig.QrDialogConfigKey.CLICK.key
        }
        val qrDialogConfigMap = editFragment.qrDialogConfig
            ?: emptyMap()
        val isThisClickEnable = QrDialogConfig.howEnableClick(
            clickKey,
            qrDialogConfigMap,
        )
        if(!isThisClickEnable){
            return
        }

        val clickConfigListCon = QrDialogConfig.makeClickConfigListStr(
            qrDialogConfigMap,
            clickKey,
        )
        val jsActionMap = JsActionCompleterForQr.makeJsActionMap(
            editFragment,
            clickConfigListCon,
            isLongClick,
        )
        if(
            jsActionMap.isEmpty()
        ) return
        JsPathHandlerForQrAndListIndex.handle(
            editFragment,
            jsActionMap,
            clickFileName,
            listIndexPosition,
        )
        editFragment.binding.editListSearchEditText.setText(String())
    }
}
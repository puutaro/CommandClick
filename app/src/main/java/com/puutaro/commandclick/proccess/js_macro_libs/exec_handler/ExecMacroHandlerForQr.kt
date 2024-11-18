package com.puutaro.commandclick.proccess.js_macro_libs.exec_handler

import androidx.fragment.app.Fragment
import com.puutaro.commandclick.proccess.ScriptFileDescription
import com.puutaro.commandclick.proccess.js_macro_libs.macros.JsMacroForQr
import com.puutaro.commandclick.proccess.js_macro_libs.qr_libs.ExecQr
import com.puutaro.commandclick.proccess.edit_list.config_settings.ListSettingsForEditList
import com.puutaro.commandclick.util.dialog.DialogObject

object ExecMacroHandlerForQr {
    fun handle(
        fragment: Fragment,
        macroForQr: JsMacroForQr,
        selectedLineMap: Map<String, String>,
    ){
        val context = fragment.context
            ?: return
        val clickFileName =  selectedLineMap.get(
            ListSettingsForEditList.MapListPathManager.Key.SRC_CON.key
        ) ?: return
        when(macroForQr) {
            JsMacroForQr.EXEC_QR
            -> ExecQr.exec(
                fragment,
                clickFileName,
            )

            JsMacroForQr.DESC
            -> {
                val parentDirPath = String()
//                ActionToolForQr.getParentDirPath(
//                    editFragment
//                )
                val contents = ActionToolForQr.getContents(
                    fragment.context,
                    parentDirPath,
                    clickFileName
                ) ?: return
                ScriptFileDescription.show(
                    fragment,
                    contents.split("\n"),
//                    parentDirPath,
                    clickFileName
                )
            }

//            JsMacroForQr.EDIT_LOGO
//            -> {
////                val parentDirPath = ActionToolForQr.getParentDirPath(
////                    editFragment
////                )
//                QrLogoEditDialogLauncher.launch(
//                    editFragment,
////                    parentDirPath,
//                    clickFileName,
//                    editFragment.qrDialogConfig ?: emptyMap(),
////                    qrDialogConfigMap
//                )
//            }

            JsMacroForQr.FILE_CONTENTS
            -> {
                val parentDirPath = String()
//                ActionToolForQr.getParentDirPath(
//                    editFragment
//                )
                val contents = ActionToolForQr.getContents(
                    fragment.context,
                    parentDirPath,
                    clickFileName
                ) ?: return
                DialogObject.simpleTextShow(
                    context,
                    clickFileName,
                    contents,
                )
            }
        }
    }
}
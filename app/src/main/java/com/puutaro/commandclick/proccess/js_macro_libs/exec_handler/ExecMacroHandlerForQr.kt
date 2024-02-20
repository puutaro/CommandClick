package com.puutaro.commandclick.proccess.js_macro_libs.exec_handler

import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.ScriptFileDescription
import com.puutaro.commandclick.proccess.qr.qr_dialog_config.QrLogoEditDialogLauncher
import com.puutaro.commandclick.proccess.js_macro_libs.macros.JsMacroForQr
import com.puutaro.commandclick.proccess.js_macro_libs.qr_libs.ExecQr
import com.puutaro.commandclick.util.dialog.DialogObject

object ExecMacroHandlerForQr {
    fun handle(
        editFragment: EditFragment,
        macroForQr: JsMacroForQr,
        clickFileName: String,
    ){
        val context = editFragment.context
            ?: return
        when(macroForQr) {
            JsMacroForQr.EXEC_QR
            -> ExecQr.exec(
                editFragment,
                clickFileName,
            )

            JsMacroForQr.DESC
            -> {
                val parentDirPath = ActionToolForQr.getParentDirPath(
                    editFragment
                )
                val contents = ActionToolForQr.getContents(
                    editFragment.context,
                    parentDirPath,
                    clickFileName
                ) ?: return
                ScriptFileDescription.show(
                    editFragment,
                    contents.split("\n"),
                    parentDirPath,
                    clickFileName
                )
            }

            JsMacroForQr.EDIT_LOGO
            -> {
                val parentDirPath = ActionToolForQr.getParentDirPath(
                    editFragment
                )
                QrLogoEditDialogLauncher.launch(
                    editFragment,
                    parentDirPath,
                    clickFileName,
                    editFragment.qrDialogConfig ?: emptyMap(),
//                    qrDialogConfigMap
                )
            }

            JsMacroForQr.FILE_CONTENTS
            -> {
                val parentDirPath = ActionToolForQr.getParentDirPath(
                    editFragment
                )
                val contents = ActionToolForQr.getContents(
                    editFragment.context,
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
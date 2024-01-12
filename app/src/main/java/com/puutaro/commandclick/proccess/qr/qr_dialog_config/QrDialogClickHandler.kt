package com.puutaro.commandclick.proccess.qr.qr_dialog_config

import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.ScriptFileDescription
import com.puutaro.commandclick.proccess.qr.QrConfirmDialog
import com.puutaro.commandclick.proccess.qr.QrDecodedTitle
import com.puutaro.commandclick.proccess.qr.QrDialogConfig
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.TargetFragmentInstance
import com.puutaro.commandclick.util.dialog.DialogObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

object QrDialogClickHandler {
    fun handle(
        isLongClick: Boolean,
        fragment: Fragment,
        currentAppDirPath: String,
        parentDirPath: String,
        clickFileName: String,
        qrDialogConfigMap: Map<String, String>
    ){
        val context = fragment.context
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
        val isThisClickEnable = QrDialogConfig.howEnableClick(
            clickKey,
            qrDialogConfigMap,
        )
        val defaultMode = when(isLongClick){
            true -> QrDialogConfig.ClickModeValues.EDIT_LOGO
            else -> QrDialogConfig.ClickModeValues.FILE_CONTENTS
        }
        if(!isThisClickEnable){
            return
        }
        val modeStr = qrDialogConfigMap.get(clickKey)
        val mode = QrDialogConfig.ClickModeValues.values().find {
            it.mode == modeStr
        } ?: defaultMode
        val contents = if(
            File("${parentDirPath}/${clickFileName}").isFile
        ) ReadText(
            parentDirPath,
            clickFileName
        ).readText()
        else "no file"
        when(mode){
            QrDialogConfig.ClickModeValues.EXEC_QR
            -> execQr(
                fragment,
                currentAppDirPath,
                parentDirPath,
                contents,
            )
            QrDialogConfig.ClickModeValues.DESC
            -> ScriptFileDescription.show(
                fragment,
                contents.split("\n"),
                parentDirPath,
                clickFileName
            )
            QrDialogConfig.ClickModeValues.EDIT_LOGO
            -> QrLogoEditDialogLauncher.launch(
                fragment,
                parentDirPath,
                clickFileName,
                qrDialogConfigMap
            )
            QrDialogConfig.ClickModeValues.FILE_CONTENTS
            -> DialogObject.simpleTextShow(
                context,
                clickFileName,
                contents,
            )
        }
        when(fragment){
            is EditFragment -> {
                fragment.binding.editListSearchEditText.setText(String())
            }
        }
    }

    private fun execQr(
        fragment: Fragment,
        currentAppDirPath: String,
        parentDirPath: String,
        contents: String,
    ){
        val targetFragmentInstance = TargetFragmentInstance()
        val terminalFragment =
            targetFragmentInstance.getCurrentTerminalFragmentFromFrag(fragment.activity)
                ?: return
        val termLinearParam = terminalFragment.view?.layoutParams as? LinearLayout.LayoutParams
            ?: return
        val onLaunchByWebViewDialog = termLinearParam.weight <= 0f
        val useAppDirPath =
            when(onLaunchByWebViewDialog){
                true -> currentAppDirPath
                else -> parentDirPath
            }
        CoroutineScope(Dispatchers.Main).launch {
            QrConfirmDialog(
                fragment,
                null,
                null,
                useAppDirPath,
                QrDecodedTitle.makeTitle(contents),
                contents
            ).launch()
        }
    }
}
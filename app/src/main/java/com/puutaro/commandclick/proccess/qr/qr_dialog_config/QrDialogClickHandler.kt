package com.puutaro.commandclick.proccess.qr.qr_dialog_config

import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.ScriptFileDescription
import com.puutaro.commandclick.proccess.qr.QrConfirmDialog
import com.puutaro.commandclick.proccess.qr.QrDecodedTitle
import com.puutaro.commandclick.proccess.qr.QrDialogConfig
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import com.puutaro.commandclick.util.dialog.DialogObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object QrDialogClickHandler {
    fun handle(
        isLongClick: Boolean,
        editFragment: EditFragment,
        currentAppDirPath: String,
        clickFileName: String,
        qrDialogConfigMap: Map<String, String>
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
        val clickMap = QrDialogConfig.makeQrLogoClickMap(
            qrDialogConfigMap,
            clickKey,
        )
        val parentDirPath = ListIndexForEditAdapter.filterDir
        val isThisClickEnable = QrDialogConfig.howEnableClick(
            clickKey,
            qrDialogConfigMap,
        )
        val defaultMode = when(isLongClick){
            true -> QrDialogConfig.ClickTypeValuesForQrDialog.EDIT_LOGO
            else -> QrDialogConfig.ClickTypeValuesForQrDialog.FILE_CONTENTS
        }
        if(!isThisClickEnable){
            return
        }
        val modeStr = clickMap.get(QrDialogConfig.ClickSettingKeyForQrDialog.TYPE.key)
        val mode = QrDialogConfig.ClickTypeValuesForQrDialog.values().find {
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
            QrDialogConfig.ClickTypeValuesForQrDialog.EXEC_QR
            -> execQr(
                editFragment,
                currentAppDirPath,
                parentDirPath,
                contents,
            )
            QrDialogConfig.ClickTypeValuesForQrDialog.DESC
            -> ScriptFileDescription.show(
                editFragment,
                contents.split("\n"),
                parentDirPath,
                clickFileName
            )
            QrDialogConfig.ClickTypeValuesForQrDialog.EDIT_LOGO
            -> QrLogoEditDialogLauncher.launch(
                editFragment,
                parentDirPath,
                clickFileName,
                qrDialogConfigMap
            )
            QrDialogConfig.ClickTypeValuesForQrDialog.FILE_CONTENTS
            -> DialogObject.simpleTextShow(
                context,
                clickFileName,
                contents,
            )
        }
        when(editFragment){
            is EditFragment -> {
                editFragment.binding.editListSearchEditText.setText(String())
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
        var terminalFragment: TerminalFragment? = null
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.Main) {
                for (i in 1..10) {
                    terminalFragment =
                        targetFragmentInstance.getCurrentTerminalFragmentFromFrag(fragment.activity)
                    if (terminalFragment != null) break
                    delay(100)
                }
            }
            if(terminalFragment == null) return@launch
            val termLinearParam = terminalFragment?.view?.layoutParams as? LinearLayout.LayoutParams
                ?: return@launch
            val onLaunchByWebViewDialog = termLinearParam.weight <= 0f
            val useAppDirPath =
                withContext(Dispatchers.IO) {
                    when (onLaunchByWebViewDialog) {
                        true -> currentAppDirPath
                        else -> parentDirPath
                    }
                }
            withContext(Dispatchers.Main) {
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
}
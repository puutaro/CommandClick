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
import com.puutaro.commandclick.proccess.qr.qr_dialog_config.config_settings.ClickSettingsForQrDialog
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
            true -> ClickSettingsForQrDialog.ClickTypeValues.EDIT_LOGO
            else -> ClickSettingsForQrDialog.ClickTypeValues.FILE_CONTENTS
        }
        if(!isThisClickEnable){
            return
        }
        val modeStr = clickMap.get(ClickSettingsForQrDialog.ClickSettingKey.TYPE.key)
        val mode = ClickSettingsForQrDialog.ClickTypeValues.values().find {
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
            ClickSettingsForQrDialog.ClickTypeValues.EXEC_QR
            -> execQr(
                editFragment,
                currentAppDirPath,
                parentDirPath,
                contents,
            )
            ClickSettingsForQrDialog.ClickTypeValues.DESC
            -> ScriptFileDescription.show(
                editFragment,
                contents.split("\n"),
                parentDirPath,
                clickFileName
            )
            ClickSettingsForQrDialog.ClickTypeValues.EDIT_LOGO
            -> QrLogoEditDialogLauncher.launch(
                editFragment,
                parentDirPath,
                clickFileName,
                qrDialogConfigMap
            )
            ClickSettingsForQrDialog.ClickTypeValues.FILE_CONTENTS
            -> DialogObject.simpleTextShow(
                context,
                clickFileName,
                contents,
            )
        }
        editFragment.binding.editListSearchEditText.setText(String())
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
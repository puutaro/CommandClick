package com.puutaro.commandclick.proccess.js_macro_libs.qr_libs

import android.widget.LinearLayout
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.js_macro_libs.exec_handler.ActionToolForQr
import com.puutaro.commandclick.proccess.qr.QrConfirmDialog
import com.puutaro.commandclick.proccess.qr.QrDecodedTitle
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ExecQr {
    fun exec(
        editFragment: EditFragment,
        clickFileName: String
    ){
        val context = editFragment.context
        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
            editFragment.fannelInfoMap
        )
        val parentDirPath = ActionToolForQr.getParentDirPath(
            editFragment
        )
        val contents = ActionToolForQr.getContents(
            editFragment.context,
            parentDirPath,
            clickFileName
        ) ?: return

        val targetFragmentInstance = TargetFragmentInstance()
        var terminalFragment: TerminalFragment? = null
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.Main) {
                for (i in 1..10) {
                    terminalFragment =
                        targetFragmentInstance.getCurrentTerminalFragmentFromFrag(editFragment.activity)
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
                    editFragment,
                    null,
                    useAppDirPath,
                    QrDecodedTitle.makeTitle(
                        context,
                        contents
                    ),
                    contents
                ).launch()
            }
        }
    }
}
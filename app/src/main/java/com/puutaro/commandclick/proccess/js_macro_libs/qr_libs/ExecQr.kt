package com.puutaro.commandclick.proccess.js_macro_libs.qr_libs

import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.path.UsePath
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
        fragment: Fragment,
        clickFileName: String
    ){
        val context = fragment.context
//        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//            editFragment.fannelInfoMap
//        )
        val parentDirPath = String()
//        ActionToolForQr.getParentDirPath(
//            editFragment
//        )
        val contents = ActionToolForQr.getContents(
            context,
            parentDirPath,
            clickFileName
        ) ?: return

        var terminalFragment: TerminalFragment? = null
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.Main) {
                for (i in 1..10) {
                    terminalFragment =
                        TargetFragmentInstance.getCurrentTerminalFragmentFromFrag(fragment.activity)
                    if (terminalFragment != null) break
                    delay(100)
                }
            }
            if(terminalFragment == null) return@launch
            val termLinearParam = terminalFragment?.view?.layoutParams as? LinearLayout.LayoutParams
                ?: return@launch
            val onLaunchByWebViewDialog = termLinearParam.weight <= 0f
//            val useAppDirPath =
//                withContext(Dispatchers.IO) {
//                    when (onLaunchByWebViewDialog) {
//                        true -> UsePath.cmdclickDefaultAppDirPath
//                        else -> parentDirPath
//                    }
//                }
            withContext(Dispatchers.Main) {
                QrConfirmDialog.launch(
                    fragment,
                    null,
//                    useAppDirPath,
                    QrDecodedTitle.makeTitle(
                        context,
                        contents
                    ),
                    contents
                )
            }
        }
    }
}
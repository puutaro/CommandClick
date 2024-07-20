package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.qr

import android.webkit.JavascriptInterface
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.dialog.JsDialog
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.EditSettingExtraArgsTool
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.proccess.qr.QrScanner
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class JsQrGetter(
    private val terminalFragment: TerminalFragment
) {
    private val activity = terminalFragment.activity
    private val fannelInfoMap = terminalFragment.fannelInfoMap
    private val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
        fannelInfoMap
    )
    private val currentFannelName = FannelInfoTool.getCurrentFannelName(
        fannelInfoMap
    )
    private val currentFannelState = FannelInfoTool.getCurrentStateName(
        fannelInfoMap
    )

    @JavascriptInterface
    fun get_S(
        stockConDirPathForTsv: String,
        compPrefix: String,
        compSuffix: String,
    ){
        val contentsName = JsDialog(terminalFragment).prompt(
            "Input contents name",
            String(),
            String()
        )
        if(
            contentsName.isEmpty()
        ) return
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.Main){
                execGet(
                    contentsName,
                    stockConDirPathForTsv,
                    compPrefix,
                    compSuffix,
                )
            }
        }
    }

    private fun execGet(
        contentsName: String,
        stockConDirPathForTsv: String,
        compPrefix: String,
        compSuffix: String,
    ){
        val editFragment = TargetFragmentInstance().getCurrentEditFragmentFromFragment(
            activity,
            currentAppDirPath,
            currentFannelName,
            currentFannelState,
        ) ?: return
        val fileName = UsePath.compExtend(
            contentsName,
            ".txt",
        )
        val parentDirPath = ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
            editFragment,
            ListIndexForEditAdapter.indexListMap,
            ListIndexForEditAdapter.listIndexTypeKey
        )
        val filePath = "${parentDirPath}/${fileName}"
        if(
            File(filePath).isFile
        ) {
            ToastUtils.showShort("Already exist: ${filePath}")
            return
        }
        val stockDirAndCompMap = mapOf(
            EditSettingExtraArgsTool.ExtraKey.PARENT_DIR_PATH.key to stockConDirPathForTsv,
            EditSettingExtraArgsTool.ExtraKey.COMP_PREFIX.key to compPrefix,
            EditSettingExtraArgsTool.ExtraKey.COMP_SUFFIX.key to compSuffix,
        )
        QrScanner.saveFromCamera(
            editFragment,
            parentDirPath,
            stockDirAndCompMap,
            fileName
        )
    }
}
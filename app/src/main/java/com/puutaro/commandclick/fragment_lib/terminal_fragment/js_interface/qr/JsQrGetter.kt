package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.qr

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ListIndexDuplicate
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.dialog.JsDialog
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.EditSettingExtraArgsTool
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class JsQrGetter(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {

    @JavascriptInterface
    fun get_S(
        stockConDirPathForTsv: String,
        compPrefix: String,
        compSuffix: String,
    ){
        /*
        ## Description

        Get contents from QR code

        ## Corresponding macro

        -> [GET_QR_CON](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#get_qr_con)

        ## stockConDirPathForTsv arg

      　-> [parentDirPath in args for GET_QR_CON macro in toolbar](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#args-for-get_qr_con)

        ## compPrefix arg

        -> [compPrefix in args for GET_QR_CON macro in toolbar](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#args-for-get_qr_con)

        ## compSuffix arg

        -> [compSuffix in args for GET_QR_CON macro in toolbar](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#args-for-get_qr_con)


        ## Example

        ```js.js
        run=getFile
            ?func=jsQrGetter.get_S
            ?args=
                &stockConDirPathForTsv=${save dir path}
                &compSuffix=".txt"
        ```
                `
        */

        val contentsName = JsDialog(terminalFragmentRef).prompt(
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
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val activity = terminalFragment.activity
        val fannelInfoMap = terminalFragment.fannelInfoMap
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
        val currentFannelState = FannelInfoTool.getCurrentStateName(
            fannelInfoMap
        )
        val editFragment = TargetFragmentInstance.getCurrentEditFragmentFromFragment(
            activity,
//            currentAppDirPath,
            currentFannelName,
            currentFannelState,
        ) ?: return
        val fileName = UsePath.compExtend(
            contentsName,
            ".txt",
        )
//        val parentDirPath = ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
//            editFragment,
//            ListIndexAdapter.indexListMap,
//            ListIndexAdapter.listIndexTypeKey
//        )
        ListIndexDuplicate.isFileDetect(
//            parentDirPath,
            fileName
        ).let {
                isDetect ->
            if(
                isDetect
            ) return
        }
        val stockDirAndCompMap = mapOf(
            EditSettingExtraArgsTool.ExtraKey.PARENT_DIR_PATH.key to stockConDirPathForTsv,
            EditSettingExtraArgsTool.ExtraKey.COMP_PREFIX.key to compPrefix,
            EditSettingExtraArgsTool.ExtraKey.COMP_SUFFIX.key to compSuffix,
        )
//        QrScanner.saveFromCamera(
//            editFragment,
////            parentDirPath,
//            stockDirAndCompMap,
//            fileName
//        )
    }
}
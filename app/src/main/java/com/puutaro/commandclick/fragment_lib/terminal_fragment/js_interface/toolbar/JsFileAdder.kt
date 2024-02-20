package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.common.variable.intent.scheme.BroadCastIntentSchemeForEdit
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.dialog.JsDialog
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.EditSettingExtraArgsTool
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.TypeSettingsForListIndex
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.state.SharePreferenceMethod
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import java.io.File

class JsFileAdder(
    private val terminalFragment: TerminalFragment
) {

    private val activity = terminalFragment.activity
    private val readSharePreferenceMap =
        terminalFragment.readSharePreferenceMap
    private val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
        readSharePreferenceMap,
        SharePrefferenceSetting.current_app_dir
    )
    private val currentFannelPath = SharePreferenceMethod.getReadSharePreffernceMap(
        readSharePreferenceMap,
        SharePrefferenceSetting.current_fannel_name
    )
    private val currentFannelState = SharePreferenceMethod.getReadSharePreffernceMap(
        readSharePreferenceMap,
        SharePrefferenceSetting.current_fannel_state
    )

    @JavascriptInterface
    fun add(
        compPrefix: String,
        compSuffix: String,
    ){

        val editFragment = TargetFragmentInstance().getCurrentEditFragmentFromFragment(
            activity,
            currentAppDirPath,
            currentFannelPath,
            currentFannelState
        ) ?: return

        execAddItemForEdit(
            editFragment,
            compPrefix,
            compSuffix,
        )
    }

    private fun execAddItemForEdit(
        editFragment: EditFragment,
        compPrefix: String,
        compSuffix: String
    ){
        val context = editFragment.context
            ?: return
        val type = ListIndexEditConfig.getListIndexType(
            editFragment
        )
        when(type){
            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL,
            TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT -> {
                return
            }
            TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
            -> {}
        }
        val parentDirPath =
            ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
                editFragment,
                ListIndexForEditAdapter.indexListMap,
                ListIndexForEditAdapter.listIndexTypeKey
            )
        val fileName = JsDialog(terminalFragment).prompt(
            "Type item name",
            String(),
            String()
        )
        if(
            fileName.isEmpty()
        ) return
        val compFileNameMap = mapOf(
            EditSettingExtraArgsTool.ExtraKey.COMP_PREFIX.key to compPrefix,
            EditSettingExtraArgsTool.ExtraKey.COMP_SUFFIX.key to compSuffix
        )
        val compFileName = EditSettingExtraArgsTool.makeCompFileName(
            editFragment.busyboxExecutor,
            fileName,
            compFileNameMap ,
        )
        FileSystems.writeFile(
            File(
                parentDirPath,
                compFileName
            ).absolutePath,
            String()
        )
        BroadcastSender.normalSend(
            context,
            BroadCastIntentSchemeForEdit.UPDATE_INDEX_LIST.action
        )
    }

}
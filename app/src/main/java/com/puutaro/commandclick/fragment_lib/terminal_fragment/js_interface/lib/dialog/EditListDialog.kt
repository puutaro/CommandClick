package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog

import android.app.Dialog
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.settings.EditSettings
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.custom_view.OutlineTextView
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system.JsFannelInfo
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.WithEditComponentListView
import com.puutaro.commandclick.proccess.edit.lib.ListSettingVariableListMaker
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.FannelInfoTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.lang.ref.WeakReference

class EditListDialog(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {
    fun create(
        fannelInfoCon: String,
        editListConfigPath: String,
    ){
        CoroutineScope(Dispatchers.Main).launch {
            execCreate(
                fannelInfoCon,
                editListConfigPath,
            )
        }

    }

    fun execCreate(
        fannelInfoCon: String,
        editListConfigPath: String,
    ){
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val context = terminalFragment.context
            ?: return

        terminalFragment.editListDialog = Dialog(context)
        terminalFragment.editListDialog?.setContentView(
            R.layout.edit_list_dialog_layout
        ) ?: return
        val constraintLayout =
            terminalFragment.editListDialog?.findViewById<ConstraintLayout>(
                R.id.edit_list_dialog_constraint_layout
            ) ?: return
        val editBackstackCountFrame =
            terminalFragment.editListDialog?.findViewById<FrameLayout>(
                R.id.edit_list_dialog_backstack_count_frame
            ) ?: return
        val editBackstackCountView =
            terminalFragment.editListDialog?.findViewById<ShapeableImageView>(
                R.id.edit_list_dialog_backstack_count
            ) ?: return
        val editListTitleView =
            terminalFragment.editListDialog?.findViewById<OutlineTextView>(
                R.id.edit_list_dialog_title_view
            ) ?: return
        val editListTitleImage =
            terminalFragment.editListDialog?.findViewById<AppCompatImageView>(
                R.id.edit_list_dialog_title_image
            ) ?: return
        val editListRecyclerView =
            terminalFragment.editListDialog?.findViewById<RecyclerView>(
                R.id.edit_list_dialog_recycler_view
            ) ?: return
        val editListBkFrame =
            terminalFragment.editListDialog?.findViewById<FrameLayout>(
                R.id.edit_list_bk_frame
            ) ?: return

        val editListSearchEditText =
            terminalFragment.editListDialog?.findViewById<AppCompatEditText>(
                R.id.edit_list_dialog_search_edit_text
            ) ?: return
        val editFooterLinearlayout =
            terminalFragment.editListDialog?.findViewById<LinearLayoutCompat>(
                R.id.edit_list_dialog_footer_linearlayout
            ) ?: return

        val fannelInfoMap = CmdClickMap.createMap(
            fannelInfoCon,
            JsFannelInfo.fannelInfoMapSeparator
        ).toMap()
        val mainFannelFile = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap,
        ).let {
            File(UsePath.cmdclickDefaultAppDirPath, it)
        }
        val mainFannelConList = ReadText(
            mainFannelFile.absolutePath,
        ).textToList()
        val settingVariableList = CommandClickVariables.extractValListFromHolder(
            mainFannelConList,
            CommandClickScriptVariable.SETTING_SEC_START,
            CommandClickScriptVariable.SETTING_SEC_END,
        )
        val setReplaceVariableMap = SetReplaceVariabler.makeSetReplaceVariableMap(
            context,
            settingVariableList,
            mainFannelFile.name
        )
        val virtualSettingValsListForEditList = listOf(
            CommandClickScriptVariable.SETTING_SEC_START,
            "${CommandClickScriptVariable.EDIT_LIST_CONFIG}=${EditSettings.filePrefix}${editListConfigPath}",
            CommandClickScriptVariable.SETTING_SEC_END,
        )
        val editListConfigMap = ListSettingVariableListMaker.makeConfigMapFromSettingValList(
            context,
            CommandClickScriptVariable.EDIT_LIST_CONFIG,
            virtualSettingValsListForEditList,
            fannelInfoMap,
            setReplaceVariableMap,
            String()
        )
        WithEditComponentListView.create(
            terminalFragment,
            fannelInfoMap,
            setReplaceVariableMap,
            terminalFragment.busyboxExecutor,
            editListConfigMap,
            editBackstackCountFrame,
            editBackstackCountView,
            editListTitleView,
            editListTitleImage,
            editListRecyclerView,
            editListBkFrame,
            editListSearchEditText,
            editFooterLinearlayout,
            null,
            null,
            mainFannelConList,
        )
        terminalFragment.editListDialog?.setOnCancelListener {
            editListRecyclerView.removeAllViews()
            editFooterLinearlayout.removeAllViews()
            editListBkFrame.removeAllViews()
            constraintLayout.removeAllViews()
            terminalFragment.editListDialog?.dismiss()
            terminalFragment.editListDialog = null
        }
        terminalFragment.editListDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        terminalFragment.editListDialog?.show()
    }

    fun dismiss(){
        val constraintLayout =
            terminalFragmentRef.get()?.editListDialog?.findViewById<ConstraintLayout>(
                R.id.edit_list_dialog_constraint_layout
            )
        val editListRecyclerView =
            terminalFragmentRef.get()?.editListDialog?.findViewById<RecyclerView>(
                R.id.edit_list_dialog_recycler_view
            )
        val editListBkFrame =
            terminalFragmentRef.get()?.editListDialog?.findViewById<FrameLayout>(
                R.id.edit_list_bk_frame
            )
        val editFooterLinearlayout =
            terminalFragmentRef.get()?.editListDialog?.findViewById<LinearLayoutCompat>(
                R.id.edit_list_dialog_footer_linearlayout
            )
        editListRecyclerView?.removeAllViews()
        editFooterLinearlayout?.removeAllViews()
        editListBkFrame?.removeAllViews()
        constraintLayout?.removeAllViews()
        val terminalFragment = terminalFragmentRef.get()
        terminalFragment?.editListDialog?.dismiss()
        terminalFragment?.editListDialog = null
    }
}
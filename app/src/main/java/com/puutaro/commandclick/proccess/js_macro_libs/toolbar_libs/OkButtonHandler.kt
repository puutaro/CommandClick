package com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs

import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.TerminalShowByTerminalDo
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.ToolbarButtonBariantForEdit
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.ScriptFileSaver
import com.puutaro.commandclick.proccess.edit.lib.ListContentsSelectBoxTool
import com.puutaro.commandclick.proccess.edit.lib.SaveTagForListContents
import com.puutaro.commandclick.proccess.intent.ExecJsOrSellHandler
import com.puutaro.commandclick.util.Keyboard
import com.puutaro.commandclick.util.state.FannelInfoTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object OkButtonHandler {

    fun handle(
        editFragment: EditFragment,
    ) {
        val context = editFragment.context
        val fannelInfoMap = editFragment.fannelInfoMap
//        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//            fannelInfoMap
//        )
        val currentScriptFileName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
        val enableCmdEdit = editFragment.enableCmdEdit
        val onPassCmdVariableEdit =
            editFragment.passCmdVariableEdit ==
                    CommandClickScriptVariable.PASS_CMDVARIABLE_EDIT_ON_VALUE
        val scriptFileSaver = ScriptFileSaver(
            editFragment,
        )

        val buttonTag = SaveTagForListContents.OK.tag
        scriptFileSaver.save()
        val isCmdEditExecute = enableCmdEdit
                && editFragment.enableEditExecute
                && !onPassCmdVariableEdit
        val isSettingEditByPass = enableCmdEdit
                && editFragment.enableEditExecute
                && onPassCmdVariableEdit
        val isSettingEdit = !enableCmdEdit

        val isOnlyCmdEditNoFdialog = enableCmdEdit
                && !editFragment.enableEditExecute
        when (true) {
            isCmdEditExecute -> {
                Keyboard.hiddenKeyboardForFragment(
                    editFragment
                )
                ListContentsSelectBoxTool.saveListContents(
                    editFragment,
                    buttonTag
                )
                TerminalShowByTerminalDo.show(
                    editFragment,
                )
                ExecJsOrSellHandler.handle(
                    editFragment,
//                    currentAppDirPath,
                    currentScriptFileName,
                )
            }
            isSettingEditByPass,
            isOnlyCmdEditNoFdialog,
            isSettingEdit,
            -> {
                val listener =
                    context as? EditFragment.OnToolBarButtonClickListenerForEditFragment
                listener?.onToolBarButtonClickForEditFragment(
                    String(),
                    ToolbarButtonBariantForEdit.CANCEL,
                    mapOf(),
                    false
                )
            }
            else -> {}
        }
    }

    fun cmdValSaveAndBack(
        editFragment: EditFragment
    ){
        val context = editFragment.context
        val scriptFileSaver = ScriptFileSaver(
            editFragment,
        )
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.Main) {
                scriptFileSaver.save()
            }
            withContext(Dispatchers.Main) {
                val listener =
                    context as? EditFragment.OnToolBarButtonClickListenerForEditFragment
                listener?.onToolBarButtonClickForEditFragment(
                    String(),
                    ToolbarButtonBariantForEdit.CANCEL,
                    mapOf(),
                    false
                )
            }
        }
    }

}
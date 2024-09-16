package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.edit

import android.webkit.JavascriptInterface
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.EditTextSupportViewId
import com.puutaro.commandclick.proccess.edit.lib.EditVariableName
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import com.puutaro.commandclick.view_model.activity.EditViewModel
import java.lang.ref.WeakReference

class JsEdit(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {
//    private val context = terminalFragment.context
//    private val activity = terminalFragment.activity
//    private val editViewModel: EditViewModel by terminalFragment.activityViewModels()


    @JavascriptInterface
    fun updateByVariable(
        fannelScriptPath: String,
        targetVariableName: String,
        updateVariableValue: String,
    ){
        val jsScript = JsScript(terminalFragmentRef)
        updateEditText(
            targetVariableName,
            updateVariableValue
        )
        val jsContents = ReadText(
            fannelScriptPath
        ).readText()
        val updateJsContents = jsScript.replaceCommandVariable(
            jsContents,
            "${targetVariableName}=${updateVariableValue}"
        )
        FileSystems.writeFile(
            fannelScriptPath,
            updateJsContents
        )
    }

    @JavascriptInterface
    fun updateEditText(
        updateVariableName: String,
        updateVariableValue: String
    ){
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val context = terminalFragment.context
        val listener = context as? TerminalFragment.OnEditTextUpdateListenerForTermFragment
        val editViewModel: EditViewModel by terminalFragment.activityViewModels()
        val editTextId = editViewModel.variableNameToEditTextIdMap.get(updateVariableName)
        listener?.onEditTextUpdateForTermFragment(
            editTextId,
            updateVariableValue
        )
    }

    @JavascriptInterface
    fun getFromEditText(
        targetVariableName: String,
    ): String {
        val terminalFragment = terminalFragmentRef.get()
            ?: return String()
        val fannelInfoMap =
            terminalFragment.fannelInfoMap
//        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//            fannelInfoMap
//        )

        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
        val fannelState = FannelInfoTool.getCurrentStateName(
            fannelInfoMap
        )
        val activity = terminalFragment.activity
        val editFragment = TargetFragmentInstance.getCurrentEditFragmentFromFragment(
            activity,
//            currentAppDirPath,
            currentFannelName,
            fannelState
        ) ?: return String()
        val targetEditTextCon = EditVariableName.getText(
            editFragment,
            targetVariableName
        )
        return targetEditTextCon
    }

    @JavascriptInterface
    fun updateSpinner(
        updateVariableName: String,
        updateVariableValue: String
    ){
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val context = terminalFragment.context
        val listener = context as? TerminalFragment.OnSpinnerUpdateListenerForTermFragment
        val editViewModel: EditViewModel by terminalFragment.activityViewModels()
        val editTextId =
            editViewModel.variableNameToEditTextIdMap.get(updateVariableName)
                ?:return
        val currentSpinnerId = editTextId + EditTextSupportViewId.SPINNER.id
        listener?.onSpinnerUpdateForTermFragment(
            currentSpinnerId,
            updateVariableValue
        )
    }
}
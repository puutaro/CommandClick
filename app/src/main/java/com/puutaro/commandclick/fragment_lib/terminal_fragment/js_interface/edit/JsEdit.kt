package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.edit

import android.webkit.JavascriptInterface
import androidx.fragment.app.activityViewModels
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.EditTextSupportViewId
import com.puutaro.commandclick.proccess.edit.lib.EditVariableName
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.state.SharePrefTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import com.puutaro.commandclick.view_model.activity.EditViewModel
import java.io.File

class JsEdit(
    private val terminalFragment: TerminalFragment
) {
    private val context = terminalFragment.context
    private val activity = terminalFragment.activity
    private val editViewModel: EditViewModel by terminalFragment.activityViewModels()


    @JavascriptInterface
    fun updateByVariable(
        fannelScriptPath: String,
        targetVariableName: String,
        updateVariableValue: String,
    ){
        val jsScript = JsScript(terminalFragment)
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
        val listener = context as? TerminalFragment.OnEditTextUpdateListenerForTermFragment
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
        val readSharedPreferences =
            terminalFragment.readSharePreferenceMap
        val currentAppDirPath = SharePrefTool.getCurrentAppDirPath(
            readSharedPreferences
        )

        val currentFannelName = SharePrefTool.getCurrentFannelName(
            readSharedPreferences
        )
        val fannelState = SharePrefTool.getCurrentStateName(
            readSharedPreferences
        )
        val editFragment = TargetFragmentInstance().getCurrentEditFragmentFromFragment(
            activity,
            currentAppDirPath,
            currentFannelName,
            fannelState
        ) ?: return String()
        return EditVariableName.getText(
            editFragment,
            targetVariableName
        )
    }

    @JavascriptInterface
    fun updateSpinner(
        updateVariableName: String,
        updateVariableValue: String
    ){
        val listener = context as? TerminalFragment.OnSpinnerUpdateListenerForTermFragment
        val editTextId =
            editViewModel.variableNameToEditTextIdMap.get(updateVariableName)
                ?:return
        val currentSpinnerId = editTextId + EditTextSupportViewId.SPINNER.id
        listener?.onSpinnerUpdateForTermFragment(
            currentSpinnerId,
            updateVariableValue
        )
    }

    @JavascriptInterface
    fun removeFromEditHtml(
        editPath: String,
        removeUri: String
    ){
        val editPathObj = File(editPath)
        if(
            !editPathObj.isFile
        ) {
            ToastUtils.showLong("no exist\n ${editPath}")
            return
        }
        val removedUrlList = ReadText(
            editPath
        ).textToList().filter {
            val path = it.split("\t").lastOrNull()
            path != removeUri
        }.joinToString("\n")
        FileSystems.writeFile(
            editPath,
            removedUrlList
        )
    }
}
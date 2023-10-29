package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.EditTextSupportViewId
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.FragmentTagManager
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.TargetFragmentInstance
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
        val fannelScriptPathObj = File(fannelScriptPath)
        val parentDirPath = fannelScriptPathObj.parent
            ?: return
        val scriptName = fannelScriptPathObj.name
        updateEditText(
            targetVariableName,
            updateVariableValue
        )
        val jsContents = ReadText(
            parentDirPath,
            scriptName
        ).readText()
        val updateJsContents = jsScript.replaceCommandVariable(
            jsContents,
            "${targetVariableName}=${updateVariableValue}"
        )
        FileSystems.writeFile(
            parentDirPath,
            scriptName,
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
        val editTextId = editViewModel.variableNameToEditTextIdMap.get(targetVariableName)
            ?: return String()
        val cmdEditFragmentTag = FragmentTagManager.makeTag(
            FragmentTagManager.Prefix.cmdEditPrefix.str,
            terminalFragment.currentAppDirPath,
            terminalFragment.currentScriptName,
            FragmentTagManager.Suffix.ON.str
        )
        val editFragment = TargetFragmentInstance().getFromFragment<EditFragment>(
            activity,
            cmdEditFragmentTag
        ) ?: return String()
        val binding = editFragment.binding
        val editLinearLayout = binding.editLinearLayout
        val editTextInEditFragment = editLinearLayout.findViewById<EditText>(editTextId)
            ?: return String()
        return editTextInEditFragment.text.toString()
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
            Toast.makeText(
                context,
                "no exsit\n ${editPath}",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        val parentDir = editPathObj.parent
            ?: return
        val editFileName = editPathObj.name
        val removedUrlList = ReadText(
            parentDir,
            editFileName
        ).textToList().filter {
            val path = it.split("\t").lastOrNull()
            path != removeUri
        }.joinToString("\n")
        FileSystems.writeFile(
            parentDir,
            editFileName,
            removedUrlList
        )
    }
}
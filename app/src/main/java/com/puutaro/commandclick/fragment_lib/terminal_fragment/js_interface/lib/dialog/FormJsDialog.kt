package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog

import android.app.Dialog
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.LanguageTypeSelects
import com.puutaro.commandclick.common.variable.settings.FannelInfoSetting
import com.puutaro.commandclick.common.variable.edit.*
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.edit.lib.SetVariableTyper
import com.puutaro.commandclick.proccess.edit.lib.ScriptContentsLister
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.RecordNumToMapNameValueInHolder
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.*

class FormJsDialog(
    private val terminalFragment: TerminalFragment
) {
    private var formDialog: Dialog? = null
    private var returnValue = String()
    private val context = terminalFragment.context
    private val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
    private val variableTypeDefineListForMiniEdit
        = TypeVariable.variableTypeDefineListForMiniEdit

    private val exitTextStartId = 90000

    private val withEditComponentForFormJsDialog = WithEditComponentForFormJsDialog()
    private val languageType =
        LanguageTypeSelects.JAVA_SCRIPT

    private val languageTypeToSectionHolderMap =
        CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(languageType)
    private val settingSectionStart = languageTypeToSectionHolderMap?.get(
        CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
    ) as String
    private val settingSectionEnd = languageTypeToSectionHolderMap?.get(
        CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
    ) as String

    private val commandSectionStart = languageTypeToSectionHolderMap?.get(
        CommandClickScriptVariable.HolderTypeName.CMD_SEC_START
    ) as String
    private val commandSectionEnd = languageTypeToSectionHolderMap?.get(
        CommandClickScriptVariable.HolderTypeName.CMD_SEC_END
    ) as String


    fun create(
        title: String,
        formSettingVariables: String,
        formCommandVariables: String
    ): String {
        terminalViewModel.onDialog = true
        runBlocking {
            withContext(Dispatchers.Main) {
                execCreate(
                    title,
                    formSettingVariables,
                    formCommandVariables
                )
            }
            withContext(Dispatchers.IO) {
                while (true) {
                    delay(100)
                    if (!terminalViewModel.onDialog) {
                        formDialog?.dismiss()
                        formDialog = null
                        break
                    }
                }
            }
        }
        return returnValue
    }


    private fun execCreate(
        title: String,
        formSettingVariables: String,
        formCommandVariables: String
    ) {
        if(
            context == null
        ) {
            terminalViewModel.onDialog = false
            returnValue = String()
            return
        }
        val virtualJsContentsList =  makeVirtualJsContentsList(
            formSettingVariables,
            formCommandVariables
        )

        val recordNumToMapNameValueInCommandHolder =
            RecordNumToMapNameValueInHolder.parse(
                virtualJsContentsList,
                commandSectionStart,
                commandSectionEnd,
            )
        val recordNumToMapNameValueInSettingHolder =
            RecordNumToMapNameValueInHolder.parse(
                virtualJsContentsList,
                settingSectionStart,
                settingSectionEnd,
            )

        val setReplaceVariableMap = SetReplaceVariabler.makeSetReplaceVariableMap(
            context,
            CommandClickVariables.extractValListFromHolder(
                virtualJsContentsList,
                settingSectionStart,
                settingSectionEnd
            ),
            String(),
            String(),
        )

        val setVariableTypeList = SetVariableTyper.makeSetVariableTypeList(
            recordNumToMapNameValueInSettingHolder,
            String(),
            String(),
            null
        )?.joinToString("\n")?.let {
            SetReplaceVariabler.execReplaceByReplaceVariables(
                it,
                setReplaceVariableMap,
                String(),
                String()
            )
        }?.split("\n")

        val recordNumToSetVariableMaps = SetVariableTyper.makeRecordNumToSetVariableMaps(
            terminalFragment,
            setVariableTypeList,
            recordNumToMapNameValueInCommandHolder,
            setReplaceVariableMap,
            terminalFragment.busyboxExecutor,
        )

        formDialog = Dialog(
            context
        )
        formDialog?.setContentView(
            R.layout.form_dialog_laytout
        )
        val confirmTitleTextView =
            formDialog?.findViewById<AppCompatTextView>(
                R.id.form_dialog_title
            )
        if(
            title.isNotEmpty()
        ) confirmTitleTextView?.text = title
        else confirmTitleTextView?.isVisible = false
        val linearLayout =
            formDialog?.findViewById<LinearLayout>(
                R.id.form_dialog_contents_linear
            ) ?: return
        val virtualReadPreffrenceMap = mapOf(
            FannelInfoSetting.current_app_dir.name
                to terminalFragment.currentAppDirPath
        )
        val editParameters = EditParameters(
            terminalFragment,
            virtualJsContentsList,
            recordNumToMapNameValueInCommandHolder,
            recordNumToMapNameValueInSettingHolder,
            virtualReadPreffrenceMap,
            setReplaceVariableMap,
            true,
            emptyList()
        )

        execFormPartsAdd(
            editParameters,
            recordNumToSetVariableMaps,
            exitTextStartId,
            linearLayout
        )

        terminalViewModel.onDialog = true
        returnValue = String()

        val confirmCancelButton =
            formDialog?.findViewById<AppCompatImageButton>(
                R.id.form_dialog_cancel
            )
        confirmCancelButton?.setOnClickListener {
            terminalViewModel.onDialog = false
            returnValue = String()
            formDialog?.dismiss()
            formDialog = null
        }
        val confirmOkButton =
            formDialog?.findViewById<AppCompatImageButton>(
                R.id.form_dialog_ok
            )
        confirmOkButton?.setOnClickListener {
            formDialog?.dismiss()
            formDialog = null
            val updateVirtualJsContentsList = if(
                recordNumToMapNameValueInCommandHolder.isNullOrEmpty()
            ) virtualJsContentsList
            else {
                ScriptContentsLister(
                    listOf(linearLayout)
                ).update(
                    recordNumToMapNameValueInCommandHolder,
                    virtualJsContentsList,
                    exitTextStartId
                )
            }
            val dialogReturnStringListSource = CommandClickVariables.extractValListFromHolder(
                updateVirtualJsContentsList,
                commandSectionStart,
                commandSectionEnd
            )
            returnValue =
                dialogReturnStringListSource?.slice(
                    1 until dialogReturnStringListSource.size-1
                )?.joinToString("\n") ?: String()
            terminalViewModel.onDialog = false
        }

        formDialog?.setOnCancelListener {
            terminalViewModel.onDialog = false
            returnValue = String()
            formDialog?.dismiss()
            formDialog = null
        }
        formDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        formDialog?.window?.setGravity(
            Gravity.BOTTOM
        )
        formDialog?.show()
    }


    private fun makeVirtualJsContentsList(
        formSettingVariables: String,
        formCommandVariables: String
    ): List<String> {
        val setVariableTypeSource =
            formSettingVariables
                .split("\n")
                .map{
                    "${CommandClickScriptVariable.SET_VARIABLE_TYPE}=\"${it}\""
                }.joinToString("\n")
        val settingSectionContents =
            listOf(
                settingSectionStart,
                setVariableTypeSource,
                settingSectionEnd,
            ).joinToString("\n")
        val commandSectionContents =
            listOf(
                commandSectionStart,
                formCommandVariables,
                commandSectionEnd,
            ).joinToString("\n")
        return "\n\n${settingSectionContents}\n\n\n${commandSectionContents}\n".split("\n")
    }

    private fun execFormPartsAdd(
        editParameters: EditParameters,
        recordNumToSetVariableMaps: Map<Int, Map<String,String>?>?,
        editTextStartId: Int,
        linearLayout: LinearLayout
    ){
        val recordNumToNameToValueInHolderSize =
            editParameters.recordNumToMapNameValueInCommandHolder?.size ?: return
        (1..recordNumToNameToValueInHolderSize).forEach { seedNum ->
            val linearParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
            val currentOrder = seedNum - 1
            val currentRecordNumToMapNameValueInHolder =
                editParameters.recordNumToMapNameValueInCommandHolder.entries.elementAt(
                    currentOrder
                )
            val currentRecordNumToNameToValueInHolder =
                currentRecordNumToMapNameValueInHolder.value
            val insertTextView = TextView(context)
            val currentVariableName = currentRecordNumToNameToValueInHolder?.get(
                RecordNumToMapNameValueInHolderColumn.VARIABLE_NAME.name
            )
            val currentVariableValue = currentRecordNumToNameToValueInHolder?.get(
                RecordNumToMapNameValueInHolderColumn.VARIABLE_VALUE.name
            )
            val currentId = editTextStartId + currentOrder
            insertTextView.text = currentVariableName
            linearParams.weight = 1F
            insertTextView.layoutParams = linearParams
            linearLayout.addView(insertTextView)
            val insertEditText = EditText(context)
            insertEditText.tag = currentVariableName
            insertEditText.id = currentId
            insertEditText.backgroundTintList =
                context?.getColorStateList(R.color.gray_out)
            insertEditText.setSelectAllOnFocus(true)
            val currentRecordNum =
                currentRecordNumToMapNameValueInHolder.key
            editParameters.setVariableMap = recordNumToSetVariableMaps?.get(
                currentRecordNum
            )
            editParameters.currentId = currentId
            editParameters.currentVariableName = currentVariableName
            editParameters.currentVariableValue = currentVariableValue
            editParameters.setVariableMap = recordNumToSetVariableMaps?.get(
                currentRecordNum
            )
            val variableTypeList = editParameters.setVariableMap?.get(
                SetVariableTypeColumn.VARIABLE_TYPE.name
            )?.split(":")?.filter {
                val isContain = variableTypeDefineListForMiniEdit.contains(it)
                if (
                    isContain
                ) return@filter isContain
                LogSystems.stdWarn("Irregular option: ${it}")
                false
            } ?: emptyList()
            editParameters.variableTypeList = variableTypeList
            val horizontalLinearLayout = withEditComponentForFormJsDialog.insert(
                insertTextView,
                editParameters
            )
            linearLayout.addView(horizontalLinearLayout)
        }
    }
}
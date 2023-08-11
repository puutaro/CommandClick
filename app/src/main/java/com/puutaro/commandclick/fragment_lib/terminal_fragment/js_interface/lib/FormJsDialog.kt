package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib

import android.app.Dialog
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.LanguageTypeSelects
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.edit.*
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.edit.lib.SetVariableTyper
import com.puutaro.commandclick.proccess.edit.lib.ScriptContentsLister
import com.puutaro.commandclick.util.CommandClickVariables
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
                    if (!terminalViewModel.onDialog) break
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

        val setVariableTypeList = SetVariableTyper.makeSetVariableTypeList(
            recordNumToMapNameValueInSettingHolder,
            String(),
            String(),
            String(),
        )

        val recordNumToSetVariableMaps = SetVariableTyper.makeRecordNumToSetVariableMaps(
            setVariableTypeList,
            recordNumToMapNameValueInCommandHolder
        )

        val setReplaceVariableMap = SetReplaceVariabler.makeSetReplaceVariableMap(
            recordNumToMapNameValueInSettingHolder,
            String(),
            String(),
            String()
        )



        formDialog = Dialog(
            context
        )
        formDialog?.setContentView(
            com.puutaro.commandclick.R.layout.form_dialog_laytout
        )
        val confirmTitleTextView =
            formDialog?.findViewById<AppCompatTextView>(
                com.puutaro.commandclick.R.id.form_dialog_title
            )
        if(
            title.isNotEmpty()
        ) confirmTitleTextView?.text = title
        else confirmTitleTextView?.isVisible = false
        val linearLayout =
            formDialog?.findViewById<LinearLayout>(
                com.puutaro.commandclick.R.id.form_dialog_contents_linear
            ) ?: return
        val virtualReadPreffrenceMap = mapOf(
            SharePrefferenceSetting.current_app_dir.name
                to terminalFragment.currentAppDirPath
        )
        val editParameters = EditParameters(
            terminalFragment,
            virtualJsContentsList,
            recordNumToMapNameValueInCommandHolder,
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
                com.puutaro.commandclick.R.id.form_dialog_cancel
            )
        confirmCancelButton?.setOnClickListener {
            terminalViewModel.onDialog = false
            returnValue = String()
            formDialog?.dismiss()
        }
        val confirmOkButton =
            formDialog?.findViewById<AppCompatImageButton>(
                com.puutaro.commandclick.R.id.form_dialog_ok
            )
        confirmOkButton?.setOnClickListener {
            formDialog?.dismiss()
            val updateVirtualJsContentsList = if(
                recordNumToMapNameValueInCommandHolder.isNullOrEmpty()
            ) virtualJsContentsList
            else {
                ScriptContentsLister(
                    linearLayout
                ).update(
                    recordNumToMapNameValueInCommandHolder,
                    virtualJsContentsList,
                    exitTextStartId
                )
            }
            val dialogReturnStringListSource = CommandClickVariables.substituteVariableListFromHolder(
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
                .split("\t")
                .map{
                    "${CommandClickScriptVariable.SET_VARIABLE_TYPE}=\"${it}\""
                }.joinToString("\n")
        val settingSectionContents =
            "${settingSectionStart}\n${setVariableTypeSource}\n${settingSectionEnd}"
        val commandSectionContents =
            "${commandSectionStart}\t${formCommandVariables}\t${commandSectionEnd}"
                .replace("\t", "\n")
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
                variableTypeDefineListForMiniEdit.contains(it)
            } ?: emptyList()
            editParameters.variableTypeList = variableTypeList
            val horizontalLinearLayout = withEditComponentForFormJsDialog.insert(
                insertTextView,
                editParameters
            )
            linearLayout.addView(horizontalLinearLayout)
        }
    }

    private fun execInsertEditText(
        insertEditText: EditText,
        linearParams: LinearLayout.LayoutParams,
        currentVariableValue: String?,
        textType: EditTextType = EditTextType.PLAIN
    ): EditText {
        insertEditText.setText(currentVariableValue)
        insertEditText.layoutParams = linearParams
        when(textType){
            EditTextType.READ_ONLY -> {
                insertEditText.setEnabled(false)
                insertEditText.focusable = View.NOT_FOCUSABLE
            }
            EditTextType.PASSWORD -> insertEditText.inputType = (
                    InputType.TYPE_CLASS_TEXT or
                            InputType.TYPE_TEXT_VARIATION_PASSWORD
                    )
            else ->  insertEditText.inputType = InputType.TYPE_CLASS_TEXT
        }
        return insertEditText
    }
}
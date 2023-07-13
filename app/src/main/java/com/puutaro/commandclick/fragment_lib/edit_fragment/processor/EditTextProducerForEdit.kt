package com.puutaro.commandclick.fragment_lib.edit_fragment.processor

import android.R
import android.content.Context
import android.widget.*
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.common.variable.edit.*
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.*
import com.puutaro.commandclick.proccess.ScriptFileDescription
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.*
import com.puutaro.commandclick.proccess.edit.lib.ListSettingVariableListMaker
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.edit.lib.SetVariableTyper
import com.puutaro.commandclick.util.FragmentTagManager
import com.puutaro.commandclick.util.SharePreffrenceMethod
import com.puutaro.commandclick.view_model.activity.EditViewModel


class EditTextProducerForEdit(
    private val editFragment: EditFragment,
    readSharePreffernceMap: Map<String, String>,
    private val currentScriptContentsList: List<String>,
    private val recordNumToMapNameValueInCommandHolder: Map<Int, Map<String,String>?>?,
    private val recordNumToMapNameValueInSettingHolder: Map<Int, Map<String,String>?>?,
    enableCmdEdit: Boolean
) {
    private val binding = editFragment.binding
    private val context = editFragment.context
    private val editViewModel: EditViewModel by editFragment.activityViewModels()

    private val currentAppDirPath = SharePreffrenceMethod.getReadSharePreffernceMap(
        readSharePreffernceMap,
        SharePrefferenceSetting.current_app_dir
    )
    private val currentScriptFileName =SharePreffrenceMethod.getReadSharePreffernceMap(
        readSharePreffernceMap,
        SharePrefferenceSetting.current_script_file_name
    )
    val fannelDirName = currentScriptFileName
        .removeSuffix(UsePath.JS_FILE_SUFFIX)
        .removeSuffix(UsePath.SHELL_FILE_SUFFIX) +
            "Dir"


    private val setReplaceVariableMap =
        SetReplaceVariabler.makeSetReplaceVariableMap(
            recordNumToMapNameValueInSettingHolder,
            currentAppDirPath,
            fannelDirName,
            currentScriptFileName,
        )
    private val setVariableTypeList = SetVariableTyper.makeSetVariableTypeList(
        recordNumToMapNameValueInSettingHolder,
        currentAppDirPath,
        fannelDirName,
        currentScriptFileName,
    )

    private val recordNumToSetVariableMaps =
        SetVariableTyper.makeRecordNumToSetVariableMaps(
            setVariableTypeList,
            recordNumToMapNameValueInCommandHolder
        )

    private val readSharePreffernceMap =
        editFragment.readSharePreffernceMap


    private val hideSettingVariableList = makeHideVariableList()

    private val editParameters = EditParameters(
        editFragment,
        currentScriptContentsList,
        recordNumToMapNameValueInCommandHolder,
        readSharePreffernceMap,
        setReplaceVariableMap,
        false,
        hideSettingVariableList,
    )

    private val withEditComponent = WithEditComponent(
        editFragment,
        enableCmdEdit
    )

    private val withIndexListView = WithIndexListView(
        editFragment,
    )

    fun adds(
        onSettingEdit: Boolean = false
    ) {
        editViewModel.variableNameToEditTextIdMap.clear()
        if (onSettingEdit) {
            val setVariableListForSettingHolder =
                CommandClickScriptVariable.setVariableForSettingHolder
            val recordNumToSetVariableMapsForSettingHolder =
                SetVariableTyper.makeRecordNumToSetVariableMaps(
                    setVariableListForSettingHolder,
                    recordNumToMapNameValueInSettingHolder
                )

            execAdd(
                recordNumToMapNameValueInSettingHolder,
                recordNumToSetVariableMapsForSettingHolder,
                EditTextIdForEdit.SETTING_VARIABLE.id
            )
            binding.editLinearLayout.addView(
                makeDescriptionButton(
                    context,
                    currentScriptContentsList,
                    readSharePreffernceMap
                )
            )
            return
        }
        execAdd(
            recordNumToMapNameValueInCommandHolder,
            recordNumToSetVariableMaps,
            EditTextIdForEdit.COMMAND_VARIABLE.id
        )
        binding.editLinearLayout.addView(
            makeDescriptionButton(
                context,
                currentScriptContentsList,
                readSharePreffernceMap
            )
        )
    }

    private fun execAdd(
        recordNumToMapNameValueInHolder: Map<Int, Map<String, String>?>?,
        recordNumToSetVariableMaps: Map<Int, Map<String, String>?>?,
        editTextStartId: Int,
    ) {
        val recordNumToNameToValueInHolderSize = recordNumToMapNameValueInHolder?.size ?: return
        (1..recordNumToNameToValueInHolderSize).forEach {
                seedNum ->
            val currentOrder = seedNum - 1
            val currentRecordNumToMapNameValueInHolder =
                recordNumToMapNameValueInHolder.entries.elementAt(
                    currentOrder
                )
            val currentRecordNumToNameToValueInHolder =
                currentRecordNumToMapNameValueInHolder.value
            val insertTextView = TextView(context)
            val currentVariableName = currentRecordNumToNameToValueInHolder?.get(
                RecordNumToMapNameValueInHolderColumn.VARIABLE_NAME.name
            )
            if (
                currentVariableName.isNullOrEmpty()
            ) return
            insertTextView.text = currentVariableName
            binding.editLinearLayout.addView(insertTextView)
            val currentVariableValue = currentRecordNumToNameToValueInHolder.get(
                RecordNumToMapNameValueInHolderColumn.VARIABLE_VALUE.name
            )
            val currentId = editTextStartId + currentOrder
            val currentRecordNum =
                currentRecordNumToMapNameValueInHolder.key
            editParameters.currentId = currentId
            editParameters.currentVariableName = currentVariableName
            editParameters.currentVariableValue = currentVariableValue
            editParameters.setVariableMap = recordNumToSetVariableMaps?.get(
                currentRecordNum
            )
            val editTextSupportViewNameList = EditTextSupportViewName.values().map {
                it.str
            }
            val variableTypeList = editParameters.setVariableMap?.get(
                SetVariableTypeColumn.VARIABLE_TYPE.name
            )?.split(":")?.filter {
                editTextSupportViewNameList.contains(it)
            } ?: emptyList()
            editParameters.variableTypeList = variableTypeList
            val isListIndex =  variableTypeList.contains(
                EditTextSupportViewName.LIST_INDEX.str
            )
            when(isListIndex) {
                true -> {
                    val listIndexLayout = withIndexListView.create(
                        editParameters
                    )
                    binding.editListLinearLayout.addView(listIndexLayout)
                }
                else -> {
                    val horizontalLinearLayout = withEditComponent.insert(
                        insertTextView,
                        editParameters,
                    )
                    binding.editLinearLayout.addView(horizontalLinearLayout)
                }
            }
        }
    }

    private fun makeDescriptionButton(
        context: Context?,
        currentShellContentsList: List<String>,
        readSharePreffernceMap: Map<String, String>
    ): Button {
        val descriptionButton = Button(context)
        val buttonLabel = "Desctiption"
        descriptionButton.setText(buttonLabel)
        context?.let {
            descriptionButton.setTextColor(
                it.getColor(R.color.white)
            )
            descriptionButton.setBackgroundTintList(
                it.getColorStateList(com.puutaro.commandclick.R.color.black)
            )
        }
        descriptionButton.setOnClickListener { innerButtonView ->
            ScriptFileDescription.show(
                innerButtonView.context,
                currentShellContentsList,
                SharePreffrenceMethod.getReadSharePreffernceMap(
                    readSharePreffernceMap,
                    SharePrefferenceSetting.current_script_file_name
                )
            )
        }
        return descriptionButton
    }

    private fun makeHideVariableList(
    ): List<String>{
        return ListSettingVariableListMaker.make(
            CommandClickScriptVariable.HIDE_SETTING_VARIABLES,
            currentAppDirPath,
            currentScriptFileName,
            fannelDirName, currentScriptContentsList,
            editFragment.settingSectionStart,
            editFragment.settingSectionEnd,
        )
    }
}


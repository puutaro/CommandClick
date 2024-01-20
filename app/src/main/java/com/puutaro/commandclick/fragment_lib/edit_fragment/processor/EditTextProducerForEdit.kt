package com.puutaro.commandclick.fragment_lib.edit_fragment.processor

import android.R
import android.widget.*
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.edit.*
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.*
import com.puutaro.commandclick.proccess.ScriptFileDescription
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.*
import com.puutaro.commandclick.proccess.edit.lib.SetVariableTyper
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.file_tool.FDialogTempFile
import com.puutaro.commandclick.util.state.SharePreferenceMethod
import com.puutaro.commandclick.view_model.activity.EditViewModel


class EditTextProducerForEdit(
    private val editFragment: EditFragment,
    private val recordNumToMapNameValueInCommandHolder: Map<Int, Map<String,String>?>?,
    private val recordNumToMapNameValueInSettingHolder: Map<Int, Map<String,String>?>?,
    hideSettingVariableList: List<String>,
) {
    private val binding = editFragment.binding
    private val context = editFragment.context
    private val currentScriptContentsList = editFragment.currentScriptContentsList
    private val editViewModel: EditViewModel by editFragment.activityViewModels()

    private val readSharePreffernceMap = editFragment.readSharePreffernceMap
    private val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
        readSharePreffernceMap,
        SharePrefferenceSetting.current_app_dir
    )
    private val currentScriptFileName = SharePreferenceMethod.getReadSharePreffernceMap(
        readSharePreffernceMap,
        SharePrefferenceSetting.current_fannel_name
    )

    private val setReplaceVariableMap = editFragment.setReplaceVariableMap
    private val setVariableForSettingHolder = CommandClickScriptVariable.setVariableForSettingHolder
    private val setVariableTypeList = SetVariableTyper.makeSetVariableTypeList(
        recordNumToMapNameValueInSettingHolder,
        currentAppDirPath,
        currentScriptFileName,
    ).let {
        if(
            it.isNullOrEmpty()
        ) return@let setVariableForSettingHolder
        setVariableForSettingHolder + it
    }

    private val recordNumToSetVariableMaps =
        SetVariableTyper.makeRecordNumToSetVariableMaps(
            setVariableTypeList,
            recordNumToMapNameValueInCommandHolder
        )


//    private val hideSettingVariableList = makeHideVariableList()

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
    )

    private val withIndexListView = WithIndexListView(
        editFragment,
    )

    fun adds(
        onSettingEdit: Boolean = false
    ) {
        editViewModel.variableNameToEditTextIdMap.clear()
        editFragment.listConSelectBoxMapList.clear()
        when (onSettingEdit) {
            true -> {
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
            }
            false -> {
                execAdd(
                    recordNumToMapNameValueInCommandHolder,
                    recordNumToSetVariableMaps,
                    EditTextIdForEdit.COMMAND_VARIABLE.id
                )
            }
        }
        if (
            FDialogTempFile.howFDialogFile(currentScriptFileName)
        ) return
        binding.editLinearLayout.addView(
            makeDescriptionButton(
                editFragment,
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
                val isContain = editTextSupportViewNameList.contains(it)
                if (
                    isContain
                ) return@filter isContain
                LogSystems.stdWarn("Irregular option: ${it}")
                false
            } ?: emptyList()
            editParameters.variableTypeList = variableTypeList
            val isListIndex = variableTypeList.contains(
                EditTextSupportViewName.LIST_INDEX.str
            )
            when(isListIndex) {
                true -> {
                    withIndexListView.create(
                        editParameters
                    )
                }
                else -> {
                    withEditComponent.insert(
                        insertTextView,
                        editParameters,
                    ).let {
                        binding.editLinearLayout.addView(it)
                    }
                }
            }
        }
    }

    private fun makeDescriptionButton(
        editFragment: EditFragment,
    ): Button {
        val context = editFragment.context
        val readSharePreffernceMap = editFragment.readSharePreffernceMap
        val descriptionButton = Button(context)
        val buttonLabel = "Description"
        descriptionButton.text = buttonLabel
        val linearParamsForButton = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT,
        )
        descriptionButton.layoutParams = linearParamsForButton
        context?.let {
            descriptionButton.setTextColor(
                it.getColor(R.color.white)
            )
            descriptionButton.backgroundTintList =
                it.getColorStateList(
                    com.puutaro.commandclick.R.color.terminal_color
                )
        }
        descriptionButton.setOnClickListener { innerButtonView ->
            ScriptFileDescription.show(
                editFragment,
                editFragment.currentScriptContentsList,
                currentAppDirPath,
                SharePreferenceMethod.getReadSharePreffernceMap(
                    readSharePreffernceMap,
                    SharePrefferenceSetting.current_fannel_name
                )
            )
        }
        return descriptionButton
    }
}


package com.puutaro.commandclick.fragment_lib.edit_fragment.processor

import android.R
import android.widget.*
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.edit.*
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.*
import com.puutaro.commandclick.proccess.ScriptFileDescription
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.*
import com.puutaro.commandclick.proccess.edit.lib.SetVariableTyper
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.state.SharePrefTool
import com.puutaro.commandclick.view_model.activity.EditViewModel
import java.io.File


class EditTextProducerForEdit(
    private val editFragment: EditFragment,
) {
    private val binding = editFragment.binding
    private val context = editFragment.context
    private val currentScriptContentsList = editFragment.currentFannelConList
    private val editViewModel: EditViewModel by editFragment.activityViewModels()

    private val readSharePreffernceMap = editFragment.readSharePreferenceMap
    private val currentAppDirPath = SharePrefTool.getCurrentAppDirPath(
        readSharePreffernceMap
    )

    private val setReplaceVariableMap = editFragment.setReplaceVariableMap
    private val descriptionHidValName = "description"


    private val editParameters = EditParameters(
        editFragment,
        currentScriptContentsList,
        editFragment.recordNumToMapNameValueInCommandHolder,
        editFragment.recordNumToMapNameValueInSettingHolder,
        readSharePreffernceMap,
        setReplaceVariableMap,
        false,
        editFragment.hideSettingVariableList,
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
            true -> execAddEditComponent(
                editFragment.recordNumToMapNameValueInSettingHolder,
                EditTextIdForEdit.SETTING_VARIABLE.id
            )
            false -> execAddEditComponent(
                editFragment.recordNumToMapNameValueInCommandHolder,
                EditTextIdForEdit.COMMAND_VARIABLE.id
            )
        }
        val disableDesc =
            editFragment.hideSettingVariableList
                .contains(descriptionHidValName)
        if(disableDesc) return
        binding.editLinearLayout.addView(
            makeDescriptionButton(
                editFragment,
            )
        )
    }

    private fun execAddEditComponent(
        recordNumToMapNameValueInCommandOrSettingHolder:  Map<Int, Map<String, String>?>?,
        editTextStartId: Int,
    ){
        val recordNumToSetVariableMaps =
            SetVariableTyper.makeRecordNumToSetVariableMaps(
                editFragment,
                editFragment.setVariableTypeList,
                recordNumToMapNameValueInCommandOrSettingHolder,
                editFragment.setReplaceVariableMap,
            )
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "setValMap.txt").absolutePath,
//            listOf(
//                "setVariableTypeList: ${editFragment.setVariableTypeList}",
//                "recordNumToSetVariableMaps: ${recordNumToSetVariableMaps}",
//            ).joinToString("\n\n\n")
//        )
        execAdd(
            recordNumToMapNameValueInCommandOrSettingHolder,
            recordNumToSetVariableMaps,
            editTextStartId
        )
    }

    private fun execAdd(
        recordNumToMapNameValueInHolder: Map<Int, Map<String, String>?>?,
        recordNumToSetVariableMaps: Map<Int, Map<String, String>?>?,
        editTextStartId: Int,
    ) {
        val existIndexList = editFragment.existIndexList
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
            if(existIndexList){
                setListIndexLayoutComponent(
                    recordNumToSetVariableMaps,
                    currentRecordNum,
                    insertTextView,
                )
                return@forEach
            }
            if(editFragment.existIndexList) return@forEach
            withEditComponent.insert(
                insertTextView,
                editParameters,
            ).let {
                binding.editLinearLayout.addView(it)
            }
        }
    }

    private fun setListIndexLayoutComponent(
        recordNumToSetVariableMaps: Map<Int, Map<String, String>?>?,
        currentRecordNum: Int,
        insertTextView: TextView,
    ){
        val listIndexOrder = recordNumToSetVariableMaps?.filter {
            val setValTypeEl = it.value
            val variableType = setValTypeEl?.get(SetVariableTypeColumn.VARIABLE_TYPE.name)
            variableType?.contains(
                EditTextSupportViewName.LIST_INDEX.str
            ) ?: false
        }?.keys?.firstOrNull() ?: 0
        when(true){
            (currentRecordNum < listIndexOrder) ->
                withEditComponent.insert(
                    insertTextView,
                    editParameters,
                ).let {
                    binding.editListInnerTopLinearLayout.addView(it)
                }
            (currentRecordNum == listIndexOrder) ->
                withIndexListView.create(
                    editParameters
                )
            else ->
                withEditComponent.insert(
                    insertTextView,
                    editParameters,
                ).let {
                    binding.editListInnerBottomLinearLayout.addView(it)
                }
        }

    }


    private fun makeDescriptionButton(
        editFragment: EditFragment,
    ): Button {
        val context = editFragment.context
        val readSharePreffernceMap = editFragment.readSharePreferenceMap
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
                editFragment.currentFannelConList,
                currentAppDirPath,
                SharePrefTool.getCurrentFannelName(
                    readSharePreffernceMap
                )
            )
        }
        return descriptionButton
    }
}


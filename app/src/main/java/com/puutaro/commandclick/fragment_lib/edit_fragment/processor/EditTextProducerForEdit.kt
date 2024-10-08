package com.puutaro.commandclick.fragment_lib.edit_fragment.processor

import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.edit.*
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.*
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.*
import com.puutaro.commandclick.view_model.activity.EditViewModel

object EditTextProducerForEdit {

//    private val idDuration = 1000

    fun adds(
        editFragment: EditFragment,
//        onSettingEdit: Boolean = false
    ) {
        val binding = editFragment.binding
        val currentScriptContentsList = editFragment.currentFannelConList
        val editViewModel: EditViewModel by editFragment.activityViewModels()

        val fannelInfoMap = editFragment.fannelInfoMap

        val setReplaceVariableMap = editFragment.setReplaceVariableMap
//        val descriptionHidValName = "description"


        val editParameters = EditParameters(
//        editFragment,
            currentScriptContentsList,
//            editFragment.recordNumToMapNameValueInCommandHolder,
//            editFragment.recordNumToMapNameValueInSettingHolder,
            fannelInfoMap,
            setReplaceVariableMap,
            false,
//            editFragment.hideSettingVariableList,
        )

        editViewModel.variableNameToEditTextIdMap.clear()
        editFragment.listConSelectBoxMapList.clear()
        execAddEditComponent(
            editFragment,
            editParameters,
//            editFragment.recordNumToMapNameValueInCommandHolder,
            EditTextIdForEdit.COMMAND_VARIABLE.id,
        )
//        when (onSettingEdit) {
//            true -> execAddEditComponent(
//                editFragment,
//                editParameters,
//                editFragment.recordNumToMapNameValueInSettingHolder,
//                EditTextIdForEdit.SETTING_VARIABLE.id,
//            )
//            false -> execAddEditComponent(
//                editFragment,
//                editParameters,
//                editFragment.recordNumToMapNameValueInCommandHolder,
//                EditTextIdForEdit.COMMAND_VARIABLE.id,
//            )
//        }
//        val disableDesc =
//            editFragment.hideSettingVariableList
//                .contains(descriptionHidValName)
//        if(disableDesc) return
//        binding.editLinearLayout.addView(
//            makeDescriptionButton(
//                editFragment,
//            )
//        )
    }

    private fun execAddEditComponent(
        editFragment: EditFragment,
        editParameters: EditParameters,
//        recordNumToMapNameValueInCommandOrSettingHolder:  Map<Int, Map<String, String>?>?,
        editTextStartId: Int,
    ){
//        val recordNumToSetVariableMaps =
//            SetVariableTyper.makeRecordNumToSetVariableMaps(
//                editFragment,
//                editFragment.setVariableTypeList,
//                recordNumToMapNameValueInCommandOrSettingHolder,
//                editFragment.setReplaceVariableMap,
//                editFragment.busyboxExecutor,
//            )
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "setValMap.txt").absolutePath,
//            listOf(
//                "setVariableTypeList: ${editFragment.setVariableTypeList}",
//                "recordNumToSetVariableMaps: ${recordNumToSetVariableMaps}",
//            ).joinToString("\n\n\n")
//        )
        execAdd(
            editFragment,
            editParameters,
//            recordNumToMapNameValueInCommandOrSettingHolder,
//            recordNumToSetVariableMaps,
            editTextStartId
        )
    }

    private fun execAdd(
        editFragment: EditFragment,
        editParameters: EditParameters,
//        recordNumToMapNameValueInHolder: Map<Int, Map<String, String>?>?,
//        recordNumToSetVariableMaps: Map<Int, Map<String, String>?>?,
        editTextStartId: Int,
    ) {
//        val context = editFragment.context
//        val binding = editFragment.binding
//        val existIndexList = editFragment.existIndexList
//        val recordNumToNameToValueInHolderSize = recordNumToMapNameValueInHolder?.size ?: return
        setListIndexLayoutComponent(
            editFragment,
        )
//        (1..recordNumToNameToValueInHolderSize).forEach {
//                seedNum ->
//            val currentOrder = seedNum - 1
//            val currentRecordNumToMapNameValueInHolder =
//                recordNumToMapNameValueInHolder.entries.elementAt(
//                    currentOrder
//                )
//            val currentRecordNumToNameToValueInHolder =
//                currentRecordNumToMapNameValueInHolder.value
////            val insertTextView = TextView(context)
//            val currentVariableName = currentRecordNumToNameToValueInHolder?.get(
//                RecordNumToMapNameValueInHolderColumn.VARIABLE_NAME.name
//            )
//            if (
//                currentVariableName.isNullOrEmpty()
//            ) return
////            insertTextView.text = currentVariableName
////            binding.editLinearLayout.addView(insertTextView)
//            val currentVariableValue = currentRecordNumToNameToValueInHolder.get(
//                RecordNumToMapNameValueInHolderColumn.VARIABLE_VALUE.name
//            )
//            val currentId = editTextStartId + currentOrder
//            val currentRecordNum =
//                currentRecordNumToMapNameValueInHolder.key
////            editParameters.currentId = currentId
////            editParameters.currentVariableName = currentVariableName
////            editParameters.currentVariableValue = currentVariableValue
////            editParameters.setVariableMap = recordNumToSetVariableMaps?.get(
////                currentRecordNum
////            )
////            val editTextSupportViewNameList = EditTextSupportViewName.values().map {
////                it.str
////            }
////            val variableTypeList = editParameters.setVariableMap?.get(
////                SetVariableTypeColumn.VARIABLE_TYPE.name
////            )?.split(":")?.filter {
////                val isContain = editTextSupportViewNameList.contains(it)
////                if (
////                    isContain
////                ) return@filter isContain
////                LogSystems.stdWarn("Irregular option: ${it}")
////                false
////            } ?: emptyList()
////            editParameters.variableTypeList = variableTypeList
//            setListIndexLayoutComponent(
//                editFragment,
//                editParameters,
////                recordNumToSetVariableMaps,
////                currentRecordNum,
////                insertTextView,
//            )
////            if(existIndexList){
////                setListIndexLayoutComponent(
////                    editFragment,
////                    editParameters,
////                    recordNumToSetVariableMaps,
////                    currentRecordNum,
////                    insertTextView,
////                )
////                return@forEach
////            }
////            if(editFragment.existIndexList) return@forEach
////            WithEditComponent.insert(
////                editFragment,
////                insertTextView,
////                editParameters,
////            ).let {
////                binding.editLinearLayout.addView(it)
////            }
//        }
    }

    private fun setListIndexLayoutComponent(
        editFragment: EditFragment,
    ){
        val binding = editFragment.binding
        WithEditComponentListView.create(
            editFragment,
            editFragment.fannelInfoMap,
            editFragment.setReplaceVariableMap,
            editFragment.busyboxExecutor,
            editFragment.listIndexConfigMap,
            binding.editTextView,
            binding.editTitleImage,
            binding.editListRecyclerView,
            binding.editListBkFrame,
            binding.editListSearchEditText,
            binding.editFooterLinearlayout,
            editFragment.mainFannelConList,
        )
    }


//    private fun makeDescriptionButton(
//        editFragment: EditFragment,
//    ): Button {
//        val context = editFragment.context
//        val fannelInfoMap = editFragment.fannelInfoMap
//        val descriptionButton = Button(context)
//        val buttonLabel = "Description"
//        descriptionButton.text = buttonLabel
//        val linearParamsForButton = LinearLayoutCompat.LayoutParams(
//            LinearLayoutCompat.LayoutParams.MATCH_PARENT,
//            LinearLayoutCompat.LayoutParams.MATCH_PARENT,
//        )
//        descriptionButton.layoutParams = linearParamsForButton
//        context?.let {
//            descriptionButton.setTextColor(
//                it.getColor(R.color.white)
//            )
//            descriptionButton.backgroundTintList =
//                it.getColorStateList(
//                    com.puutaro.commandclick.R.color.terminal_color
//                )
//        }
//        descriptionButton.setOnClickListener { innerButtonView ->
//            ScriptFileDescription.show(
//                editFragment,
//                editFragment.currentFannelConList,
////                currentAppDirPath,
//                FannelInfoTool.getCurrentFannelName(
//                    fannelInfoMap
//                )
//            )
//        }
//        return descriptionButton
//    }
}


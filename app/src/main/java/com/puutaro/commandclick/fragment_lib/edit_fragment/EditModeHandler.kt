package com.puutaro.commandclick.fragment_lib.edit_fragment

import android.widget.Toast
import com.puutaro.commandclick.common.variable.edit.RecordNumToMapNameValueInHolderColumn
import com.puutaro.commandclick.databinding.EditFragmentBinding
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.IsCmdEdit
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.UpdateLastModifiedForAppHistory
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.EditTextProducerForEdit
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.ToolbarButtonProducerForEdit
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.ToolbarButtonBariantForEdit
import com.puutaro.commandclick.util.*
import kotlinx.coroutines.*


class EditModeHandler(
    private val editFragment: EditFragment,
    binding: EditFragmentBinding,
) {
    private val context = editFragment.context
    private val currentScriptContentsList = editFragment.currentScriptContentsList
    private val enableCmdEdit = editFragment.enableCmdEdit
    private val readSharePreferenceMap = editFragment.readSharePreferenceMap

    private val editExecuteValue = CommandClickVariables.returnEditExecuteValueStr(
        currentScriptContentsList,
        editFragment.languageType
    )

    private val toolbarButtonProducerForEdit = ToolbarButtonProducerForEdit(
        binding,
        editFragment,
    )
    val settingSectionStart = editFragment.settingSectionStart
    val settingSectionEnd = editFragment.settingSectionEnd

    private val commandSectionStart = editFragment.commandSectionStart
    private val commandSectionEnd = editFragment.commandSectionEnd


    fun execByHowFullEdit(){
        when(
            IsCmdEdit.judge(editFragment)
        ) {
            false -> editSettingVariable()
            else -> editCommandVariable()
        }
    }

    private fun editCommandVariable(
    ) {
        val recordNumToMapNameValueInCommandHolderSrc = RecordNumToMapNameValueInHolder.parse(
            currentScriptContentsList,
            commandSectionStart,
            commandSectionEnd
        )
        val recordNumToMapNameValueInCommandHolder = filterRecordNumToMapNameValueInHolderByHideVariable(
            recordNumToMapNameValueInCommandHolderSrc
        )

        val recordNumToMapNameValueInSettingHolder =
            RecordNumToMapNameValueInHolder.parse(
                currentScriptContentsList,
                settingSectionStart,
                settingSectionEnd,
                true,
            )
        if(
            recordNumToMapNameValueInCommandHolder.isNullOrEmpty()
        ) return
        UpdateLastModifiedForAppHistory.update(
            editExecuteValue,
            readSharePreferenceMap,
        )
        buttonCreate(
            ToolbarButtonBariantForEdit.HISTORY,
            null,
            null,
        )

        buttonCreate(
            ToolbarButtonBariantForEdit.OK,
            recordNumToMapNameValueInCommandHolder,
            recordNumToMapNameValueInSettingHolder,
        )
        val editTextProducerForEdit = EditTextProducerForEdit(
            editFragment,
            recordNumToMapNameValueInCommandHolder,
            recordNumToMapNameValueInSettingHolder,
        )
        editTextProducerForEdit.adds()

        buttonCreate(
            ToolbarButtonBariantForEdit.EDIT,
            recordNumToMapNameValueInCommandHolder,
            recordNumToMapNameValueInSettingHolder,
        )
        buttonCreate(
            ToolbarButtonBariantForEdit.SETTING,
            recordNumToMapNameValueInCommandHolder,
            recordNumToMapNameValueInSettingHolder,
        )
    }

    private fun editSettingVariable(
    ) {
        val recordNumToMapNameValueInCommandHolder =
            RecordNumToMapNameValueInHolder.parse(
                currentScriptContentsList,
                commandSectionStart,
                commandSectionEnd
            )
        val recordNumToMapNameValueInSettingHolderSrc =
            RecordNumToMapNameValueInHolder.parse(
                currentScriptContentsList,
                settingSectionStart,
                settingSectionEnd,
                true,
            )
        val recordNumToMapNameValueInSettingHolder = filterRecordNumToMapNameValueInHolderByHideVariable(
            recordNumToMapNameValueInSettingHolderSrc
        )
        if(
            recordNumToMapNameValueInCommandHolder.isNullOrEmpty()
            && recordNumToMapNameValueInSettingHolder.isNullOrEmpty()
        ) {
            editFragment.popBackStackToIndexImmediateJob?.cancel()
            editFragment.popBackStackToIndexImmediateJob = CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.IO){
                    delay(200)
                }
                withContext(Dispatchers.Main){
                    Toast.makeText(
                        context,
                        "No editable variable therefore, go back",
                        Toast.LENGTH_LONG
                    ).show()
                    val listener = context as? EditFragment.onToolBarButtonClickListenerForEditFragment
                    listener?.onToolBarButtonClickForEditFragment(
                        editFragment.tag,
                        ToolbarButtonBariantForEdit.CANCEL,
                        readSharePreferenceMap,
                        enableCmdEdit,
                    )
                }
            }
            return
        }
        buttonCreate(
            ToolbarButtonBariantForEdit.OK,
            recordNumToMapNameValueInCommandHolder,
            recordNumToMapNameValueInSettingHolder,
        )
        val editTextProducerForEdit = EditTextProducerForEdit(
            editFragment,
            recordNumToMapNameValueInCommandHolder,
            recordNumToMapNameValueInSettingHolder,
        )
        editTextProducerForEdit.adds(
        true
        )
    }

    private fun buttonCreate(
        toolbarButtonVariantForEdit: ToolbarButtonBariantForEdit,
        recordNumToMapNameValueInCommandHolder: Map<Int, Map<String, String>?>?,
        recordNumToMapNameValueInSettingHolder: Map<Int, Map<String, String>?>?,
    ){
        toolbarButtonProducerForEdit.make(
            toolbarButtonVariantForEdit,
            recordNumToMapNameValueInCommandHolder,
            recordNumToMapNameValueInSettingHolder,
        )
    }

    private fun filterRecordNumToMapNameValueInHolderByHideVariable(
        recordNumToMapNameValueInHolder: Map<Int, Map<String, String>?>?,
    ): Map<Int, Map<String, String>?>? {
        val hideSettingVariableList = editFragment.hideSettingVariableList
        return recordNumToMapNameValueInHolder?.filter {
                currentRecordNumToMapNameValueInHolder ->
            val currentRecordNumToNameToValueInHolder =
                currentRecordNumToMapNameValueInHolder.value
            val currentVariableName = currentRecordNumToNameToValueInHolder?.get(
                RecordNumToMapNameValueInHolderColumn.VARIABLE_NAME.name
            )
            if (
                currentVariableName.isNullOrEmpty()
            ) return@filter false
            !hideSettingVariableList.contains(
                currentVariableName
            )
        }
    }
}

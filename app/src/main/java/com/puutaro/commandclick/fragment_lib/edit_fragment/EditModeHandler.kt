package com.puutaro.commandclick.fragment_lib.edit_fragment

import android.widget.Toast
import com.puutaro.commandclick.common.variable.edit.RecordNumToMapNameValueInHolderColumn
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.databinding.EditFragmentBinding
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.UpdateLastModifiedForAppHistory
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.EditTextProducerForEdit
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.ToolbarButtonProducerForEdit
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.ToolbarButtonBariantForEdit
import com.puutaro.commandclick.proccess.edit.lib.ListSettingVariableListMaker
import com.puutaro.commandclick.util.*
import com.puutaro.commandclick.util.state.FragmentTagManager
import com.puutaro.commandclick.util.state.SharePreferenceMethod
import kotlinx.coroutines.*


class EditModeHandler(
    private val editFragment: EditFragment,
    binding: EditFragmentBinding,
) {
    private val context = editFragment.context
    private val currentScriptContentsList = editFragment.currentScriptContentsList
    private val currentEditFragmentTag = editFragment.tag
    private val onPassCmdVariableEdit =
        editFragment.passCmdVariableEdit ==
                CommandClickScriptVariable.PASS_CMDVARIABLE_EDIT_ON_VALUE
    private val enableCmdEdit = editFragment.enableCmdEdit
    private val readSharePreffernceMap = editFragment.readSharePreffernceMap
    private val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
        readSharePreffernceMap,
        SharePrefferenceSetting.current_app_dir
    )
    private val currentFannelName = SharePreferenceMethod.getReadSharePreffernceMap(
        readSharePreffernceMap,
        SharePrefferenceSetting.current_fannel_name
    )

    private val hideSettingVariableList = makeHideVariableList()

    private val editExecuteValue = CommandClickVariables.returnEditExecuteValueStr(
        currentScriptContentsList,
        editFragment.languageType
    )

    private val toolbarButtonProducerForEdit = ToolbarButtonProducerForEdit(
        binding,
        editFragment,
        readSharePreffernceMap,
    )
    val settingSectionStart = editFragment.settingSectionStart
    val settingSectionEnd = editFragment.settingSectionEnd

    private val commandSectionStart = editFragment.commandSectionStart
    private val commandSectionEnd = editFragment.commandSectionEnd


    fun execByHowFullEdit(){
        if(
            currentEditFragmentTag?.startsWith(
                FragmentTagManager.Prefix.SETTING_EDIT_PREFIX.str
            ) != true
            && !onPassCmdVariableEdit
        ) {
            editCommandVariable()
        }
        else editSettingVariable()
    }

    private fun editCommandVariable(
    ) {
        val languageTypeToSectionHolderMap =
            CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP
                .get(editFragment.languageType)
        val recordNumToMapNameValueInCommandHolderSrc = RecordNumToMapNameValueInHolder.parse(
            currentScriptContentsList,
            languageTypeToSectionHolderMap?.get(
                CommandClickScriptVariable.HolderTypeName.CMD_SEC_START
            ) as String,
            languageTypeToSectionHolderMap[
                    CommandClickScriptVariable.HolderTypeName.CMD_SEC_END
            ] as String,
        )
        val recordNumToMapNameValueInCommandHolder = filterRecordNumToMapNameValueInHolderByHideVariable(
            recordNumToMapNameValueInCommandHolderSrc
        )

        val recordNumToMapNameValueInSettingHolder =
            RecordNumToMapNameValueInHolder.parse(
                currentScriptContentsList,
                languageTypeToSectionHolderMap.get(
                    CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
                ) as String,
                languageTypeToSectionHolderMap[
                        CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
                ] as String,
                true,
                currentFannelName
            )

        if(
            recordNumToMapNameValueInCommandHolder.isNullOrEmpty()
        ) return
        UpdateLastModifiedForAppHistory.update(
            editExecuteValue,
            readSharePreffernceMap,
        )
        buttonCreate(
            ToolbarButtonBariantForEdit.HISTORY,
        )

        buttonCreate(
            ToolbarButtonBariantForEdit.OK,
            recordNumToMapNameValueInCommandHolder=recordNumToMapNameValueInCommandHolder,
        )
        val editTextProducerForEdit = EditTextProducerForEdit(
            editFragment,
            recordNumToMapNameValueInCommandHolder,
            recordNumToMapNameValueInSettingHolder,
            hideSettingVariableList,
        )
        editTextProducerForEdit.adds()

        buttonCreate(
            ToolbarButtonBariantForEdit.EDIT,
            recordNumToMapNameValueInCommandHolder=recordNumToMapNameValueInCommandHolder,
        )
        buttonCreate(
            ToolbarButtonBariantForEdit.SETTING,
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
                currentFannelName
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
                        readSharePreffernceMap,
                        enableCmdEdit,
                    )
                }
            }
            return
        }
//        buttonCreate(
//            ToolbarButtonBariantForEdit.HISTORY,
//            1f,
//        )

        buttonCreate(
            ToolbarButtonBariantForEdit.OK,
            recordNumToMapNameValueInCommandHolder,
            recordNumToMapNameValueInSettingHolder,
        )
        val editTextProducerForEdit = EditTextProducerForEdit(
            editFragment,
            recordNumToMapNameValueInCommandHolder,
            recordNumToMapNameValueInSettingHolder,
            hideSettingVariableList,
        )
        editTextProducerForEdit.adds(
            true
        )
    }

    private fun buttonCreate(
        toolbarButtonVariantForEdit: ToolbarButtonBariantForEdit,
        recordNumToMapNameValueInCommandHolder: Map<Int, Map<String, String>?>? = null,
        recordNumToMapNameValueInSettingHolder: Map<Int, Map<String, String>?>? = null,
    ){
        toolbarButtonProducerForEdit.make(
            toolbarButtonVariantForEdit,
            recordNumToMapNameValueInCommandHolder,
            recordNumToMapNameValueInSettingHolder,
        )
    }

    private fun makeHideVariableList(): List<String>{
        return ListSettingVariableListMaker.make(
            CommandClickScriptVariable.HIDE_SETTING_VARIABLES,
            currentAppDirPath,
            currentFannelName,
            currentScriptContentsList,
            editFragment.settingSectionStart,
            editFragment.settingSectionEnd,
        )
    }

    private fun filterRecordNumToMapNameValueInHolderByHideVariable(
        recordNumToMapNameValueInHolder: Map<Int, Map<String, String>?>?,
    ): Map<Int, Map<String, String>?>? {
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

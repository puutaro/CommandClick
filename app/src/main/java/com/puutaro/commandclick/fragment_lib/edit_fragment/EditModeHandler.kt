package com.puutaro.commandclick.fragment_lib.edit_fragment

import android.widget.Toast
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.databinding.EditFragmentBinding
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.UpdateLastModifiedForAppHistory
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.EditTextProducerForEdit
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.ToolbarButtonProducerForEdit
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.ToolbarButtonBariantForEdit
import com.puutaro.commandclick.util.*
import com.puutaro.commandclick.util.state.FragmentTagManager
import com.puutaro.commandclick.util.state.SharePreferenceMethod
import kotlinx.coroutines.*


class EditModeHandler(
    private val editFragment: EditFragment,
    binding: EditFragmentBinding,
    private val currentScriptContentsList: List<String>
) {
    private val context = editFragment.context
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

    private val editExecuteValue = CommandClickVariables.returnEditExecuteValueStr(
        currentScriptContentsList,
        editFragment.languageType
    )

    private val enableEditExecute = editFragment.enableEditExecute

    private val toolbarButtonProducerForEdit = ToolbarButtonProducerForEdit(
        binding,
        editFragment,
        currentScriptContentsList,
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
        val recordNumToMapNameValueInCommandHolder =
            RecordNumToMapNameValueInHolder.parse(
                currentScriptContentsList,
                languageTypeToSectionHolderMap?.get(
                    CommandClickScriptVariable.HolderTypeName.CMD_SEC_START
                ) as String,
                languageTypeToSectionHolderMap[
                        CommandClickScriptVariable.HolderTypeName.CMD_SEC_END
                ] as String,
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
        val buttonWeight = culcButtonWeight()
        buttonCreate(
            ToolbarButtonBariantForEdit.HISTORY,
            buttonWeight
        )

        buttonCreate(
            ToolbarButtonBariantForEdit.OK,
            buttonWeight,
            recordNumToMapNameValueInCommandHolder=recordNumToMapNameValueInCommandHolder,
            shellContentsList=currentScriptContentsList,
            setDrawble = setDrawableForOk(),
        )
        val editTextProducerForEdit = EditTextProducerForEdit(
            editFragment,
            readSharePreffernceMap,
            currentScriptContentsList,
            recordNumToMapNameValueInCommandHolder,
            recordNumToMapNameValueInSettingHolder,
        )
        editTextProducerForEdit.adds()

        buttonCreate(
            ToolbarButtonBariantForEdit.EDIT,
            buttonWeight,
            recordNumToMapNameValueInCommandHolder=recordNumToMapNameValueInCommandHolder,
            shellContentsList=currentScriptContentsList,
        )
        buttonCreate(
            ToolbarButtonBariantForEdit.SETTING,
            buttonWeight,
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
        val recordNumToMapNameValueInSettingHolder =
            RecordNumToMapNameValueInHolder.parse(
                currentScriptContentsList,
                settingSectionStart,
                settingSectionEnd,
                true,
                currentFannelName
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
        val settingWeight = 0.25F
        buttonCreate(
            ToolbarButtonBariantForEdit.HISTORY,
            1f,
        )

        buttonCreate(
            ToolbarButtonBariantForEdit.OK,
            settingWeight,
            recordNumToMapNameValueInCommandHolder,
            recordNumToMapNameValueInSettingHolder,
            currentScriptContentsList,
        )
        val editTextProducerForEdit = EditTextProducerForEdit(
            editFragment,
            readSharePreffernceMap,
            currentScriptContentsList,
            recordNumToMapNameValueInCommandHolder,
            recordNumToMapNameValueInSettingHolder,
        )
        editTextProducerForEdit.adds(
            true
        )
        buttonCreate(
            ToolbarButtonBariantForEdit.EDIT,
            settingWeight,
            recordNumToMapNameValueInCommandHolder=recordNumToMapNameValueInCommandHolder,
        )
        buttonCreate(
            ToolbarButtonBariantForEdit.SETTING,
            settingWeight,
        )
    }

    private fun buttonCreate(
        toolbarButtonVariantForEdit: ToolbarButtonBariantForEdit,
        buttonWeight: Float,
        recordNumToMapNameValueInCommandHolder: Map<Int, Map<String, String>?>? = null,
        recordNumToMapNameValueInSettingHolder: Map<Int, Map<String, String>?>? = null,
        shellContentsList: List<String> = listOf(),
        setDrawble: Int? = null
    ){
        toolbarButtonProducerForEdit.make(
            toolbarButtonVariantForEdit,
            buttonWeight,
            recordNumToMapNameValueInCommandHolder,
            recordNumToMapNameValueInSettingHolder,
            shellContentsList,
            setDrawble
        )
    }


    private fun setDrawableForOk()
            :Int? {
        return if(
            enableCmdEdit
            && enableEditExecute
        ){
            R.drawable.icons_play
        } else {
            null
        }
    }

    private fun culcButtonWeight(): Float {
        return listOf(
            editFragment.onDisableHistoryButton,
            editFragment.onDisableEditButton,
            editFragment.onDisablePlayButton,
            editFragment.onDisableSettingButton,
        ).filter {
            !it
        }.size.let { 1.0F / it }
    }
}

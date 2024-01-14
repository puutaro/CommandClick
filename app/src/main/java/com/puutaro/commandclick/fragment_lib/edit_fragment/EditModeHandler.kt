package com.puutaro.commandclick.fragment_lib.edit_fragment

import android.widget.Toast
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.databinding.EditFragmentBinding
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.ButtonViewHowActive
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
    private val enableCmdEdit = currentEditFragmentTag?.startsWith(
        FragmentTagManager.Prefix.cmdEditPrefix.str
    ) == true
    private val onDisableSettingButton =
        editFragment.disableSettingButton ==
                SettingVariableSelects.disableSettingButtonSelects.ON.name
    private val onDisableEditButton =
        editFragment.disableEditButton ==
                SettingVariableSelects.disableEditButtonSelects.ON.name
    private val onDisablePlayButton =
        editFragment.disablePlayButton ==
                SettingVariableSelects.disablePlayButtonSelects.ON.name
    private val readSharePreffernceMap = editFragment.readSharePreffernceMap
    private val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
        readSharePreffernceMap,
        SharePrefferenceSetting.current_app_dir
    )
    private val currentShellFileName = SharePreferenceMethod.getReadSharePreffernceMap(
        readSharePreffernceMap,
        SharePrefferenceSetting.current_fannel_name
    )

    private val editExecuteValue = CommandClickVariables.returnEditExecuteValueStr(
        currentScriptContentsList,
        editFragment.languageType
    )

    private val onShortcut = SharePreferenceMethod.getReadSharePreffernceMap(
        readSharePreffernceMap,
        SharePrefferenceSetting.on_shortcut
    ) == FragmentTagManager.Suffix.ON.name

    private val enableEditExecute =
        (
                editExecuteValue ==
                        SettingVariableSelects.EditExecuteSelects.ALWAYS.name
                && onShortcut
                )

    private val toolbarButtonProducerForEdit = ToolbarButtonProducerForEdit(
        binding,
        editFragment,
        currentScriptContentsList,
        readSharePreffernceMap,
        enableCmdEdit,
    )
    private val buttonViewHowActive = ButtonViewHowActive(
        binding,
        editFragment
    )
    val settingSectionStart = editFragment.settingSectionStart
    val settingSectionEnd = editFragment.settingSectionEnd

    private val commandSectionStart = editFragment.commandSectionStart
    private val commandSectionEnd = editFragment.commandSectionEnd


    fun execByHowFullEdit(){
        if(
            currentEditFragmentTag?.startsWith(
                FragmentTagManager.Prefix.settingEditPrefix.str
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
                currentShellFileName
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
            editExecuteValue=editExecuteValue,
            setDrawble = setDrawbleForOk(),
            howActive = !onDisablePlayButton
        )
        val editTextProducerForEdit = EditTextProducerForEdit(
            editFragment,
            readSharePreffernceMap,
            currentScriptContentsList,
            recordNumToMapNameValueInCommandHolder,
            recordNumToMapNameValueInSettingHolder,
            enableCmdEdit
        )
        editTextProducerForEdit.adds()

        val onEditButton = !onPassCmdVariableEdit
        buttonCreate(
            ToolbarButtonBariantForEdit.EDIT,
            buttonWeight,
            recordNumToMapNameValueInCommandHolder=recordNumToMapNameValueInCommandHolder,
            shellContentsList=currentScriptContentsList,
            howActive=onEditButton && !onDisableEditButton
        )
        buttonCreate(
            ToolbarButtonBariantForEdit.SETTING,
            buttonWeight,
            howActive=enableEditExecute && !onDisableSettingButton
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
                currentShellFileName
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
            editExecuteValue=editExecuteValue
        )
        val editTextProducerForEdit = EditTextProducerForEdit(
            editFragment,
            readSharePreffernceMap,
            currentScriptContentsList,
            recordNumToMapNameValueInCommandHolder,
            recordNumToMapNameValueInSettingHolder,
            enableCmdEdit
        )
        editTextProducerForEdit.adds(
            true
        )
        buttonCreate(
            ToolbarButtonBariantForEdit.EDIT,
            settingWeight,
            recordNumToMapNameValueInCommandHolder=recordNumToMapNameValueInCommandHolder,
            howActive=false
        )
        buttonCreate(
            ToolbarButtonBariantForEdit.SETTING,
            settingWeight,
            howActive=enableEditExecute && enableCmdEdit && !onPassCmdVariableEdit,
        )
    }

    private fun buttonCreate(
        toolbarButtonVariantForEdit: ToolbarButtonBariantForEdit,
        buttonWeight: Float,
        recordNumToMapNameValueInCommandHolder: Map<Int, Map<String, String>?>? = null,
        recordNumToMapNameValueInSettingHolder: Map<Int, Map<String, String>?>? = null,
        shellContentsList: List<String> = listOf(),
        howActive: Boolean = true,
        editExecuteValue: String = SettingVariableSelects.EditExecuteSelects.NO.name,
        setDrawble: Int? = null
    ){
        toolbarButtonProducerForEdit.make(
            toolbarButtonVariantForEdit,
            buttonWeight,
            recordNumToMapNameValueInCommandHolder,
            recordNumToMapNameValueInSettingHolder,
            shellContentsList,
            editExecuteValue,
            setDrawble
        )
        if(howActive) return
        buttonViewHowActive.buttonViewHowActive(
            toolbarButtonVariantForEdit.str,
            howActive,
        )
    }


    private fun setDrawbleForOk()
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
        val ordinalWeight = 0.25F
        if(!enableCmdEdit || !enableEditExecute){
            return ordinalWeight
        }
        val onMark = SettingVariableSelects.disableEditButtonSelects.ON.name
        val historyDisableValue = "OFF"
        return listOf(
            historyDisableValue,
            editFragment.disableEditButton,
            editFragment.disablePlayButton,
            editFragment.disableSettingButton,
        ).filter {
            it != onMark
        }.size.let { 1.0F / it }
    }
}

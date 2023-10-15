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
import com.puutaro.commandclick.util.FragmentTagManager
import kotlinx.coroutines.*


class EditModeHandler(
    private val editFragment: EditFragment,
    binding: EditFragmentBinding,
    private val currentShellContentsList: List<String>
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
    private val currentAppDirPath = SharePreffrenceMethod.getReadSharePreffernceMap(
        readSharePreffernceMap,
        SharePrefferenceSetting.current_app_dir
    )
    private val currentShellFileName =SharePreffrenceMethod.getReadSharePreffernceMap(
        readSharePreffernceMap,
        SharePrefferenceSetting.current_script_file_name
    )

    private val editExecuteValue = CommandClickVariables.returnEditExecuteValueStr(
        currentShellContentsList,
        editFragment.languageType
    )

    private val onShortcut = SharePreffrenceMethod.getReadSharePreffernceMap(
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
                currentShellContentsList,
                languageTypeToSectionHolderMap?.get(
                    CommandClickScriptVariable.HolderTypeName.CMD_SEC_START
                ) as String,
                languageTypeToSectionHolderMap[
                        CommandClickScriptVariable.HolderTypeName.CMD_SEC_END
                ] as String,
            )
        val recordNumToMapNameValueInSettingHolder =
            RecordNumToMapNameValueInHolder.parse(
                currentShellContentsList,
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
        buttonCreate(
            ToolbarButtonBariantForEdit.HISTORY,
        )

        buttonCreate(
            ToolbarButtonBariantForEdit.OK,
            recordNumToMapNameValueInCommandHolder=recordNumToMapNameValueInCommandHolder,
            shellContentsList=currentShellContentsList,
            editExecuteValue=editExecuteValue,
            setDrawble = setDrawbleForOk(),
            howActive = !onDisablePlayButton
        )
        val editTextProducerForEdit = EditTextProducerForEdit(
            editFragment,
            readSharePreffernceMap,
            currentShellContentsList,
            recordNumToMapNameValueInCommandHolder,
            recordNumToMapNameValueInSettingHolder,
            enableCmdEdit
        )
        editTextProducerForEdit.adds()

        val onEditButton = !onPassCmdVariableEdit
        buttonCreate(
            ToolbarButtonBariantForEdit.EDIT,
            recordNumToMapNameValueInCommandHolder=recordNumToMapNameValueInCommandHolder,
            shellContentsList=currentShellContentsList,
            howActive=onEditButton && !onDisableEditButton
        )
        buttonCreate(
            ToolbarButtonBariantForEdit.SETTING,
            howActive=enableEditExecute && !onDisableSettingButton
        )
    }

    private fun editSettingVariable(
    ) {
        val recordNumToMapNameValueInCommandHolder =
            RecordNumToMapNameValueInHolder.parse(
                currentShellContentsList,
                commandSectionStart,
                commandSectionEnd
            )
        val recordNumToMapNameValueInSettingHolder =
            RecordNumToMapNameValueInHolder.parse(
                currentShellContentsList,
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
        buttonCreate(
            ToolbarButtonBariantForEdit.HISTORY,
        )

        buttonCreate(
            ToolbarButtonBariantForEdit.OK,
            recordNumToMapNameValueInCommandHolder,
            recordNumToMapNameValueInSettingHolder,
            currentShellContentsList,
            editExecuteValue=editExecuteValue
        )
        val editTextProducerForEdit = EditTextProducerForEdit(
            editFragment,
            readSharePreffernceMap,
            currentShellContentsList,
            recordNumToMapNameValueInCommandHolder,
            recordNumToMapNameValueInSettingHolder,
            enableCmdEdit
        )
        editTextProducerForEdit.adds(
            true
        )
        buttonCreate(
            ToolbarButtonBariantForEdit.EDIT,
            recordNumToMapNameValueInCommandHolder=recordNumToMapNameValueInCommandHolder,
            howActive=false
        )
        buttonCreate(
            ToolbarButtonBariantForEdit.SETTING,
            howActive=enableEditExecute && enableCmdEdit && !onPassCmdVariableEdit,
        )
    }

    private fun buttonCreate(
        toolbarButtonVariantForEdit: ToolbarButtonBariantForEdit,
        recordNumToMapNameValueInCommandHolder: Map<Int, Map<String, String>?>? = null,
        recordNumToMapNameValueInSettingHolder: Map<Int, Map<String, String>?>? = null,
        shellContentsList: List<String> = listOf(),
        howActive: Boolean = true,
        editExecuteValue: String = SettingVariableSelects.EditExecuteSelects.NO.name,
        setDrawble: Int? = null
    ){
        toolbarButtonProducerForEdit.make(
            toolbarButtonVariantForEdit,
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

}

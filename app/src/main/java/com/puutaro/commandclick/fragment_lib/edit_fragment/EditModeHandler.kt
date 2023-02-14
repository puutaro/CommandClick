package com.puutaro.commandclick.fragment_lib.edit_fragment

import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.*
import com.puutaro.commandclick.databinding.EditFragmentBinding
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.ButtonViewHowActive
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.UpdateLastModifiedForAppHistory
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.EditTextProducerForEdit
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.ToolbarButtonProducerForEdit
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.ToolbarButtonBariantForEdit
import com.puutaro.commandclick.util.*


class EditModeHandler(
    private val editFragment: EditFragment,
    binding: EditFragmentBinding,
    private val readSharePreffernceMap: Map<String, String>
) {

    private val context = editFragment.context
    private val currentEditFragmentTag = editFragment.tag
    private val cmdVariableEditTagFName = context?.getString(
        R.string.cmd_variable_edit_fragment
    )
    private val cmdConfigEditTagFName = context?.getString(
        R.string.cmd_config_variable_edit_fragment
    )
    private val settingVariableEditTagFName = context?.getString(
        R.string.setting_variable_edit_fragment
    )
    private val apiEditTagName = context?.getString(
        R.string.api_cmd_variable_edit_api_fragment
    )
    private val enableCmdEdit = currentEditFragmentTag == cmdVariableEditTagFName
    private val currentAppDirPath = SharePreffrenceMethod.getReadSharePreffernceMap(
        readSharePreffernceMap,
        SharePrefferenceSetting.current_app_dir
    )
    private val currentShellFileName =SharePreffrenceMethod.getReadSharePreffernceMap(
        readSharePreffernceMap,
        SharePrefferenceSetting.current_shell_file_name
    )

    private val currentShellContentsList = ReadText(
        currentAppDirPath,
        currentShellFileName
    ).txetToList()

    private val editExecuteValue = CommandClickVariables.returnEditExecuteValueStr(
        currentShellContentsList
    )

    private val on_shortcut = SharePreffrenceMethod.getReadSharePreffernceMap(
        readSharePreffernceMap,
        SharePrefferenceSetting.on_shortcut
    ) == ShortcutOnValueStr.ON.name

    private val enableEditExecute =
        (editExecuteValue == SettingVariableSelects.Companion.EditExecuteSelects.ALWAYS.name
                && on_shortcut)


    private val toolbarButtonProducerForEdit = ToolbarButtonProducerForEdit(
        binding,
        editFragment,
        readSharePreffernceMap,
        enableCmdEdit,
    )
    val buttonViewHowActive = ButtonViewHowActive(
        binding,
        editFragment
    )

    fun execByHowFullEdit(){
        if(
            currentEditFragmentTag != settingVariableEditTagFName
            && currentEditFragmentTag != cmdConfigEditTagFName
        ) {
            editCommandVariable()
        }
        else editSettingVariable()
    }

    private fun editCommandVariable(
    ) {
        val recordNumToMapNameValueInCommandHolder =
            RecordNumToMapNameValueInHolder.parse(
                currentShellContentsList,
                CommandClickShellScript.CMD_VARIABLE_SECTION_START,
                CommandClickShellScript.CMD_VARIABLE_SECTION_END
            )
        val recordNumToMapNameValueInSettingHolder =
            RecordNumToMapNameValueInHolder.parse(
                currentShellContentsList,
                CommandClickShellScript.SETTING_SECTION_START,
                CommandClickShellScript.SETTING_SECTION_END,
                true,
            )

        if(
            recordNumToMapNameValueInCommandHolder.isNullOrEmpty()
        ) return
        UpdateLastModifiedForAppHistory.update(
            editExecuteValue,
            readSharePreffernceMap
        )
        buttonCreate(
            ToolbarButtonBariantForEdit.HISTORY,
            howActive = (
                        editFragment.tag != context?.getString(
                            R.string.api_cmd_variable_edit_api_fragment
                        )
                    )
        )

        buttonCreate(
            ToolbarButtonBariantForEdit.OK,
            recordNumToMapNameValueInCommandHolder=recordNumToMapNameValueInCommandHolder,
            shellContentsList=currentShellContentsList,
            editExecuteValue=editExecuteValue,
            setDrawble = setDrawbleForOk()
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

        val onEditButton = (
                currentEditFragmentTag != cmdConfigEditTagFName
                        && currentEditFragmentTag != apiEditTagName)
        buttonCreate(
            ToolbarButtonBariantForEdit.EDIT,
            recordNumToMapNameValueInCommandHolder=recordNumToMapNameValueInCommandHolder,
            shellContentsList=currentShellContentsList,
            howActive=onEditButton
        )
        buttonCreate(
            ToolbarButtonBariantForEdit.SETTING,
            howActive=enableEditExecute
        )
    }

    private fun editSettingVariable(
    ) {
        val recordNumToMapNameValueInCommandHolder =
            RecordNumToMapNameValueInHolder.parse(
                currentShellContentsList,
                CommandClickShellScript.CMD_VARIABLE_SECTION_START,
                CommandClickShellScript.CMD_VARIABLE_SECTION_END
            )
        val recordNumToMapNameValueInSettingHolder =
            RecordNumToMapNameValueInHolder.parse(
                currentShellContentsList,
                CommandClickShellScript.SETTING_SECTION_START,
                CommandClickShellScript.SETTING_SECTION_END,
                true,
                currentShellFileName
            )

        if(
            recordNumToMapNameValueInCommandHolder.isNullOrEmpty()
            && recordNumToMapNameValueInSettingHolder.isNullOrEmpty()
        ) {
            val listener = this.context as? EditFragment.onToolBarButtonClickListenerForEditFragment
            listener?.onToolBarButtonClickForEditFragment(
                editFragment.tag,
                ToolbarButtonBariantForEdit.CANCEL,
                readSharePreffernceMap,
                enableCmdEdit,
            )
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
            howActive=enableEditExecute && enableCmdEdit,
        )
    }

    private fun buttonCreate(
        toolbarButtonBariantForEdit: ToolbarButtonBariantForEdit,
        recordNumToMapNameValueInCommandHolder: Map<Int, Map<String, String>?>? = null,
        recordNumToMapNameValueInSettingHolder: Map<Int, Map<String, String>?>? = null,
        shellContentsList: List<String> = listOf(),
        howActive: Boolean = true,
        editExecuteValue: String = SettingVariableSelects.Companion.EditExecuteSelects.NO.name,
        setDrawble: Int? = null
    ){
        toolbarButtonProducerForEdit.make(
            toolbarButtonBariantForEdit,
            recordNumToMapNameValueInCommandHolder,
            recordNumToMapNameValueInSettingHolder,
            shellContentsList,
            editExecuteValue,
            setDrawble
        )
        if(howActive) return
        buttonViewHowActive.buttonViewHowActive(
            toolbarButtonBariantForEdit.str,
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

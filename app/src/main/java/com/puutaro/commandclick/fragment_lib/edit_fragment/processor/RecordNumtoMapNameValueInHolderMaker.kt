package com.puutaro.commandclick.fragment_lib.edit_fragment.processor

import com.puutaro.commandclick.common.variable.edit.RecordNumToMapNameValueInHolderColumn
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.IsCmdEdit
import com.puutaro.commandclick.util.RecordNumToMapNameValueInHolder
import com.puutaro.commandclick.util.state.SettingFannelConHandlerForEdit

object RecordNumToMapNameValueInHolderMaker {

    fun makeForSetting(
        editFragment: EditFragment,
    ): Map<Int, Map<String, String>?>? {
        val settingFannelConList = SettingFannelConHandlerForEdit.handle(
            editFragment
        )
        val recordNumToMapNameValueInSettingHolderSrc =
            RecordNumToMapNameValueInHolder.parse(
                settingFannelConList,
                editFragment.settingSectionStart,
                editFragment.settingSectionEnd,
                true,
            )
        return when(
            IsCmdEdit.judge(editFragment)
        ) {
            false -> {
//                editSettingVariable()
                filterRecordNumToMapNameValueInHolderByHideVariable(
                    editFragment,
                    recordNumToMapNameValueInSettingHolderSrc
                )
            }
            else -> {
//                editCommandVariable()
                recordNumToMapNameValueInSettingHolderSrc
            }
        }
    }

    fun makeForCmdHolder(
        editFragment: EditFragment,
        mainFannelSettingConList: List<String>,
    ):  Map<Int, Map<String, String>?>? {
        val recordNumToMapNameValueInCommandHolderSrc =
            RecordNumToMapNameValueInHolder.parse(
                mainFannelSettingConList,
                editFragment.commandSectionStart,
                editFragment.commandSectionEnd
            )
        return when(
            IsCmdEdit.judge(editFragment)
        ) {
            false -> {
//                editSettingVariable()
                recordNumToMapNameValueInCommandHolderSrc
            }
            else -> {
//                editCommandVariable()
                filterRecordNumToMapNameValueInHolderByHideVariable(
                    editFragment,
                    recordNumToMapNameValueInCommandHolderSrc
                )
            }
        }
    }

    private fun filterRecordNumToMapNameValueInHolderByHideVariable(
        editFragment: EditFragment,
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

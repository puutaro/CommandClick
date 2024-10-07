package com.puutaro.commandclick.fragment_lib.edit_fragment.processor

import com.puutaro.commandclick.common.variable.edit.RecordNumToMapNameValueInHolderColumn
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.util.RecordNumToMapNameValueInHolder
import com.puutaro.commandclick.util.state.SettingFannelConHandlerForEdit

object RecordNumToMapNameValueInHolderMaker {

    private const val settingSectionStart =  CommandClickScriptVariable.SETTING_SEC_START
    private const val settingSectionEnd =  CommandClickScriptVariable.SETTING_SEC_END
    private const val commandSectionStart =  CommandClickScriptVariable.CMD_SEC_START
    private const val commandSectionEnd =  CommandClickScriptVariable.CMD_SEC_END

//    fun makeForSetting(
//        editFragment: EditFragment,
//    ): Map<String, String>? {
//        val settingFannelConList = SettingFannelConHandlerForEdit.handle(
//            editFragment
//        )
//        val recordNumToMapNameValueInSettingHolderSrc =
//            RecordNumToMapNameValueInHolder.parse(
//                settingFannelConList,
//                settingSectionStart,
//                settingSectionEnd,
//            )
//        return recordNumToMapNameValueInSettingHolderSrc
////        filterRecordNumToMapNameValueInHolderByHideVariable(
////            editFragment,
////            recordNumToMapNameValueInSettingHolderSrc
////        )
////        return when(
////            IsCmdEdit.judge(editFragment)
////        ) {
////            false -> {
//////                editSettingVariable()
////                filterRecordNumToMapNameValueInHolderByHideVariable(
////                    editFragment,
////                    recordNumToMapNameValueInSettingHolderSrc
////                )
////            }
////            else -> {
//////                editCommandVariable()
////                recordNumToMapNameValueInSettingHolderSrc
////            }
////        }
//    }
//
//    fun makeForCmdHolder(
//        mainFannelSettingConList: List<String>,
//    ):  Map<String, String>? {
//        val recordNumToMapNameValueInCommandHolderSrc =
//            RecordNumToMapNameValueInHolder.parse(
//                mainFannelSettingConList,
//                commandSectionStart,
//                commandSectionEnd
//            )
//        return recordNumToMapNameValueInCommandHolderSrc
////        filterRecordNumToMapNameValueInHolderByHideVariable(
////            editFragment,
////            recordNumToMapNameValueInCommandHolderSrc
////        )
////        return when(
////            IsCmdEdit.judge(editFragment)
////        ) {
////            false -> {
//////                editSettingVariable()
////                recordNumToMapNameValueInCommandHolderSrc
////            }
////            else -> {
//////                editCommandVariable()
////                filterRecordNumToMapNameValueInHolderByHideVariable(
////                    editFragment,
////                    recordNumToMapNameValueInCommandHolderSrc
////                )
////            }
////        }
//    }

//    private fun filterRecordNumToMapNameValueInHolderByHideVariable(
//        editFragment: EditFragment,
//        recordNumToMapNameValueInHolder: Map<Int, Map<String, String>?>?,
//    ): Map<Int, Map<String, String>?>? {
////        val hideSettingVariableList = editFragment.hideSettingVariableList
//        return recordNumToMapNameValueInHolder?.filter {
//                currentRecordNumToMapNameValueInHolder ->
//            val currentRecordNumToNameToValueInHolder =
//                currentRecordNumToMapNameValueInHolder.value
//            val currentVariableName = currentRecordNumToNameToValueInHolder?.get(
//                RecordNumToMapNameValueInHolderColumn.VARIABLE_NAME.name
//            )
//            if (
//                currentVariableName.isNullOrEmpty()
//            ) return@filter false
//            !hideSettingVariableList.contains(
//                currentVariableName
//            )
//        }
//    }
}

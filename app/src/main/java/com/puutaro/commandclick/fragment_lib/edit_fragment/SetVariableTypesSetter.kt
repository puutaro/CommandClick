package com.puutaro.commandclick.fragment_lib.edit_fragment

import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.IsCmdEdit
import com.puutaro.commandclick.proccess.edit.lib.ListSettingVariableListMaker
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.util.state.FannelInfoTool

object SetVariableTypesSetterForEdit {

    fun set(
        editFragment: EditFragment,
        fannelInfoMap: Map<String, String>,
        settingVariableList: List<String>?,
    ): List<String> {
        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
            fannelInfoMap
        )
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )

        val setVariableTypeListSrc = ListSettingVariableListMaker.makeFromSettingVariableList(
            CommandClickScriptVariable.SET_VARIABLE_TYPE,
            fannelInfoMap,
            editFragment.setReplaceVariableMap,
            settingVariableList,
        )
        val setVariableForSettingHolder =
            CommandClickScriptVariable.setVariableForSettingHolder
        val isCmdEdit = IsCmdEdit.judge(editFragment)
        return when(isCmdEdit) {
            false -> setVariableForSettingHolder
            else -> setVariableTypeListSrc.let {
                if (
                    it.isEmpty()
                ) return@let setVariableForSettingHolder
                setVariableForSettingHolder + it
            }
        }.joinToString("\n").let {
            SetReplaceVariabler.execReplaceByReplaceVariables(
                it,
                editFragment.setReplaceVariableMap,
                currentAppDirPath,
                currentFannelName
            )
        }.split("\n").filter {
            it.trim().isNotEmpty()
        }
    }
}
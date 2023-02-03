package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.common

import android.content.Context
import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.util.CommandClickVariables

class DecideEditTag(
    private val shellContentsList: List<String>,
) {

    fun decide(
        context: Context?,
        cmdSettingEditFragmentTag: String? = null
    ): String? {

        val enableCommandHolderVariablesEdit = howEnablevaliableHolder(
            CommandClickShellScript.CMD_VARIABLE_SECTION_START,
            CommandClickShellScript.CMD_VARIABLE_SECTION_END
        )

        val enableSettingHolderVariablesEdit = howEnablevaliableHolder(
            CommandClickShellScript.SETTING_SECTION_START,
            CommandClickShellScript.SETTING_SECTION_END
        )
        if(!enableCommandHolderVariablesEdit
            && !enableSettingHolderVariablesEdit
        ){
            return null
        }
        return if(enableCommandHolderVariablesEdit) {
            context?.getString(com.puutaro.commandclick.R.string.cmd_variable_edit_fragment)
        } else {
            cmdSettingEditFragmentTag
        }

    }

    fun howEnablevaliableHolder(
        startHolderName: String,
        endHolderName: String,
    ): Boolean {
        val cmdclickVariableRegex = Regex("^[a-zA-Z0-9_-]*=")
        return CommandClickVariables.substituteVariableListFromHolder(
            shellContentsList,
            startHolderName,
            endHolderName
        )?.filter {
            cmdclickVariableRegex.containsMatchIn(
                it
            )
        }?.size?.let {
            it > 0
        } ?: false
    }
}
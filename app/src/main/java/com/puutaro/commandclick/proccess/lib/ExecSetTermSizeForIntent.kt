package com.puutaro.commandclick.proccess.lib

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.common.variable.ReadLines
import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.ToolbarMenuCategoriesVariantForCmdIndex
import com.puutaro.commandclick.proccess.ExecSetTermSizeForCmdIndexFragment.Companion.execSetTermSizeForCmdIndexFragment
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.view_model.activity.TerminalViewModel


class ExecSetTermSizeForIntent {
    companion object {
        fun execSetTermSizeForIntent(
            currentFragment: Fragment,
            substituteSettingVariableList: List<String>?,
            ) {

            val context = currentFragment.context
            val terminalViewModel: TerminalViewModel by currentFragment.activityViewModels()

            val terminalSizeType = CommandClickVariables.substituteCmdClickVariable(
                substituteSettingVariableList,
                CommandClickShellScript.TERMINAL_SIZE_TYPE
            ) ?: String()
            val onSetTerminalSize = when(terminalSizeType){
                SettingVariableSelects.Companion.TerminalSizeTypeSelects.SHORT.name -> {
                    ReadLines.SHORTH != terminalViewModel.readlinesNum
                }
                SettingVariableSelects.Companion.TerminalSizeTypeSelects.LONG.name -> {
                    ReadLines.LONGTH != terminalViewModel.readlinesNum
                }
                else -> false
            }
            if(
                !onSetTerminalSize || context == null
            ) return
            when(currentFragment){
                is CommandIndexFragment -> {
                    execSetTermSizeForCmdIndexFragment(
                        currentFragment,
                    )
                }
                is EditFragment -> {
                    val listener = context as? EditFragment.OnToolbarMenuCategoriesListenerForEdit
                    listener?.onToolbarMenuCategoriesForEdit(
                        ToolbarMenuCategoriesVariantForCmdIndex.TERMMAX
                    )
                }
                else -> {}
            }
        }
    }
}
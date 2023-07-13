package com.puutaro.commandclick.proccess.lib

import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.ReadLines
import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.ToolbarMenuCategoriesVariantForCmdIndex
import com.puutaro.commandclick.proccess.ExecSetTermSizeForCmdIndexFragment
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.view_model.activity.TerminalViewModel


object ExecSetTermSizeForIntent {
    fun execSetTermSizeForIntent(
        currentFragment: androidx.fragment.app.Fragment,
        substituteSettingVariableList: List<String>?,
        ) {

        val context = currentFragment.context
        val terminalViewModel: TerminalViewModel by currentFragment.activityViewModels()

        val terminalSizeType = CommandClickVariables.substituteCmdClickVariable(
            substituteSettingVariableList,
            CommandClickScriptVariable.TERMINAL_SIZE_TYPE
        ) ?: String()
        val onSetTerminalSize = when(terminalSizeType){
            SettingVariableSelects.TerminalSizeTypeSelects.SHORT.name -> {
                ReadLines.SHORTH != terminalViewModel.readlinesNum
            }
            SettingVariableSelects.TerminalSizeTypeSelects.LONG.name -> {
                ReadLines.LONGTH != terminalViewModel.readlinesNum
            }
            else -> false
        }
        if(
            !onSetTerminalSize || context == null
        ) return
        when(currentFragment){
            is com.puutaro.commandclick.fragment.CommandIndexFragment -> {
                ExecSetTermSizeForCmdIndexFragment.execSetTermSizeForCmdIndexFragment(
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
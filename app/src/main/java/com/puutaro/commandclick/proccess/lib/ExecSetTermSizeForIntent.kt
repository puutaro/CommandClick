package com.puutaro.commandclick.proccess.lib

import android.widget.LinearLayout
import com.puutaro.commandclick.common.variable.variant.ReadLines
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.ToolbarMenuCategoriesVariantForCmdIndex
import com.puutaro.commandclick.proccess.ExecSetTermSizeForCmdIndexFragment
import com.puutaro.commandclick.proccess.setting_button.MonitorSize
import com.puutaro.commandclick.util.state.EditFragmentArgs


object ExecSetTermSizeForIntent {
    fun execSetTermSizeForIntent(
        currentFragment: androidx.fragment.app.Fragment,
        monitorSize: String,
//        substituteSettingVariableList: List<String>?,
        ) {

        val context = currentFragment.context

//        val terminalSizeType = CommandClickVariables.substituteCmdClickVariable(
//            substituteSettingVariableList,
//            CommandClickScriptVariable.TERMINAL_SIZE_TYPE
//        ) ?: String()
        val currentFragmentWeight = when(currentFragment){
            is CommandIndexFragment -> {
                val linearLayoutParam =
                    currentFragment.binding.commandIndexFragment.layoutParams as LinearLayout.LayoutParams
                linearLayoutParam.weight
            }
            is EditFragment -> {
                val linearLayoutParam =
                    currentFragment.binding.editFragment.layoutParams as LinearLayout.LayoutParams
                linearLayoutParam.weight
            }
            else -> {
                return
            }
        }
        val onSetTerminalSize = when(monitorSize){
            MonitorSize.SHORT.name -> {
                ReadLines.LONGTH != currentFragmentWeight
            }
            MonitorSize.LONG.name -> {
                ReadLines.SHORTH != currentFragmentWeight
            }
            else -> false
        }
        if(
            !onSetTerminalSize || context == null
        ) return
        when(currentFragment){
            is CommandIndexFragment -> {
                ExecSetTermSizeForCmdIndexFragment.execSetTermSizeForCmdIndexFragment(
                    currentFragment,
                )
            }
            is EditFragment -> {
                val listener = context as? EditFragment.OnToolbarMenuCategoriesListenerForEdit
                listener?.onToolbarMenuCategoriesForEdit(
                    ToolbarMenuCategoriesVariantForCmdIndex.TERMMAX,
                    EditFragmentArgs(
                        currentFragment.readSharePreffernceMap,
                        EditFragmentArgs.Companion.EditTypeSettingsKey.CMD_VAL_EDIT
                    ),
                )
            }
            else -> {}
        }
    }
}
package com.puutaro.commandclick.proccess.lib

import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import com.puutaro.commandclick.common.variable.variant.ReadLines
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.ToolbarMenuCategoriesVariantForCmdIndex
import com.puutaro.commandclick.proccess.monitor.MonitorSizeManager
import com.puutaro.commandclick.proccess.tool_bar_button.config_settings.ClickSettingsForToolbarButton
import com.puutaro.commandclick.util.state.EditFragmentArgs


object ExecSetTermSizeForIntent {
    fun execSetTermSizeForIntent(
        currentFragment: androidx.fragment.app.Fragment,
        monitorSize: String,
        ) {

        val context = currentFragment.context

        val currentFragmentWeight = when(currentFragment){
            is CommandIndexFragment -> {
                val linearLayoutParam =
                    currentFragment.binding.commandIndexFragment.layoutParams as LinearLayoutCompat.LayoutParams
                linearLayoutParam.weight
            }
            is EditFragment -> {
                val linearLayoutParam =
                    currentFragment.binding.editFragment.layoutParams as LinearLayoutCompat.LayoutParams
                linearLayoutParam.weight
            }
            else -> {
                return
            }
        }
        val onSetTerminalSize = when(monitorSize){
            ClickSettingsForToolbarButton.MonitorSize.SHORT.name -> {
                ReadLines.LONGTH != currentFragmentWeight
            }
            ClickSettingsForToolbarButton.MonitorSize.LONG.name -> {
                ReadLines.SHORTH != currentFragmentWeight
            }
            else -> false
        }
        if(
            !onSetTerminalSize || context == null
        ) return
        when(currentFragment){
            is CommandIndexFragment -> {
                MonitorSizeManager.changeForCmdIndexFragment(
                    currentFragment,
                )
            }
            is EditFragment -> {
                val listener = context as? EditFragment.OnToolbarMenuCategoriesListenerForEdit
                listener?.onToolbarMenuCategoriesForEdit(
                    ToolbarMenuCategoriesVariantForCmdIndex.TERMMAX,
                    EditFragmentArgs(
                        currentFragment.fannelInfoMap,
                        EditFragmentArgs.Companion.EditTypeSettingsKey.CMD_VAL_EDIT
                    ),
                )
            }
            else -> {}
        }
    }
}
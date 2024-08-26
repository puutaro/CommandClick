package com.puutaro.commandclick.proccess.monitor

import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.variant.ReadLines
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.SearchSwichImage
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.ToolbarMenuCategoriesVariantForCmdIndex
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditLayoutViewHideShow
import com.puutaro.commandclick.proccess.ExistTerminalFragment
import com.puutaro.commandclick.util.state.EditFragmentArgs


object MonitorSizeManager {
    fun changeForCmdIndexFragment(
        cmdIndexFragment: CommandIndexFragment,
    ){
        val context = cmdIndexFragment.context

        val binding = cmdIndexFragment.binding
        val cmdindexInternetButton = binding.cmdindexInternetButton
//        val cmdListSwipeToRefresh = binding.cmdListSwipeToRefresh
        val linearLayoutParam =
            binding.commandIndexFragment.layoutParams as LinearLayoutCompat.LayoutParams
        when(
            linearLayoutParam.weight != ReadLines.LONGTH
        ) {
            true
            -> {
//                cmdListSwipeToRefresh.isVisible = true
                binding.cmdSearchEditText.setText("")
                cmdindexInternetButton.setImageResource(
                    SearchSwichImage.TERMINAL.image
                )
                context?.let {
                    cmdindexInternetButton.imageTintList =
                        it.getColorStateList(R.color.terminal_color)
                    cmdindexInternetButton.backgroundTintList =
                        it.getColorStateList(R.color.icon_selected_color)
                }
            }
            else
            -> {
//                cmdListSwipeToRefresh.isVisible = false
                cmdindexInternetButton.setImageResource(
                    SearchSwichImage.WEB.image
                )
                context?.let {
                    cmdindexInternetButton.imageTintList =
                        it.getColorStateList(R.color.terminal_color)
                    cmdindexInternetButton.backgroundTintList =
                        it.getColorStateList(R.color.icon_selected_color)
                }
            }
        }
        cmdIndexFragment.WebSearchSwitch = true
        val listener =
            context as? CommandIndexFragment.OnToolbarMenuCategoriesListener
        listener?.onToolbarMenuCategories(
            ToolbarMenuCategoriesVariantForCmdIndex.TERMMAX,
            EditFragmentArgs(
                mapOf(),
                EditFragmentArgs.Companion.EditTypeSettingsKey.CMD_VAL_EDIT
            )
        )
    }

    fun changeForEdit(
        editFragment: EditFragment
    ) {
        if (
            editFragment.terminalOn
            == SettingVariableSelects.TerminalDoSelects.OFF.name
        ) return
        val context = editFragment.context
        val existEditExecuteTerminalFragment = ExistTerminalFragment
            .how(
                editFragment,
                editFragment.context?.getString(
                    R.string.edit_terminal_fragment
                )
            )
        if (
            existEditExecuteTerminalFragment?.isVisible != true
        ) {
            ToastUtils.showShort("no terminal")
            return
        }
        val linearLayoutParam =
            editFragment.binding.editFragment.layoutParams as LinearLayoutCompat.LayoutParams
        val isShow = linearLayoutParam.weight != ReadLines.LONGTH
        EditLayoutViewHideShow.exec(
            editFragment,
            isShow
        )
        val listener =
            context as? EditFragment.OnToolbarMenuCategoriesListenerForEdit
        listener?.onToolbarMenuCategoriesForEdit(
            ToolbarMenuCategoriesVariantForCmdIndex.TERMMAX,
            EditFragmentArgs(
                editFragment.fannelInfoMap,
                EditFragmentArgs.Companion.EditTypeSettingsKey.CMD_VAL_EDIT
            )
        )
    }
}
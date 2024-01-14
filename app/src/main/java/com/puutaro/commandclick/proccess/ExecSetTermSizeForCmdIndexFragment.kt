package com.puutaro.commandclick.proccess

import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.variant.ReadLines
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.SearchSwichImage
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.ToolbarMenuCategoriesVariantForCmdIndex
import com.puutaro.commandclick.util.state.EditFragmentArgs


object ExecSetTermSizeForCmdIndexFragment {
    fun execSetTermSizeForCmdIndexFragment(
        cmdIndexFragment: CommandIndexFragment,
    ){
        val context = cmdIndexFragment.context

        val binding = cmdIndexFragment.binding
        val cmdindexInternetButton = binding.cmdindexInternetButton
        val cmdListSwipeToRefresh = binding.cmdListSwipeToRefresh
        val linearLayoutParam =
            binding.commandIndexFragment.layoutParams as LinearLayout.LayoutParams
        when(
            linearLayoutParam.weight != ReadLines.LONGTH
        ) {
            true
            -> {
                cmdListSwipeToRefresh.isVisible = true
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
                cmdListSwipeToRefresh.isVisible = false
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
}
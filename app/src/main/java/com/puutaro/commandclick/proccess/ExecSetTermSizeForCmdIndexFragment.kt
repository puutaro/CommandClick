package com.puutaro.commandclick.proccess

import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.ReadLines
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.SearchSwichImage
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.ToolbarMenuCategoriesVariantForCmdIndex
import com.puutaro.commandclick.view_model.activity.TerminalViewModel


object ExecSetTermSizeForCmdIndexFragment {
    fun execSetTermSizeForCmdIndexFragment(
        cmdIndexFragment: CommandIndexFragment,
    ){
        val context = cmdIndexFragment.context
        val terminalViewModel: TerminalViewModel by cmdIndexFragment.activityViewModels()

        val binding = cmdIndexFragment.binding
        val cmdindexInternetButton = binding.cmdindexInternetButton
        val cmdListSwipeToRefresh = binding.cmdListSwipeToRefresh
        if(terminalViewModel.readlinesNum != ReadLines.SHORTH) {
            cmdListSwipeToRefresh.isVisible = true
            binding.cmdSearchEditText.setText("")
            cmdindexInternetButton.setImageResource(
                SearchSwichImage.TERMINAL.image
            )
            context?.let {
                cmdindexInternetButton.imageTintList =
                    it.getColorStateList(R.color.black)
                cmdindexInternetButton.backgroundTintList = it.getColorStateList(R.color.gray_out)
            }
        } else {
            cmdListSwipeToRefresh.isVisible = false
            cmdindexInternetButton.setImageResource(
                SearchSwichImage.WEB.image
            )
            context?.let {
                cmdindexInternetButton.imageTintList = it.getColorStateList(R.color.black)
                cmdindexInternetButton.setBackgroundTintList(it.getColorStateList(R.color.gray_out));
                (it.getColor(R.color.white))
            }
        }
        cmdIndexFragment.WebSearchSwitch = true
        val listener =
            context as? CommandIndexFragment.OnToolbarMenuCategoriesListener
        listener?.onToolbarMenuCategories(
            ToolbarMenuCategoriesVariantForCmdIndex.TERMMAX
        )
    }
}
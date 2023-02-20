package com.puutaro.commandclick.proccess

import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.ReadLines
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.internet_button.AutoCompleteEditTexter
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.SearchSwichImage
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.ToolbarMenuCategoriesVariantForCmdIndex
import com.puutaro.commandclick.view_model.activity.TerminalViewModel


class ExecSetTermSizeForCmdIndexFragment {
    companion object {
        fun execSetTermSizeForCmdIndexFragment(
            cmdIndexFragment: CommandIndexFragment,
            recentAppDirPath: String? = null,
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
                    cmdindexInternetButton.setBackgroundTintList(
                        it.getColorStateList(R.color.white)
                    )
                    AutoCompleteEditTexter.setAdapter(
                        context,
                        binding.cmdSearchEditText,
                    )
                }
            } else {
                cmdListSwipeToRefresh.isVisible = false
                cmdindexInternetButton.setImageResource(
                    SearchSwichImage.WEB.image
                )
                context?.let {
                    cmdindexInternetButton.imageTintList = it.getColorStateList(R.color.black)
                    cmdindexInternetButton.setBackgroundTintList(it.getColorStateList(R.color.white));
                    (it.getColor(R.color.white))
                }
            }
            cmdIndexFragment.WebSearchSwitch = false
            val listener =
                context as? CommandIndexFragment.OnToolbarMenuCategoriesListener
            listener?.onToolbarMenuCategories(
                ToolbarMenuCategoriesVariantForCmdIndex.TERMMAX
            )
        }
    }
}
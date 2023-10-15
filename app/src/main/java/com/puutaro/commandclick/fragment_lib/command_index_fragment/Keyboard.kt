package com.puutaro.commandclick.fragment_lib.command_index_fragment

import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.variant.ReadLines
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.ToolbarWidgetWeightForLinearLayout
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.ToolbarMenuCategoriesVariantForCmdIndex
import com.puutaro.commandclick.view_model.activity.TerminalViewModel


object KeyboardForCmdIndex {

    fun historyAndSearchHideShow(
        isOpen: Boolean,
        cmdIndexCommandIndexFragment: CommandIndexFragment,
    ){

        val binding = cmdIndexCommandIndexFragment.binding
        val cmdIndexHistory = binding.cmdindexHistoryButton
        val cmdIndexSetting = binding.cmdindexSettingButton
        val cmdSearchEditText = binding.cmdSearchEditText
        val linearLayoutParam = ToolbarWidgetWeightForLinearLayout.buttonWideWeight
        val linearLayoutParamShrink =ToolbarWidgetWeightForLinearLayout.buttonShrinkWeight
        if(!isOpen) {
            cmdIndexSetting.layoutParams = linearLayoutParam
            cmdIndexHistory.layoutParams = linearLayoutParam
            cmdSearchEditText.setText(String())
            cmdSearchEditText.clearFocus()
            return
        }
        cmdIndexSetting.layoutParams = linearLayoutParamShrink
        cmdIndexHistory.layoutParams = linearLayoutParamShrink
    }

    fun ajustCmdIndexFragmentWhenTermLong(
        isOpen: Boolean,
        cmdIndexFragment: CommandIndexFragment,
    ){
        val terminalViewModel: TerminalViewModel by cmdIndexFragment.activityViewModels()
        val binding = cmdIndexFragment.binding
        val context = cmdIndexFragment.context
        val cmdIndexSwipToRefreshLayout = binding.cmdListSwipeToRefresh
        val cmdIndexHistory = binding.cmdindexHistoryButton
        val cmdIndexSetting = binding.cmdindexSettingButton
        val cmdSearchEditText = binding.cmdSearchEditText
        val linearLayoutParamForButtonWideWeight = ToolbarWidgetWeightForLinearLayout.buttonWideWeight
        val linearLayoutParamShrinkForButtonShrinkWeight = ToolbarWidgetWeightForLinearLayout.buttonShrinkWeight
        val linearLayoutParamForSearchTextWideWeight = ToolbarWidgetWeightForLinearLayout.searchEditTextWideWeight
        val linearLayoutParamShrinkForSearchTextShrinkWeight =ToolbarWidgetWeightForLinearLayout.searchEditTextShrinkWeight
        if(isOpen){
            cmdIndexSwipToRefreshLayout.isVisible = false
            cmdIndexHistory.layoutParams = linearLayoutParamShrinkForButtonShrinkWeight
            cmdIndexSetting.layoutParams = linearLayoutParamShrinkForButtonShrinkWeight
            cmdSearchEditText.layoutParams = linearLayoutParamForSearchTextWideWeight
            val listener = context as? CommandIndexFragment.OnToolbarMenuCategoriesListener
            listener?.onToolbarMenuCategories(
                ToolbarMenuCategoriesVariantForCmdIndex.TERMMAX_KEYBOARD_OPEN
            )
            return
        }
        cmdIndexHistory.layoutParams = linearLayoutParamForButtonWideWeight
        cmdIndexSetting.layoutParams = linearLayoutParamForButtonWideWeight
        cmdSearchEditText.layoutParams = linearLayoutParamShrinkForSearchTextShrinkWeight
        if(!cmdIndexFragment.WebSearchSwitch) cmdSearchEditText.setText(String())
        cmdSearchEditText.clearFocus()
        cmdIndexSwipToRefreshLayout.isVisible =
            terminalViewModel.readlinesNum == ReadLines.SHORTH
        val listener = context as? CommandIndexFragment.OnToolbarMenuCategoriesListener
        listener?.onToolbarMenuCategories(
            ToolbarMenuCategoriesVariantForCmdIndex.TERMMAX_KEYBOARD_CLOSE
        )
    }
}
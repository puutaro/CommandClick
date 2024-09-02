package com.puutaro.commandclick.fragment_lib.command_index_fragment

import android.widget.AutoCompleteTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.proccess.pin.PinFannelHideShow
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import com.puutaro.commandclick.util.url.WebUrlVariables


object KeyboardForCmdIndex {

    fun historyAndSearchHideShow(
        activity: MainActivity,
        isOpen: Boolean,
        isFromTerminal: Boolean,
    ){
        val cmdIndexFragment = TargetFragmentInstance.getCmdIndexFragment(
            activity
        ) ?: return
        val isPinHide = PinFannelHideShow.isHide()
        when(isFromTerminal){
            true -> {
                val toolbarLinear = TargetFragmentInstance
                    .getCurrentTerminalFragment(activity)
                    ?.binding
                    ?.termBottomLinear
                toolbarLinear?.isVisible = !isOpen && !isPinHide
            }
            else -> {
                val toolbarLinear = cmdIndexFragment.binding.cmdindexToolbarLinearLayout
                toolbarLinear.isVisible = !isOpen && isPinHide
            }
        }
        val terminalFragment = TargetFragmentInstance.getCurrentTerminalFragment(
            activity
        )
        terminalFragment?.binding?.fannelPinRecyclerView?.isVisible =
            !isOpen && !isPinHide

        val isGgleSearchUrl = when(
                terminalFragment == null
                || !terminalFragment.isVisible
            ) {
            true -> false
            else -> terminalFragment.binding.terminalWebView.url?.startsWith(
                WebUrlVariables.queryUrlBase
            ) == true
        }
        if(isGgleSearchUrl) {
            val cmdindexSearchLinearLayout = cmdIndexFragment.binding.cmdindexSearchLinearLayout
            val searchEditText = cmdIndexFragment.binding.cmdSearchEditText
            cmdindexSearchLinearLayout.isVisible = false
            searchEditText.setSelection(0)
            searchEditText.clearFocus()
        }
//        when(isGgleSearchUrl){
//            true -> {
//                cmdindexSearchLinearLayout.isVisible = false
//                searchEditText.setSelection(0)
//                searchEditText.clearFocus()
//            }
//            else ->
//                setSearchEditText(
//                    cmdindexSearchLinearLayout,
//                    searchEditText,
//                    isOpen,
//                )
//        }

//        cmdclickToolBar.isVisible = !isOpen
//        cmdSearchEditText.isVisible = isOpen
//        when(isOpen){
//            true -> {
////                cmdSearchEditText.setText(String())
//                cmdSearchEditText.requestFocus()
//            }
//            else -> {
//                cmdSearchEditText.clearFocus()
//            }
//        }
//        if(!isOpen) {
//            cmdclickToolBar.isVisible = true
//            cmdSearchEditText.isVisible = false
////            cmdIndexSetting.layoutParams = linearLayoutParam
////            cmdIndexHistory.layoutParams = linearLayoutParam
////            cmdSearchEditText.setText(String())
////            cmdSearchEditText.clearFocus()
//            return
//        }
//        cmdIndexSetting.layoutParams = linearLayoutParamShrink
//        cmdIndexHistory.layoutParams = linearLayoutParamShrink
    }

//    fun ajustCmdIndexFragmentWhenTermLong(
//        isOpen: Boolean,
//        cmdIndexFragment: CommandIndexFragment,
//    ){
//        val binding = cmdIndexFragment.binding
//        val context = cmdIndexFragment.context
//        val cmdIndexSwipToRefreshLayout = binding.cmdListSwipeToRefresh
//        val cmdIndexHistory = binding.cmdindexHistoryButton
//        val cmdIndexSetting = binding.cmdindexSettingButton
//        val cmdSearchEditText = binding.cmdSearchEditText
//        val linearLayoutParamForButtonWideWeight = ToolbarWidgetWeightForLinearLayout.buttonWideWeight
//        val linearLayoutParamShrinkForButtonShrinkWeight = ToolbarWidgetWeightForLinearLayout.buttonShrinkWeight
//        val linearLayoutParamForSearchTextWideWeight = ToolbarWidgetWeightForLinearLayout.searchEditTextWideWeight
//        val linearLayoutParamShrinkForSearchTextShrinkWeight =ToolbarWidgetWeightForLinearLayout.searchEditTextShrinkWeight
//        if(isOpen){
//            cmdIndexSwipToRefreshLayout.isVisible = false
//            cmdIndexHistory.layoutParams = linearLayoutParamShrinkForButtonShrinkWeight
//            cmdIndexSetting.layoutParams = linearLayoutParamShrinkForButtonShrinkWeight
//            cmdSearchEditText.layoutParams = linearLayoutParamForSearchTextWideWeight
//            val listener = context as? CommandIndexFragment.OnToolbarMenuCategoriesListener
//            listener?.onToolbarMenuCategories(
//                ToolbarMenuCategoriesVariantForCmdIndex.TERMMAX_KEYBOARD_OPEN,
//                EditFragmentArgs(
//                    mapOf(),
//                    EditFragmentArgs.Companion.EditTypeSettingsKey.CMD_VAL_EDIT,
//                )
//            )
//            return
//        }
//        cmdIndexHistory.layoutParams = linearLayoutParamForButtonWideWeight
//        cmdIndexSetting.layoutParams = linearLayoutParamForButtonWideWeight
//        cmdSearchEditText.layoutParams = linearLayoutParamShrinkForSearchTextShrinkWeight
//        if(!cmdIndexFragment.WebSearchSwitch) cmdSearchEditText.setText(String())
//        cmdSearchEditText.clearFocus()
//        val linearLayoutParam =
//            binding.commandIndexFragment.layoutParams as LinearLayout.LayoutParams
//        val cmdIndexFragmentWeight = linearLayoutParam.weight
//        cmdIndexSwipToRefreshLayout.isVisible =
//            cmdIndexFragmentWeight == ReadLines.LONGTH
//        val listener = context as? CommandIndexFragment.OnToolbarMenuCategoriesListener
//        listener?.onToolbarMenuCategories(
//            ToolbarMenuCategoriesVariantForCmdIndex.TERMMAX_KEYBOARD_CLOSE,
//            EditFragmentArgs(
//                mapOf(),
//                EditFragmentArgs.Companion.EditTypeSettingsKey.CMD_VAL_EDIT,
//            )
//        )
//    }

    private fun setSearchEditText(
        cmdindexSearchLinearLayout: LinearLayoutCompat,
        cmdSearchEditText: AutoCompleteTextView,
        isOpen: Boolean,
    ){
        cmdindexSearchLinearLayout.isVisible = isOpen
        when(isOpen){
            true -> {
//                cmdSearchEditText.setText(String())
                cmdSearchEditText.requestFocus()
            }
            else -> {
                cmdSearchEditText.setSelection(0)
                cmdSearchEditText.clearFocus()
            }
        }
    }
}
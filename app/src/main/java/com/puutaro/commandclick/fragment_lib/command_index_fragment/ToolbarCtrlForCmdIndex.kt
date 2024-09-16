package com.puutaro.commandclick.fragment_lib.command_index_fragment

import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.activity_lib.event.lib.terminal.ExecSetToolbarButtonImage
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeTerm
import com.puutaro.commandclick.custom_view.OutlineTextView
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.proccess.pin.PinFannelHideShow
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import com.puutaro.commandclick.util.url.WebUrlVariables


object ToolbarCtrlForCmdIndex {


    fun hideShow(
        activity: MainActivity,
        isOpen: Boolean,
        isFromTerminal: Boolean,
    ){
        val cmdIndexFragment = TargetFragmentInstance.getCmdIndexFragment(
            activity
        ) ?: return
        val binding = cmdIndexFragment.binding
        binding.cmdindexSelectionSearchButton.isVisible = false
        val isPinHide = PinFannelHideShow.isHide()
        val pageSearch = binding.pageSearch
        val cmdclickPageSearchToolBar = pageSearch.cmdclickPageSearchToolBar
        val isPageSearch = cmdclickPageSearchToolBar.isVisible
        when(isFromTerminal){
            true -> {
                val toolbarLinear = TargetFragmentInstance
                    .getCurrentTerminalFragment(activity)
                    ?.binding
                    ?.termBottomLinear
                toolbarLinear?.isVisible = !isOpen && !isPinHide && !isPageSearch
            }
            else -> {
                val toolbarLinear = binding.cmdindexToolbarLinearLayout
                toolbarLinear.isVisible = !isOpen && isPinHide && !isPageSearch
            }
        }
        val terminalFragment = TargetFragmentInstance.getCurrentTerminalFragment(
            activity
        )
        terminalFragment?.binding?.fannelPinRecyclerView?.isVisible =
            !isOpen && !isPinHide && !isPageSearch

        when(isPageSearch) {
            true -> {
                if(!isOpen) return
                pageSearch.cmdPageSearchEditText.requestFocus()
            }
            else -> execGgleFocus(
                terminalFragment,
                isOpen,
            )
        }
    }

    fun updateTextSelection(
        activity: MainActivity,
        updateText: String,
    ){
        val cmdIndexFragment = TargetFragmentInstance.getCmdIndexFragment(
            activity
        ) ?: return
        val binding = cmdIndexFragment.binding
        binding.cmdindexSelectionSearchCurText.apply {
            isVisible = true
            text = updateText
        }
    }

    fun hideShowForTextSelection(
        activity: MainActivity,
        isShow: Boolean,
    ){
        val cmdIndexFragment = TargetFragmentInstance.getCmdIndexFragment(
            activity
        ) ?: return
        val binding = cmdIndexFragment.binding
        val isPinHide = PinFannelHideShow.isHide()
        val pageSearch = binding.pageSearch
        val cmdclickPageSearchToolBar = pageSearch.cmdclickPageSearchToolBar
        val isPageSearch = cmdclickPageSearchToolBar.isVisible
        if(
            isPageSearch
        ) return
        binding.cmdindexSelectionSearchButton.isVisible = isShow
        if(!isShow) {
            binding.cmdindexSelectionSearchCurText.text = String()
            binding.cmdindexSelectionSearchCurText.isVisible = false
        }
        BroadcastSender.normalSend(
            activity,
            BroadCastIntentSchemeTerm.FANNEL_PIN_BAR_UPDATE.action,
        )
        if(isShow) {
            ExecSetToolbarButtonImage.SelectionBarButton.updatePocketSearchImage(
                binding,
            )
        }
        when(isPinHide){
            false -> {
                val terminalFragmentBinding = TargetFragmentInstance
                    .getCurrentTerminalFragment(activity)
                    ?.binding
                terminalFragmentBinding?.termBottomLinear?.isVisible = !isShow
            }
            else -> {
                val toolbarLinear = binding.cmdindexToolbarLinearLayout
                toolbarLinear.isVisible = !isShow
            }
        }
        val terminalFragment = TargetFragmentInstance.getCurrentTerminalFragment(
            activity
        ) ?: return
        if(!isShow) {
            terminalFragment.selectionText = String()
        }
        terminalFragment.binding.fannelPinRecyclerView.isVisible = !isPinHide
//            !isShow && !isPinHide
    }


    private fun execGgleFocus(
        terminalFragment: TerminalFragment?,
        isOpen: Boolean,
    ){
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
            terminalFragment?.binding?.termGgleFocusImage?.isVisible = isOpen
            terminalFragment?.binding?.termGgleFocusImageCaption?.isVisible = isOpen
//            val searchEditText = cmdIndexFragment.binding.cmdSearchEditText
            terminalFragment?.binding?.termQrScanImage?.isVisible = isOpen
//            terminalFragment?.binding?.termQrScanImageCaption?.isVisible = isOpen
//            searchEditText.setSelection(0)
//            searchEditText.clearFocus()
        }
    }
}
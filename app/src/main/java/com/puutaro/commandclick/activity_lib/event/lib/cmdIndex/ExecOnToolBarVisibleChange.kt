package com.puutaro.commandclick.activity_lib.event.lib.cmdIndex

import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.proccess.pin.PinFannelHideShow
import com.puutaro.commandclick.util.state.TargetFragmentInstance


object ExecOnToolBarVisibleChange {
    fun execOnToolBarVisibleChange(
        activity: MainActivity,
        commandIndexFragment: CommandIndexFragment,
        toolBarVisible: Boolean
    ){
        val isPageSearch = commandIndexFragment.binding.pageSearch.cmdclickPageSearchToolBar.isVisible
        if(isPageSearch) return
        changeCmdIndex(
            activity,
            commandIndexFragment,
            toolBarVisible
        )
        changeTerminal(
            activity,
            toolBarVisible
        )
    }

    private fun changeCmdIndex(
        activity: MainActivity,
        commandIndexFragment: CommandIndexFragment,
        toolBarVisible: Boolean
    ){
        val layoutParam = when(toolBarVisible) {
            true -> {
//                val dpHeight =
//                    ScreenSizeCalculator.toDp(
//                        activity,
//                        activity.resources.getDimension(TerminalSizingForCmdIndex.VISIBLE.height)
//                    )

                LinearLayoutCompat.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    TerminalSizingForCmdIndex.VISIBLE.height,
//                    ScreenSizeCalculator.toPx(
//                        activity,
//                        dpHeight
//                    )
                )
            }
//            TerminalSizingForCmdIndex.VISIBLE.weight
            else -> {
                LinearLayoutCompat.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    TerminalSizingForCmdIndex.INVISIBLE.height
                )
//            TerminalSizingForCmdIndex.INVISIBLE.weight
            }
        }
        if(!commandIndexFragment.isVisible) return
        commandIndexFragment.binding.commandIndexFragment.layoutParams = layoutParam
    }

    private fun changeTerminal(
        activity: MainActivity,
        toolBarVisible: Boolean
    ){
        val terminalFragment =
            TargetFragmentInstance.getCurrentTerminalFragment(activity)
                ?: return
        val isShow = !PinFannelHideShow.isHide()
        val binding = terminalFragment.binding
        val fannelPinRecyclerView =
            binding.fannelPinRecyclerView
        val termBottomLinear =
            binding.termBottomLinear
        fannelPinRecyclerView.isVisible = toolBarVisible && isShow
        termBottomLinear.isVisible = toolBarVisible && isShow
    }
}



private enum class TerminalSizingForCmdIndex(
    val height: Int
) {
    VISIBLE(ViewGroup.LayoutParams.WRAP_CONTENT),
    INVISIBLE(0)
}
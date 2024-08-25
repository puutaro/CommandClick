package com.puutaro.commandclick.activity_lib.event.lib.cmdIndex

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.TerminalSizingForEdit
import com.puutaro.commandclick.util.image_tools.ScreenSizeCalculator
import com.puutaro.commandclick.util.state.TargetFragmentInstance

object ExecOnToolBarVisibleChange {
    fun execOnToolBarVisibleChange(
        activity: MainActivity,
        commandIndexFragment: CommandIndexFragment,
        toolBarVisible: Boolean
    ){
//        val layoutParam = LinearLayout.LayoutParams(
//            ViewGroup.LayoutParams.MATCH_PARENT,
//            0
//        )
//        layoutParam.weight =
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
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ScreenSizeCalculator.toDp(
                        activity,
                        TerminalSizingForCmdIndex.VISIBLE.height
                    )
                )
            }
//            TerminalSizingForCmdIndex.VISIBLE.weight
            else -> {
                LinearLayout.LayoutParams(
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
            TargetFragmentInstance().getCurrentTerminalFragment(activity)
                ?: return
        val fannelPinRecyclerView =
            terminalFragment.binding.fannelPinRecyclerView
        fannelPinRecyclerView.isVisible = toolBarVisible
    }
}



private enum class TerminalSizingForCmdIndex(
    val height: Int
) {
    VISIBLE(50),
    INVISIBLE(0)
}
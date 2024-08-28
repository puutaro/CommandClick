package com.puutaro.commandclick.activity_lib.event.lib.cmdIndex

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import ch.qos.logback.core.util.Loader.getResources
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.fragment.CommandIndexFragment
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
    VISIBLE(ViewGroup.LayoutParams.WRAP_CONTENT),
    INVISIBLE(0)
}
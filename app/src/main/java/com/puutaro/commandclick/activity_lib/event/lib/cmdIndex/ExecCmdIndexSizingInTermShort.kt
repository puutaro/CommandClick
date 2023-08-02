package com.puutaro.commandclick.activity_lib.event.lib.cmdIndex

import androidx.core.view.isVisible
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.CmdIndexLinearWeightParam

object ExecCmdIndexSizingInTermShort {
    fun execCmdIndexSizingInTermShort(
        activity: MainActivity,
        isKeyboardShowing: Boolean,
    ){
        val cmdIndexCommandIndexFragment = try {
            activity.supportFragmentManager.findFragmentByTag(
                activity.getString(R.string.command_index_fragment)
            ) as CommandIndexFragment
        } catch (e: Exception) {
            return
        }
        if(isKeyboardShowing) {
            cmdIndexCommandIndexFragment.binding.commandIndexFragment.layoutParams =
                CmdIndexLinearWeightParam.listViewShortWeight
            cmdIndexCommandIndexFragment.binding.cmdListSwipeToRefresh.isVisible = false
            return
        }
        cmdIndexCommandIndexFragment.binding.cmdListSwipeToRefresh.isVisible = true
        cmdIndexCommandIndexFragment.binding.commandIndexFragment.layoutParams =
            CmdIndexLinearWeightParam.listViewLongWeight
    }
}
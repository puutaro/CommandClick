package com.puutaro.commandclick.activity_lib.event.lib.cmdIndex

import androidx.core.view.isVisible
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.CmdIndexLinearWeightParam

class ExecCmdIndexSizingInTermShort {
    companion object {
        fun execCmdIndexSizingInTermShort(
            activity: MainActivity,
            isKeyboardShowing: Boolean,
        ){
            val cmdIndexFragment = try {
                activity.supportFragmentManager.findFragmentByTag(
                    activity.getString(R.string.command_index_fragment)
                ) as CommandIndexFragment
            } catch (e: Exception) {
                return
            }
            if(isKeyboardShowing) {
                cmdIndexFragment.binding.commandIndexFragment.layoutParams =
                    CmdIndexLinearWeightParam.listViewShortWeight
                cmdIndexFragment.binding.cmdListSwipeToRefresh.isVisible = false
                return
            }
            cmdIndexFragment.binding.cmdListSwipeToRefresh.isVisible = true
            cmdIndexFragment.binding.commandIndexFragment.layoutParams =
                CmdIndexLinearWeightParam.listViewLongWeight
        }
    }
}
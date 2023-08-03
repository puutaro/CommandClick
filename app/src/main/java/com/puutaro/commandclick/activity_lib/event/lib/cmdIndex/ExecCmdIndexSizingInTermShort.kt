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
    ) {
        val cmdIndexFragment = try {
            activity.supportFragmentManager.findFragmentByTag(
                activity.getString(R.string.command_index_fragment)
            ) as CommandIndexFragment
        } catch (e: Exception) {
            return
        }
        val binding = cmdIndexFragment.binding
        if(isKeyboardShowing) {
            binding.commandIndexFragment.layoutParams =
                CmdIndexLinearWeightParam.listViewShortWeight
            binding.cmdListSwipeToRefresh.isVisible = false
            return
        }
        binding.cmdListSwipeToRefresh.isVisible = true
        binding.commandIndexFragment.layoutParams =
            CmdIndexLinearWeightParam.listViewLongWeight

    }
}
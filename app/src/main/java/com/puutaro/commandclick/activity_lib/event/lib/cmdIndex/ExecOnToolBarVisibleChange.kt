package com.puutaro.commandclick.activity_lib.event.lib.cmdIndex

import android.view.ViewGroup
import android.widget.LinearLayout
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.TerminalSizingForEdit

class ExecOnToolBarVisibleChange {
    companion object {
        fun execOnToolBarVisibleChange(
            activity: MainActivity,
            toolBarVisible: Boolean
        ){
            val layoutParam = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0
            )
            val commandIndexFragment =
                try {
                    activity.supportFragmentManager.findFragmentByTag(
                        activity.getString(R.string.command_index_fragment)
                    ) as CommandIndexFragment
                } catch (e: Exception) {
                    return
                }
            layoutParam.weight = if(toolBarVisible) {
                TerminalSizingForEdit.VISIBLE.weight
            } else {
                TerminalSizingForEdit.INVISIBLE.weight
            }
            if(!commandIndexFragment.isVisible) return
            commandIndexFragment.binding.commandIndexFragment.layoutParams = layoutParam
        }
    }
}
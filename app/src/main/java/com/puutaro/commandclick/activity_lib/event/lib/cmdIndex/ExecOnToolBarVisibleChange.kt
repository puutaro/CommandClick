package com.puutaro.commandclick.activity_lib.event.lib.cmdIndex

import android.view.ViewGroup
import android.widget.LinearLayout
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.TerminalSizingForEdit

object ExecOnToolBarVisibleChange {
    fun execOnToolBarVisibleChange(
        activity: MainActivity,
        commandIndexFragment: CommandIndexFragment,
        toolBarVisible: Boolean
    ){
        val layoutParam = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            0
        )
        layoutParam.weight = if(toolBarVisible) {
            TerminalSizingForEdit.VISIBLE.weight
        } else {
            TerminalSizingForEdit.INVISIBLE.weight
        }
        if(!commandIndexFragment.isVisible) return
        commandIndexFragment.binding.commandIndexFragment.layoutParams = layoutParam
    }
}
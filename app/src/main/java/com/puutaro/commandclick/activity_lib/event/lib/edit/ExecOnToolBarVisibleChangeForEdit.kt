package com.puutaro.commandclick.activity_lib.event.lib.edit

import android.view.ViewGroup
import android.widget.LinearLayout
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.TerminalSizingForEdit

object ExecOnToolBarVisibleChangeForEdit {
    fun execOnToolBarVisibleChangeForEdit(
        activity: MainActivity,
        editFragment: EditFragment,
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
        if(!editFragment.isVisible) return
        editFragment.binding.editFragment.layoutParams = layoutParam
    }
}

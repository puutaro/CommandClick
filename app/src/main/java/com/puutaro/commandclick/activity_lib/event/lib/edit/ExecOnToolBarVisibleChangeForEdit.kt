package com.puutaro.commandclick.activity_lib.event.lib.edit

import android.view.ViewGroup
import android.widget.LinearLayout
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.TerminalSizingForEdit

class ExecOnToolBarVisibleChangeForEdit {
    companion object {
        fun execOnToolBarVisibleChangeForEdit(
            activity: MainActivity,
            toolBarVisible: Boolean
        ){
            val editFragment =
                try {
                    activity.supportFragmentManager.findFragmentByTag(
                        activity.getString(R.string.cmd_variable_edit_fragment)
                    ) as EditFragment
                } catch (e: Exception) {
                    return
                }
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
}

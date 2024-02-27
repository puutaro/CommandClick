package com.puutaro.commandclick.activity_lib.event.lib.edit

import android.widget.LinearLayout
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.state.TargetFragmentInstance

object ExecTermMinimumForEdit {
    fun min(
        activity: MainActivity
    ){
        val editExecuteTerminalFragmentTag = activity.getString(
            R.string.edit_terminal_fragment
        )
        val editExecuteTerminalFragment =
            TargetFragmentInstance().getFromActivity<TerminalFragment>(
                activity,
                editExecuteTerminalFragmentTag
            ) ?: return
        val param = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0
        )
        val minTermRate = 0F
        param.weight = minTermRate
        editExecuteTerminalFragment.view?.layoutParams = param
    }
}
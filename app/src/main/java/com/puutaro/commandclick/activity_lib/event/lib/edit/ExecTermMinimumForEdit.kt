package com.puutaro.commandclick.activity_lib.event.lib.edit

import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
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
            TargetFragmentInstance.getFromActivity<TerminalFragment>(
                activity,
                editExecuteTerminalFragmentTag
            ) ?: return
        val param = LinearLayoutCompat.LayoutParams(
            LinearLayoutCompat.LayoutParams.MATCH_PARENT,
            0
        )
        val minTermRate = 0F
        param.weight = minTermRate
        editExecuteTerminalFragment.view?.layoutParams = param
    }
}
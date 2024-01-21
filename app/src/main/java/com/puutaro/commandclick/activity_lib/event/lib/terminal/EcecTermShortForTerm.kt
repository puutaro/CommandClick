package com.puutaro.commandclick.activity_lib.event.lib.terminal

import android.widget.LinearLayout
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.fragment.TerminalFragment

object ExecTermShortForTerm {

    fun short(
        activity: MainActivity,
        terminalFragment: TerminalFragment
    ){
        val param = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0
        )
        val shortTermRate = 0.4F
        param.weight = shortTermRate
        terminalFragment.view?.layoutParams = param
    }
}
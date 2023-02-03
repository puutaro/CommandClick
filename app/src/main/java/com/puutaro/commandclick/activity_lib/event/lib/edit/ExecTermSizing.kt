package com.puutaro.commandclick.activity_lib.event.lib.edit

import android.view.ViewGroup
import android.widget.LinearLayout
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.TargetFragmentInstance

class ExecTermSizing {
    companion object {


        fun linearLayoutSizing(
            size: Float
        ): LinearLayout.LayoutParams {
            val linearLayoutShrink = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
            0
            )
            linearLayoutShrink.weight = size
            return linearLayoutShrink
        }


        fun execTermSizing(
            activity: MainActivity,
            size: Float
        ){
            val terminalFragment = TargetFragmentInstance().getFromActivity<TerminalFragment>(
                activity,
                activity.getString(R.string.edit_execute_terminal_fragment)
            )
            if(terminalFragment == null) return
            terminalFragment.binding.terminalWebView.onPause()
            terminalFragment.binding.terminalFragment.layoutParams = linearLayoutSizing(size)
        }
    }
}
package com.puutaro.commandclick.activity_lib.event.lib.edit

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.state.TargetFragmentInstance

object ExecTermSizing {

    fun execTermSizing(
        activity: MainActivity,
        size: Float
    ){
        val terminalFragment = TargetFragmentInstance.getFromActivity<TerminalFragment>(
            activity,
            activity.getString(
                R.string.edit_terminal_fragment
            )
        ) ?: return
        terminalFragment.binding.terminalWebView.onPause()
        terminalFragment.binding.terminalFragment.layoutParams = linearLayoutSizing(size)
    }

    private fun linearLayoutSizing(
        size: Float
    ): LinearLayoutCompat.LayoutParams {
        val linearLayoutShrink = LinearLayoutCompat.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            0
        )
        linearLayoutShrink.weight = size
        return linearLayoutShrink
    }
}
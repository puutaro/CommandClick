package com.puutaro.commandclick.fragment_lib.terminal_fragment

import android.view.MotionEvent
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.ReadLines
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.variable.ChangeTargetFragment
import com.puutaro.commandclick.proccess.IndexOrEditFragment
import com.puutaro.commandclick.view_model.activity.TerminalViewModel

class ToolbarHideShowWhenTermLong {
    companion object {
        fun invoke (
            terminalFragment: TerminalFragment,
            terminalViewModel: TerminalViewModel,
        ){

            val context = terminalFragment.context
            val binding = terminalFragment.binding
            val listener =
                context as? TerminalFragment.OnToolBarVisibleChangeListener
            var oldPositionY = 0f
            with(binding.terminalWebView){
                setOnTouchListener {
                        v, event ->
                    val changeTargetFragment =
                        IndexOrEditFragment(terminalFragment).select()
                    if(
                        terminalFragment.isVisible
                        && terminalViewModel.readlinesNum != ReadLines.SHORTH
                        && changeTargetFragment != null
                    ) {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            oldPositionY = event.rawY
                            v.performClick()
                        }
                        MotionEvent.ACTION_UP -> {
                                execHideShow(
                                    changeTargetFragment,
                                    oldPositionY,
                                    event.rawY,
                                    listener
                                )
                            }
                        }
                    }
                    v.performClick()
                    false
                }
            }
        }
    }
}


private fun execHideShow(
    changeTargetFragment: ChangeTargetFragment,
    oldPositionY: Float,
    rawY: Float,
    listener: TerminalFragment.OnToolBarVisibleChangeListener?
) {
    val oldCurrYDff = oldPositionY - rawY
    if(-250 < oldCurrYDff && oldCurrYDff < -10){
        listener?.onToolBarVisibleChange(
            true,
            changeTargetFragment
        )
    }
    if(oldCurrYDff > 10) {
        listener?.onToolBarVisibleChange(
            false,
            changeTargetFragment
        )
    }
}
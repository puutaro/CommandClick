package com.puutaro.commandclick.fragment_lib.terminal_fragment

import android.view.MotionEvent
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.variant.ReadLines
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.ScrollPosition
import com.puutaro.commandclick.util.ScreenSizeCalculator
import com.puutaro.commandclick.util.TargetFragmentInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


object ToolbarHideShowWhenTermLongAndScrollSave {
    fun invoke (
        terminalFragment: TerminalFragment,
    ){

        val context = terminalFragment.context
        val activity = terminalFragment.activity
        val binding = terminalFragment.binding
        val terminalWebView = binding.terminalWebView
        val listener =
            context as? TerminalFragment.OnToolBarVisibleChangeListener
        var oldPositionY = 0f
        val hideShowThreshold = getScreenHeight(terminalFragment)
        with(binding.terminalWebView){
            setOnTouchListener {
                    v, event ->
                val targetFragmentInstance = TargetFragmentInstance()
                val cmdEditFragmentTag = targetFragmentInstance.getCmdEditFragmentTag(activity)
                val bottomFragment = targetFragmentInstance.getCurrentBottomFragmentInFrag(
                    activity,
                    cmdEditFragmentTag,
                )
                val bottomFragmentWeight = targetFragmentInstance.getCurrentBottomFragmentWeight(bottomFragment)
                if(
                    terminalFragment.isVisible
                    && bottomFragmentWeight != ReadLines.LONGTH
                    && bottomFragment != null
                ) {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            oldPositionY = event.rawY
                            v.performClick()
                        }
                        MotionEvent.ACTION_UP -> {
                            execHideShow(
                                bottomFragment,
                                hideShowThreshold,
                                oldPositionY,
                                event.rawY,
                                listener
                            )
                            val url = terminalWebView.url
                            CoroutineScope(Dispatchers.IO).launch {
                                ScrollPosition.save(
                                    terminalFragment,
                                    url,
                                    terminalWebView.scrollY,
                                    oldPositionY,
                                    event.rawY,
                                )
                            }
                        }
                    }
                }
                v.performClick()
                false
            }
        }
    }
}


private fun execHideShow(
    bottomFragment: Fragment?,
    hideShowThreshold: Int,
    oldPositionY: Float,
    rawY: Float,
    listener: TerminalFragment.OnToolBarVisibleChangeListener?
) {
    val oldCurrYDff = oldPositionY - rawY
    if(hideShowThreshold < oldCurrYDff && oldCurrYDff < -10){
        listener?.onToolBarVisibleChange(
            true,
            bottomFragment
        )
    }
    if(oldCurrYDff > 10) {
        listener?.onToolBarVisibleChange(
            false,
            bottomFragment
        )
    }
}

private fun getScreenHeight(
    terminalFragment: TerminalFragment
): Int {
    val dpHeight = ScreenSizeCalculator.dpHeight(
        terminalFragment
    )
    val hideShowRate =
        if(dpHeight > 670f) 3.0f
        else if(dpHeight > 630) 3.5F
        else 4.0f
    return -(dpHeight / hideShowRate).toInt()
}

package com.puutaro.commandclick.activity_lib.event.lib.common

import android.util.Log
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.puutaro.commandclick.common.variable.ReadLines
import com.puutaro.commandclick.view_model.activity.TerminalViewModel


class ExecTerminalLongOrShort {
    companion object {
        fun <T: Fragment> open(
            fragmentTag: String,
            supportFragmentManager: FragmentManager,
            terminalViewModel: TerminalViewModel,
        ){
            val targetFragment = try {
                supportFragmentManager.findFragmentByTag(fragmentTag) as T
            } catch(e: java.lang.Exception){
                Log.d(this.toString(), "not exist ${fragmentTag}")
                return
            }
            val param = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0
            )
            param.weight = if(terminalViewModel.readlinesNum == ReadLines.SHORTH) {
                terminalViewModel.readlinesNum = ReadLines.LONGTH
                ReadLines.SHORTH
            } else {
                terminalViewModel.readlinesNum = ReadLines.SHORTH
                ReadLines.LONGTH
            }
            targetFragment.view?.layoutParams = param
        }
    }
}
package com.puutaro.commandclick.activity_lib.event.lib.common

import android.view.KeyEvent
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.ReadLines
import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.ExecSetTermSizeForCmdIndexFragment
import com.puutaro.commandclick.util.TargetFragmentInstance
import com.puutaro.commandclick.view_model.activity.TerminalViewModel

class ExecBackstackHandle {
    companion object {
        fun execBackstackHandle(
            keyCode: Int,
            activity: MainActivity,
        ) {
            if(keyCode != KeyEvent.KEYCODE_BACK) return
            DoEexecBackstack(activity)
            return
        }


        private fun DoEexecBackstack(
            activity: MainActivity
        ){
            val supportFragmentManager = activity.supportFragmentManager
            val indexTerminalFragmentTag =  activity.getString(R.string.index_terminal_fragment)
            val editExecuteTerminalFragmentTag =  activity.getString(R.string.edit_execute_terminal_fragment)
            val terminalViewModel: TerminalViewModel =
                ViewModelProvider(activity).get(TerminalViewModel::class.java)
            val readLinesNum = terminalViewModel.readlinesNum
            if(readLinesNum == ReadLines.SHORTH) {
                execPopBackStackImmediate(
                    activity,
                    supportFragmentManager,
                    readLinesNum
                )
                return
            }
            val indexTerminalFragment = TargetFragmentInstance().getFromActivity<TerminalFragment>(
                activity,
                indexTerminalFragmentTag
            )
            val editExecuteTerminalFragment = TargetFragmentInstance().getFromActivity<TerminalFragment>(
                activity,
                editExecuteTerminalFragmentTag
            )
            if(
                indexTerminalFragment == null
                && editExecuteTerminalFragment == null) {
                execPopBackStackImmediate(
                    activity,
                    supportFragmentManager,
                    readLinesNum
                )
            }
            if(
                indexTerminalFragment?.isVisible != true
                && editExecuteTerminalFragment?.isVisible != true) {
                execPopBackStackImmediate(
                    activity,
                    supportFragmentManager,
                    readLinesNum
                )
            }
            if(
                indexTerminalFragment?.isVisible == true
            ){
                execBack(
                    activity,
                    indexTerminalFragment,
                    readLinesNum
                )
                return
            } else if(
                editExecuteTerminalFragment?.isVisible == true
            ){
                execBack(
                    activity,
                    editExecuteTerminalFragment,
                    readLinesNum
                )
                return
            }

            execPopBackStackImmediate(
                activity,
                supportFragmentManager,
                readLinesNum
            )
        }
    }
}


internal fun execBack(
    activity: MainActivity,
    terminalFragment: TerminalFragment,
    readLinesNum: Float
){
    val supportFragmentManager = activity.supportFragmentManager
    val webVeiw = try {
        terminalFragment.binding.terminalWebView
    } catch(e: Exception) {
        execPopBackStackImmediate(
            activity,
            supportFragmentManager,
            readLinesNum
        )
        return
    }
    if(!terminalFragment.isVisible) {
        execPopBackStackImmediate(
            activity,
            supportFragmentManager,
            readLinesNum
        )
        return
    }
    if (webVeiw.canGoBack()) {
        webVeiw.goBack()
        return
    }
    val targetFragmentInstance = TargetFragmentInstance()
    val cmdIndexFragment = targetFragmentInstance.getFromActivity<CommandIndexFragment>(
        activity,
        activity.getString(
            R.string.command_index_fragment
        )
    )
    if(
        cmdIndexFragment != null
        && cmdIndexFragment.isVisible
    ) {
        ExecSetTermSizeForCmdIndexFragment.execSetTermSizeForCmdIndexFragment(
            cmdIndexFragment
        )
        return
    }

    val cmdVariableEditFragmentTag = activity.getString(R.string.cmd_variable_edit_fragment)
    val cmdVariableEditFragment = targetFragmentInstance.getFromActivity<EditFragment>(
        activity,
        activity.getString(
            R.string.cmd_variable_edit_fragment
        )
    )
    if(
        cmdVariableEditFragment != null
        && cmdVariableEditFragment.isVisible
    ) {
        val terminalViewModel: TerminalViewModel =
            ViewModelProvider(activity).get(TerminalViewModel::class.java)
        ExecTerminalLongOrShort.open<EditFragment>(
            cmdVariableEditFragmentTag,
            activity.supportFragmentManager,
            terminalViewModel
        )
    }

}

internal fun execPopBackStackImmediate(
    activity: MainActivity,
    supportFragmentManager: FragmentManager,
    readlinesNum: Float,
){
    if(
        supportFragmentManager.backStackEntryCount == 0
        && readlinesNum == ReadLines.SHORTH
    ) {
        activity.finish()
        return
    }
    supportFragmentManager.popBackStackImmediate()
}

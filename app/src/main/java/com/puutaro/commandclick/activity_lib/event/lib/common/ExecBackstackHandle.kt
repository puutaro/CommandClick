package com.puutaro.commandclick.activity_lib.event.lib.common

import android.view.KeyEvent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.variant.ReadLines
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditLayoutViewHideShow
import com.puutaro.commandclick.proccess.ExecSetTermSizeForCmdIndexFragment
import com.puutaro.commandclick.util.TargetFragmentInstance

object ExecBackstackHandle {
    fun execBackstackHandle(
        keyCode: Int,
        activity: MainActivity,
    ) {
        if(keyCode != KeyEvent.KEYCODE_BACK) return
        doEexecBackstack(activity)
        return
    }


    private fun doEexecBackstack(
        activity: MainActivity
    ){
        val supportFragmentManager = activity.supportFragmentManager
        val currentTerminalFragment = getCurrentTerminalFragment(
            activity
        )
        val targetFragmentInstance = TargetFragmentInstance()
        val cmdVariableEditFragmentTag = targetFragmentInstance.getCmdEditFragmentTag(activity)
        val currentBottomFragment = targetFragmentInstance.getCurrentBottomFragment(
            activity,
            cmdVariableEditFragmentTag
        )
        if(currentBottomFragment == null){
            execPopBackStackImmediate(
                activity,
                supportFragmentManager,
            )
            return
        }
        val currentBottomFragmentWeight = targetFragmentInstance.getCurrentBottomFragmentWeight(
            currentBottomFragment,
        )
        if(currentBottomFragmentWeight == null){
            execPopBackStackImmediate(
                activity,
                supportFragmentManager,
            )
            return
        }
        if(
            currentTerminalFragment == null
            || currentBottomFragmentWeight != ReadLines.SHORTH
        ) {
            execPopBackStackImmediate(
                activity,
                supportFragmentManager,
            )
            return
        }
        execBack(
            activity,
            currentTerminalFragment,
            currentBottomFragment,
            cmdVariableEditFragmentTag,
        )
    }
}


private fun execBack(
    activity: MainActivity,
    terminalFragment: TerminalFragment,
    currentBottomFragment: Fragment,
    cmdVariableEditFragmentTag: String,
){
    val supportFragmentManager = activity.supportFragmentManager
    val webVeiw = try {
        terminalFragment.binding.terminalWebView
    } catch(e: Exception) {
        execPopBackStackImmediate(
            activity,
            supportFragmentManager,
        )
        return
    }
    val enableGoBack = webVeiw.canGoBack()
    if (enableGoBack) {
        webVeiw.goBack()
        terminalFragment.goBackFlag = enableGoBack
        return
    }
    when(currentBottomFragment){
        is CommandIndexFragment -> {
            ExecSetTermSizeForCmdIndexFragment.execSetTermSizeForCmdIndexFragment(
                currentBottomFragment
            )
            return
        }
        is EditFragment -> {
            EditLayoutViewHideShow.exec(
                currentBottomFragment,
                true
            )
            ExecTerminalLongOrShort.open<EditFragment>(
                cmdVariableEditFragmentTag,
                activity.supportFragmentManager,
            )
        }
    }
}


private fun getCurrentTerminalFragment(
    activity: MainActivity
): TerminalFragment? {
    val indexTerminalFragmentTag =  activity.getString(R.string.index_terminal_fragment)
    val editExecuteTerminalFragmentTag =  activity.getString(R.string.edit_execute_terminal_fragment)
    val indexTerminalFragment = TargetFragmentInstance().getFromActivity<TerminalFragment>(
        activity,
        indexTerminalFragmentTag
    )
    if(
        indexTerminalFragment != null
        && indexTerminalFragment.isVisible
    ) return indexTerminalFragment
    val editExecuteTerminalFragment = TargetFragmentInstance().getFromActivity<TerminalFragment>(
        activity,
        editExecuteTerminalFragmentTag
    )
    if(
        editExecuteTerminalFragment != null
        && editExecuteTerminalFragment.isVisible
    ) return editExecuteTerminalFragment
    return null
}


private fun execPopBackStackImmediate(
    activity: MainActivity,
    supportFragmentManager: FragmentManager,
){
    if(
        supportFragmentManager.backStackEntryCount == 0
    ) {
        activity.finish()
        return
    }
    supportFragmentManager.popBackStackImmediate()
}

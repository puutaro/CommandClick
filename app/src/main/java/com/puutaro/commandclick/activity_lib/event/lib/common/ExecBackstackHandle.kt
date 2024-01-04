package com.puutaro.commandclick.activity_lib.event.lib.common

import android.view.KeyEvent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
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
        doExecBackstack(activity)
        return
    }


    private fun doExecBackstack(
        activity: MainActivity
    ){
        val targetFragmentInstance = TargetFragmentInstance()
        val currentTerminalFragment = targetFragmentInstance.getCurrentTerminalFragment(
            activity
        )
        val cmdVariableEditFragmentTag = targetFragmentInstance.getCmdEditFragmentTag(activity)
        val currentBottomFragment = targetFragmentInstance.getCurrentBottomFragment(
            activity,
            cmdVariableEditFragmentTag
        )
        val supportFragmentManager = activity.supportFragmentManager
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
            || currentBottomFragmentWeight == ReadLines.LONGTH
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

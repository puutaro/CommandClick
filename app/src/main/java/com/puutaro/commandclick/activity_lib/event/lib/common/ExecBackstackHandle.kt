package com.puutaro.commandclick.activity_lib.event.lib.common

import android.content.Context
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.variant.ReadLines
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditLayoutViewHideShow
import com.puutaro.commandclick.proccess.monitor.MonitorSizeManager
import com.puutaro.commandclick.util.state.SharePreferenceMethod
import com.puutaro.commandclick.util.state.TargetFragmentInstance

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
        val currentTerminalFragment =
            targetFragmentInstance.getCurrentTerminalFragment(
                activity
            )
        val cmdVariableEditFragmentTag =
            targetFragmentInstance.getCmdEditFragmentTag(activity)
        val currentBottomFragment =
            targetFragmentInstance.getCurrentBottomFragment(
                activity,
                cmdVariableEditFragmentTag
            )
        val supportFragmentManager = activity.supportFragmentManager
        if(
            currentBottomFragment == null
        ){
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
            MonitorSizeManager.changeForCmdIndexFragment(
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
    removeEditAndTermFragment(
        activity,
        supportFragmentManager,
    )
    supportFragmentManager.popBackStackImmediate()
}

private fun removeEditAndTermFragment(
    activity: MainActivity,
    supportFragmentManager: FragmentManager,
){
    val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
    val currentAppDirPath = SharePreferenceMethod.getStringFromSharePreference(
        sharedPref,
        SharePrefferenceSetting.current_app_dir
    )
    val currentFannelName = SharePreferenceMethod.getStringFromSharePreference(
        sharedPref,
        SharePrefferenceSetting.current_fannel_name
    )
    val currentFannelState = SharePreferenceMethod.getStringFromSharePreference(
        sharedPref,
        SharePrefferenceSetting.current_fannel_state
    )
    val targetFragmentInstance = TargetFragmentInstance()
    val currentEditFragment = targetFragmentInstance.getCurrentEditFragmentFromFragment(
        activity,
        currentAppDirPath,
        currentFannelName,
        currentFannelState,
    )
    val currentTerminalFragment = targetFragmentInstance.getCurrentTerminalFragment(
        activity,
    )
    val removeFragmentList = listOf(
        currentEditFragment,
        currentTerminalFragment
    )
    val transaction = supportFragmentManager.beginTransaction()
    removeFragmentList.forEach {
        if(it == null) return@forEach
        transaction.remove(it)
    }
    transaction.commit()
}

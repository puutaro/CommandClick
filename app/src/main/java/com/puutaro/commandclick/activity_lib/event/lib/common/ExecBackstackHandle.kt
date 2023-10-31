package com.puutaro.commandclick.activity_lib.event.lib.common

import android.content.Context
import android.view.KeyEvent
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.variant.ReadLines
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditLayoutViewHideShow
import com.puutaro.commandclick.proccess.ExecSetTermSizeForCmdIndexFragment
import com.puutaro.commandclick.util.FragmentTagManager
import com.puutaro.commandclick.util.SharePreffrenceMethod
import com.puutaro.commandclick.util.TargetFragmentInstance
import com.puutaro.commandclick.view_model.activity.TerminalViewModel

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


internal fun execBack(
    activity: MainActivity,
    terminalFragment: TerminalFragment,
    readLinesNum: Float
){
    val terminalViewModel: TerminalViewModel =
        ViewModelProvider(activity).get(TerminalViewModel::class.java)
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
    val enableGoBack = webVeiw.canGoBack()
    if (enableGoBack) {
        webVeiw.goBack()
        terminalFragment.goBackFlag = enableGoBack
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

    val sharePref = activity.getPreferences(Context.MODE_PRIVATE)
    val cmdVariableEditFragmentTag = FragmentTagManager.makeTag(
        FragmentTagManager.Prefix.cmdEditPrefix.str,
        SharePreffrenceMethod.getStringFromSharePreffrence(
            sharePref,
            SharePrefferenceSetting.current_app_dir
        ),
        SharePreffrenceMethod.getStringFromSharePreffrence(
            sharePref,
            SharePrefferenceSetting.current_script_file_name
        ),
        FragmentTagManager.Suffix.ON.str
    )
    val cmdVariableEditFragment = targetFragmentInstance.getFromActivity<EditFragment>(
        activity,
        cmdVariableEditFragmentTag
    )

    if(
        cmdVariableEditFragment != null
        && cmdVariableEditFragment.isVisible
        && cmdVariableEditFragment.view?.height != 0
    ) {
        EditLayoutViewHideShow.exec(
            cmdVariableEditFragment,
            true
        )
        ExecTerminalLongOrShort.open<EditFragment>(
            cmdVariableEditFragmentTag,
            activity.supportFragmentManager,
            terminalViewModel
        )
    }

}

private fun execPopBackStackImmediate(
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

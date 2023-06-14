package com.puutaro.commandclick.activity_lib.manager

import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.IntentAction
import com.puutaro.commandclick.util.FragmentTagManager
import com.puutaro.commandclick.util.SharePreffrenceMethod
import com.puutaro.commandclick.util.TargetFragmentInstance


class InitFragmentManager(
    private val activity: MainActivity
) {
    private val startUpPref = activity.getPreferences(Context.MODE_PRIVATE)
    private val intent = activity.intent
    private val onShortcut = intent.getStringExtra(
        SharePrefferenceSetting.on_shortcut.name
    ) ?: SharePrefferenceSetting.on_shortcut.defalutStr


    fun registerSharePreferenceFromIntentExtra() {
        val mngr = activity.getSystemService(ACTIVITY_SERVICE) as? ActivityManager
        val normalTaskNum = 1
        val okOneTask = mngr?.appTasks?.size == normalTaskNum

        val disableOneTaskForUrlLaunch = IntentAction.judge(activity) && !okOneTask
        if (
            disableOneTaskForUrlLaunch
        ) {
            removeTask(mngr)
            execUrlIntent()
            return
        }

        val disableTaskRootForUrlLaunch =
            IntentAction.judge(activity) && !activity.isTaskRoot
        if (
            disableTaskRootForUrlLaunch
        ) {
            execUrlIntent()
            return
        }
        execShortcutIntent()
    }


    fun startFragment(
        savedInstanceState: Bundle?,
    ) {
        val startUpScriptFileName = SharePreffrenceMethod.getStringFromSharePreffrence(
            startUpPref,
            SharePrefferenceSetting.current_script_file_name
        )
        val startUpAppDirPath = SharePreffrenceMethod.getStringFromSharePreffrence(
            startUpPref,
            SharePrefferenceSetting.current_app_dir
        )
        val onShortcut = SharePreffrenceMethod.getStringFromSharePreffrence(
            startUpPref,
            SharePrefferenceSetting.on_shortcut
        )

        val emptyShellFileName = CommandClickScriptVariable.EMPTY_STRING

        if (
            startUpScriptFileName.isEmpty()
            || startUpScriptFileName == emptyShellFileName
            || startUpAppDirPath == UsePath.cmdclickAppDirAdminPath
            || startUpAppDirPath == UsePath.cmdclickAppHistoryDirAdminPath
            || startUpAppDirPath == UsePath.cmdclickSystemAppDirPath
            || onShortcut == SharePrefferenceSetting.on_shortcut.defalutStr
        ) {
            WrapFragmentManager.initFragment(
                savedInstanceState,
                activity.supportFragmentManager,
                activity.getString(R.string.index_terminal_fragment),
                activity.getString(R.string.command_index_fragment)
            )
            return
        }
        val cmdVariableEditFragmentTag = FragmentTagManager.makeTag(
            FragmentTagManager.Prefix.cmdEditPrefix.str,
            startUpAppDirPath,
            startUpScriptFileName,
            onShortcut
        )
        val cmdVariableEditFragment = TargetFragmentInstance().getFromActivity<EditFragment>(
            activity,
            cmdVariableEditFragmentTag
        )
        if (cmdVariableEditFragment != null) return
        WrapFragmentManager.changeFragmentEdit(
            activity.supportFragmentManager,
            cmdVariableEditFragmentTag,
            activity.getString(R.string.edit_execute_terminal_fragment),
            true
        )
    }

    private fun execUrlIntent() {
        val execIntent = Intent(activity, activity::class.java)
        execIntent.setAction(Intent.ACTION_VIEW).flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        execIntent.data = Uri.parse(intent?.dataString)
        SharePreffrenceMethod.putSharePreffrence(
            startUpPref,
            mapOf(
                SharePrefferenceSetting.current_script_file_name.name
                        to SharePrefferenceSetting.current_script_file_name.defalutStr,
                SharePrefferenceSetting.on_shortcut.name
                        to SharePrefferenceSetting.on_shortcut.defalutStr
            )
        )
        exedRestartIntent(
            execIntent
        )
    }


    private fun execShortcutIntent() {
        val recieveAppDirPath = intent.getStringExtra(
            SharePrefferenceSetting.current_app_dir.name
        )
        if (
            recieveAppDirPath.isNullOrEmpty()
        ) return

        val currentShellFileName = intent.getStringExtra(
            SharePrefferenceSetting.current_script_file_name.name
        ) ?: SharePrefferenceSetting.current_script_file_name.defalutStr
        SharePreffrenceMethod.putSharePreffrence(
            startUpPref,
            mapOf(
                SharePrefferenceSetting.current_app_dir.name to recieveAppDirPath,
                SharePrefferenceSetting.current_script_file_name.name to currentShellFileName,
                SharePrefferenceSetting.on_shortcut.name
                        to FragmentTagManager.Suffix.ON.name
            )
        )
        exedRestartIntent(
            Intent(activity, activity::class.java)
        )
    }


    private fun exedRestartIntent(
        sendIntent: Intent
    ) {
        activity.finish()
        activity.startActivity(sendIntent)
    }

    private fun removeTask(
        mngr: ActivityManager?
    ) {
        if (mngr == null) return
        mngr.appTasks.forEach {
            it.finishAndRemoveTask()
        }
    }
}

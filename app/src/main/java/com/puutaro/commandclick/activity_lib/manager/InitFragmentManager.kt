package com.puutaro.commandclick.activity_lib.manager

import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.ShortcutOnValueStr
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.IntentAction
import com.puutaro.commandclick.util.SharePreffrenceMethod
import com.puutaro.commandclick.util.TargetFragmentInstance


class InitFragmentManager(
    private val activity: MainActivity
) {
    private val startUpPref = activity.getPreferences(Context.MODE_PRIVATE)
    private val intent = activity.intent
    private val on_shortcut = intent.getStringExtra(
        SharePrefferenceSetting.on_shortcut.name
    ) ?: SharePrefferenceSetting.on_shortcut.defalutStr


    fun registerSharePreferenceFromIntentExtra() {
        if (
            on_shortcut == ShortcutOnValueStr.EDIT_API.name
        ) return
        val mngr = activity.getSystemService(ACTIVITY_SERVICE) as? ActivityManager
        val normalTaskNum = 1
        val okOneTask = mngr?.appTasks?.size == normalTaskNum //?.getRunningTasks(10)

        val disableOneTaskForUrlLaunch = IntentAction.judge(activity) && !okOneTask
        if (
            disableOneTaskForUrlLaunch
        ) {
            removeTask(mngr)
            execUrlIntent()
            return
        }

        val disableTaskRootForUrlLaunch = IntentAction.judge(activity) && !activity.isTaskRoot
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
        if (on_shortcut == ShortcutOnValueStr.EDIT_API.name) {
            WrapFragmentManager.changeFragmentEdit(
                activity.supportFragmentManager,
                activity.getString(R.string.api_cmd_variable_edit_api_fragment),
                String(),
                true
            )
            return
        }
        val startUpShellFileName = SharePreffrenceMethod.getStringFromSharePreffrence(
            startUpPref,
            SharePrefferenceSetting.current_shell_file_name
        )
        val startUpAppDirName = SharePreffrenceMethod.getStringFromSharePreffrence(
            startUpPref,
            SharePrefferenceSetting.current_app_dir
        )
        val on_shortcut = SharePreffrenceMethod.getStringFromSharePreffrence(
            startUpPref,
            SharePrefferenceSetting.on_shortcut
        )

        val emptyShellFileName = CommandClickShellScript.EMPTY_STRING

        if (
            startUpShellFileName == emptyShellFileName
            || startUpAppDirName == UsePath.cmdclickAppDirAdminPath
            || startUpAppDirName == UsePath.cmdclickAppHistoryDirAdminPath
            || startUpAppDirName == UsePath.cmdclickConfigDirPath
            || on_shortcut == SharePrefferenceSetting.on_shortcut.defalutStr
        ) {
            WrapFragmentManager.initFragment(
                savedInstanceState,
                activity.supportFragmentManager,
                activity.getString(R.string.index_terminal_fragment),
                activity.getString(R.string.command_index_fragment)
            )
            return
        }
        val cmdVariableEditFragmentTag = activity.getString(R.string.cmd_variable_edit_fragment)
        val cmdVariableEditFragment = TargetFragmentInstance().getFromActivity<EditFragment>(
            activity,
            cmdVariableEditFragmentTag
        )
        if (cmdVariableEditFragment != null) return
        WrapFragmentManager.changeFragmentEdit(
            activity.supportFragmentManager,
            activity.getString(R.string.cmd_variable_edit_fragment),
            activity.getString(R.string.edit_execute_terminal_fragment),
            true
        )
    }

    private fun execUrlIntent() {
        val execIntent = Intent(activity, activity::class.java)
        execIntent.setAction(Intent.ACTION_VIEW)
            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        execIntent.setData(
            Uri.parse(intent?.dataString)
        )
        SharePreffrenceMethod.putSharePreffrence(
            startUpPref,
            mapOf(
                SharePrefferenceSetting.current_shell_file_name.name
                        to SharePrefferenceSetting.current_shell_file_name.defalutStr,
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
            SharePrefferenceSetting.current_shell_file_name.name
        ) ?: SharePrefferenceSetting.current_shell_file_name.defalutStr
        SharePreffrenceMethod.putSharePreffrence(
            startUpPref,
            mapOf(
                SharePrefferenceSetting.current_app_dir.name to recieveAppDirPath,
                SharePrefferenceSetting.current_shell_file_name.name to currentShellFileName,
                SharePrefferenceSetting.on_shortcut.name
                        to ShortcutOnValueStr.ON.name
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

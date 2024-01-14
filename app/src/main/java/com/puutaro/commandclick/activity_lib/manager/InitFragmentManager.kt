package com.puutaro.commandclick.activity_lib.manager

import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.fannel.SystemFannel
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.WebUrlVariables
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.IntentAction
import com.puutaro.commandclick.util.FragmentTagManager
import com.puutaro.commandclick.util.LoadUrlPrefixSuffix
import com.puutaro.commandclick.util.SharePreffrenceMethod
import com.puutaro.commandclick.util.TargetFragmentInstance


class InitFragmentManager(
    private val activity: MainActivity
) {
    private val activityManager = activity.getSystemService(ACTIVITY_SERVICE) as? ActivityManager
    private val startUpPref = activity.getPreferences(Context.MODE_PRIVATE)
    private val intent = activity.intent
    private val onShortcut = intent.getStringExtra(
        SharePrefferenceSetting.on_shortcut.name
    ) ?: SharePrefferenceSetting.on_shortcut.defalutStr

    fun registerSharePreferenceFromIntentExtra() {
        val normalTaskNum = 1
        val okOneTask = activityManager?.appTasks?.size == normalTaskNum
        val disableOneTaskForUrlLaunch = IntentAction.judge(activity) && !okOneTask
        if (disableOneTaskForUrlLaunch) {
            removeTask(activityManager)
            execUrlIntent()
            return
        }
        val disableTaskRootForUrlLaunch =
            IntentAction.judge(activity) && !activity.isTaskRoot
        if (disableTaskRootForUrlLaunch) {
            execUrlIntent()
            return
        }
        execShortcutIntent()
    }


    fun startFragment(
        savedInstanceState: Bundle?,
    ) {
        val startUpAppDirPath = SharePreffrenceMethod.getStringFromSharePreffrence(
            startUpPref,
            SharePrefferenceSetting.current_app_dir
        )
        val startUpScriptFileName = SharePreffrenceMethod.getStringFromSharePreffrence(
            startUpPref,
            SharePrefferenceSetting.current_script_file_name
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
            || allowJudgeSystemFannelIntent(
                startUpAppDirPath,
                startUpScriptFileName
            )
            || onShortcut != FragmentTagManager.Suffix.ON.name
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
            FragmentTagManager.Suffix.ON.str
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
        setDataString(intent)?.let {
            execIntent.data = it
        }
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

    private fun allowJudgeSystemFannelIntent(
        startUpAppDirPath: String,
        startUpScriptFileName: String,
    ): Boolean {
        if(
            startUpAppDirPath != UsePath.cmdclickSystemAppDirPath
        ) return false
        return !SystemFannel.allowIntentSystemFannelList.contains(
            startUpScriptFileName
        )

    }

    private fun setDataString(
        intent: Intent?,
    ): Uri? {
        intent?.dataString?.let {
           val urlStr = makeUrlStr(it)
           return Uri.parse(urlStr)
        }
        return intent
            ?.extras
            ?.getString(android.app.SearchManager.QUERY)
            ?.let {
                val urlStr = makeUrlStr(it)
                return Uri.parse(urlStr)
        }
    }

    private fun makeUrlStr(urlSrcStr: String?): String? {
        if(
            urlSrcStr.isNullOrEmpty()
        ) return null
        if (
            LoadUrlPrefixSuffix.judge(urlSrcStr)
        ) return urlSrcStr
        return "${WebUrlVariables.queryUrl}${urlSrcStr}"
    }
}

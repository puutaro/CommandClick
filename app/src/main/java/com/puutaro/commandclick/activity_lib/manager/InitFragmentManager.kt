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
import com.puutaro.commandclick.util.state.FragmentTagManager
import com.puutaro.commandclick.util.LoadUrlPrefixSuffix
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.util.state.SharePreferenceMethod
import com.puutaro.commandclick.util.state.TargetFragmentInstance


class InitFragmentManager(
    private val activity: MainActivity
) {
    private val activityManager = activity.getSystemService(ACTIVITY_SERVICE) as? ActivityManager
    private val startUpPref = activity.getPreferences(Context.MODE_PRIVATE)
    private val intent = activity.intent

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
        val startUpAppDirPath = SharePreferenceMethod.getStringFromSharePreference(
            startUpPref,
            SharePrefferenceSetting.current_app_dir
        )
        val startUpScriptFileName = SharePreferenceMethod.getStringFromSharePreference(
            startUpPref,
            SharePrefferenceSetting.current_fannel_name
        )
        val onShortcut = SharePreferenceMethod.getStringFromSharePreference(
            startUpPref,
            SharePrefferenceSetting.on_shortcut
        )
        val fannelState = SharePreferenceMethod.getStringFromSharePreference(
            startUpPref,
            SharePrefferenceSetting.current_fannel_state
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
            || onShortcut != EditFragmentArgs.Companion.OnShortcutSettingKey.ON.key
        ) {
            WrapFragmentManager.initFragment(
                savedInstanceState,
                activity.supportFragmentManager,
                activity.getString(R.string.index_terminal_fragment),
                activity.getString(R.string.command_index_fragment)
            )
            return
        }
        val cmdVariableEditFragmentTag = FragmentTagManager.makeCmdValEditTag(
            startUpAppDirPath,
            startUpScriptFileName,
            fannelState,
        )
        val readSharePreferenceMapForNext = EditFragmentArgs.createReadSharePreferenceMap(
            startUpAppDirPath,
            startUpScriptFileName,
            onShortcut,
            fannelState
        )
        val cmdVariableEditFragment = TargetFragmentInstance().getFromActivity<EditFragment>(
            activity,
            cmdVariableEditFragmentTag
        )
        if (cmdVariableEditFragment != null) return
        WrapFragmentManager.changeFragmentEdit(
            activity.supportFragmentManager,
            cmdVariableEditFragmentTag,
            activity.getString(R.string.edit_terminal_fragment),
            EditFragmentArgs(
                readSharePreferenceMapForNext,
                EditFragmentArgs.Companion.EditTypeSettingsKey.CMD_VAL_EDIT,
            ),
            true
        )
    }

    private fun execUrlIntent() {
        val execIntent = Intent(activity, activity::class.java)
        execIntent.setAction(Intent.ACTION_VIEW).flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        setDataString(intent)?.let {
            execIntent.data = it
        }
        SharePreferenceMethod.putSharePreference(
            startUpPref,
            mapOf(
                SharePrefferenceSetting.current_fannel_name.name
                        to SharePrefferenceSetting.current_fannel_name.defalutStr,
                SharePrefferenceSetting.on_shortcut.name
                        to SharePrefferenceSetting.on_shortcut.defalutStr,
                SharePrefferenceSetting.current_fannel_state.name
                        to SharePrefferenceSetting.current_fannel_state.defalutStr
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
        val fannelState = intent.getStringExtra(
            SharePrefferenceSetting.current_fannel_state.name
        ) ?: SharePrefferenceSetting.current_fannel_state.defalutStr
        if (
            recieveAppDirPath.isNullOrEmpty()
        ) return

        val currentShellFileName = intent.getStringExtra(
            SharePrefferenceSetting.current_fannel_name.name
        ) ?: SharePrefferenceSetting.current_fannel_name.defalutStr
        SharePreferenceMethod.putAllSharePreference(
            startUpPref,
            recieveAppDirPath,
            currentShellFileName,
            EditFragmentArgs.Companion.OnShortcutSettingKey.ON.key,
            fannelState
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

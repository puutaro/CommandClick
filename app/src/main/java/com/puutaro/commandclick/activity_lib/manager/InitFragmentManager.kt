package com.puutaro.commandclick.activity_lib.manager

import android.app.ActivityManager
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Browser
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.broadcast.extra.PocketWebviewExtra
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeTerm
import com.puutaro.commandclick.common.variable.settings.FannelInfoSetting
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.UrlLaunchIntentAction
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.util.state.FragmentTagManager
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import com.puutaro.commandclick.util.url.UrlOrQuery
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


private const val innerUrlIntentFlag = "innerUrlIntent"
object InitFragmentManager{

//    private val activityManager = activity.getSystemService(ACTIVITY_SERVICE) as? ActivityManager
//    private val startUpPref = FannelInfoTool.getSharePref(activity)
//    private val intent = activity.intent
    private val fragmentLaunchDelayTime = 300L

    fun intentHandler(
        activity: MainActivity
    ): Boolean {
        val activityManager = activity.getSystemService(ACTIVITY_SERVICE) as? ActivityManager
        val startUpPref = FannelInfoTool.getSharePref(activity)
        val intent = activity.intent
        val isQueryIntent = QueryIntentHandler.handle(
            activity,
            activityManager
        )
        if(
            isQueryIntent
        ) return true
//        val normalTaskNum = 1
//        val okOneTask =
//            activityManager?.appTasks?.size == normalTaskNum
//        val disableOneTaskForUrlLaunch =
//            UrlLaunchIntentAction.judge(activity) && !okOneTask
        val isFinish = isTwoOverActivity(activityManager)
        val urlStr = intent?.dataString?.let {
            UrlOrQuery.convert(it)
        } ?: return false
//        if (disableOneTaskForUrlLaunch) {
//            launchUrlByPocketWebView(
//                activity,
//                urlStr,
//                isFinish
//            )
////            removeTask(activityManager)
////            execUrlIntent()
//            return isFinish
//        }
//        val disableTaskRootForUrlLaunch =
//            UrlLaunchIntentAction.judge(activity) && !activity.isTaskRoot
//        if (disableTaskRootForUrlLaunch) {
//            launchUrlByPocketWebView(
//                activity,
//                urlStr,
//                isFinish
//            )
////            execUrlIntent()
//            return isFinish
//        }
        if(
            UrlLaunchIntentAction.judge(activity)
        ){
            return when(
                activity.intent.getStringExtra(innerUrlIntentFlag).isNullOrEmpty()
            ) {
                false -> {
                    launchUrlByPocketWebView(
                        activity,
                        urlStr,
//                        true,
                        isFinish
                    )
                    isFinish
                }
                else -> {
                    execMainActivityUrlIntent(
                        activity,
                        startUpPref,
                        urlStr
                    )
                    true
                }
            }
        }
        return execShortcutIntent(activity)
    }

    private object QueryIntentHandler {
        fun handle(
            activity: MainActivity,
            activityManager:  ActivityManager?
        ): Boolean {
            val acIntent = activity.intent
            val appId = acIntent.extras?.getString(
                Browser.EXTRA_APPLICATION_ID,
                null
            )
            val query = acIntent.extras?.getString(
                android.app.SearchManager.QUERY,
                null
            )
            val isAppIdWebSearch =
                appId == activity.packageName
                        && !query.isNullOrEmpty()

            val isQueryWebSearch =
                acIntent.action == Intent.ACTION_WEB_SEARCH
                        && !query.isNullOrEmpty()
//            ToastUtils.showLong(
//                "${acIntent.extras.toString()}|${acIntent.action}|${acIntent.data}|${acIntent.categories}"
//            )
//            ToastUtils.showShort(
//                activityManager?.appTasks?.getOrNull(0)?.taskInfo?.numActivities.toString() +
//                        "|" + activityManager?.appTasks?.size.toString()
//            )
//            return true
            if(
                !isAppIdWebSearch
                && !isQueryWebSearch
            ) return false
            if(!isAppIdWebSearch && isQueryWebSearch){
//                val activityManager = activity.getSystemService(ACTIVITY_SERVICE) as? ActivityManager
//                ToastUtils.showShort(
//                    activityManager?.appTasks?.getOrNull(0)?.taskInfo?.numActivities.toString() +
//                            "|" + activityManager?.appTasks?.size.toString()
//                )
//                ToastUtils.showShort("aaa\n\n")
                execMainActivityUrlIntent(
                    activity,
                    FannelInfoTool.getSharePref(activity),
                    query
                )
                return true
            }
//            val currentFannelNameFannelInfoKey = FannelInfoSetting.current_fannel_name
//            val startUpPref = FannelInfoTool.getSharePref(activity)
//            val currentFannelName = FannelInfoTool.getStringFromFannelInfo(
//                startUpPref,
//                currentFannelNameFannelInfoKey
//            )
//            val isIndex = currentFannelName.isEmpty()
//                || currentFannelName == currentFannelNameFannelInfoKey.defalutStr
            val isFinish = isTwoOverActivity(activityManager)
//            ToastUtils.showShort(
//                "${isInnerWebSearch}|${isOuterWebSearch}|isFinish: ${isFinish}"
//            )
            val urlStr = UrlOrQuery.convert(query)
                ?: return false
            launchUrlByPocketWebView(
                activity,
                urlStr,
//                false,
                isFinish
            )
//            val queryUrl =
//                UrlOrQuery.convert(query) ?: String()
//            BroadcastSender.normalSend(
//                activity,
//                BroadCastIntentSchemeTerm.POCKET_WEBVIEW_LOAD_URL.action,
//                listOf(
//                    PocketWebviewExtra.url.schema to queryUrl
//                )
//            )
//            activity.finish()
//            when(isIndex) {
//                true -> {
////                    BroadCastIntent.sendUrlCon(
////                        activity,
////                        queryUrl
////                    )
//                    BroadcastSender.normalSend(
//                        activity,
//                        BroadCastIntentSchemeTerm.POCKET_WEBVIEW_LOAD_URL.action,
//                        listOf(
//                            PocketWebviewExtra.url.schema to queryUrl
//                        )
//                    )
//                    activity.finish()
//                }
//                else -> execInnerUrlIntent(
//                    activity,
//                    startUpPref,
//                )
//            }
            return isFinish
        }
    }


    fun startFragment(
        activity: MainActivity,
        savedInstanceState: Bundle?,
    ) {
        val startUpPref = FannelInfoTool.getSharePref(activity)
//        val preferenceAppDirPath = FannelInfoTool.getStringFromFannelInfo(
//            startUpPref,
//            FannelInfoSetting.current_app_dir
//        )
        val fannelName = FannelInfoTool.getStringFromFannelInfo(
            startUpPref,
            FannelInfoSetting.current_fannel_name
        )
        val onShortcut = FannelInfoTool.getStringFromFannelInfo(
            startUpPref,
            FannelInfoSetting.on_shortcut
        )
        val fannelState = FannelInfoTool.getStringFromFannelInfo(
            startUpPref,
            FannelInfoSetting.current_fannel_state
        )

        if (
            FannelInfoTool.isEmptyFannelName(fannelName)
//            || preferenceAppDirPath == UsePath.cmdclickAppDirAdminPath
//            || preferenceAppDirPath == UsePath.cmdclickAppHistoryDirAdminPath
//            || allowJudgeSystemFannelIntent(
//                preferenceAppDirPath,
//                fannelName
//            )
            || onShortcut != EditFragmentArgs.Companion.OnShortcutSettingKey.ON.key
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.IO){
                    delay(fragmentLaunchDelayTime)
                }
                withContext(Dispatchers.Main) {
                    WrapFragmentManager.initFragment(
                        savedInstanceState,
                        activity.supportFragmentManager,
                        activity.getString(R.string.index_terminal_fragment),
                        activity.getString(R.string.command_index_fragment)
                    )
                }
            }
            return
        }
        val cmdVariableEditFragmentTag = FragmentTagManager.makeCmdValEditTag(
//            preferenceAppDirPath,
            fannelName,
            fannelState,
        )
        val fannelInfoMapForNext = EditFragmentArgs.createFannelInfoMap(
//            preferenceAppDirPath,
            fannelName,
            onShortcut,
            fannelState
        )
        val cmdVariableEditFragment = TargetFragmentInstance.getFromActivity<EditFragment>(
            activity,
            cmdVariableEditFragmentTag
        )
        if (cmdVariableEditFragment != null) return
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO){
                delay(fragmentLaunchDelayTime)
            }
            withContext(Dispatchers.Main) {
                WrapFragmentManager.changeFragmentEdit(
                    activity.supportFragmentManager,
                    cmdVariableEditFragmentTag,
                    activity.getString(R.string.edit_terminal_fragment),
                    EditFragmentArgs(
                        fannelInfoMapForNext,
                        EditFragmentArgs.Companion.EditTypeSettingsKey.CMD_VAL_EDIT,
                    ),
                    true
                )
            }
        }
    }

//    private fun execUrlIntent() {
//        val execIntent = Intent(activity, activity::class.java)
//        execIntent.setAction(Intent.ACTION_VIEW).flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//        setDataString(intent)?.let {
//            execIntent.data = it
//        }
//        FannelInfoTool.putAllFannelInfo(
//            startUpPref,
////            currentAppDirPath = null,
//            currentFannelName = FannelInfoSetting.current_fannel_name.defalutStr,
//            onShortcutValue = FannelInfoSetting.on_shortcut.defalutStr,
//            currentFannelState = FannelInfoSetting.current_fannel_state.defalutStr
//
//        )
//        execRestartIntent(
//            activity,
//            execIntent
//        )
//    }


    private fun execShortcutIntent(
        activity: MainActivity
    ): Boolean {
        val startUpPref = FannelInfoTool.getSharePref(activity)
        val intent = activity.intent
//        val recieveAppDirPath = intent.getStringExtra(
//            FannelInfoSetting.current_app_dir.name
//        )
        val fannelState = intent.getStringExtra(
            FannelInfoSetting.current_fannel_state.name
        ) ?: FannelInfoSetting.current_fannel_state.defalutStr
//        if (
//            recieveAppDirPath.isNullOrEmpty()
//        ) return false

        val currentShellFileName = intent.getStringExtra(
            FannelInfoSetting.current_fannel_name.name
        ) ?: FannelInfoSetting.current_fannel_name.defalutStr
        FannelInfoTool.putAllFannelInfo(
            startUpPref,
//            recieveAppDirPath,
            currentShellFileName,
            EditFragmentArgs.Companion.OnShortcutSettingKey.ON.key,
            fannelState
        )
        val restartIntent = Intent(activity, activity::class.java)
        restartIntent.setAction(Intent.ACTION_VIEW).flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        execRestartIntent(
            activity,
            restartIntent
        )
        return true
    }

//    private fun removeTask(
//        mngr: ActivityManager?
//    ) {
//        if (mngr == null) return
//        mngr.appTasks.forEach {
//            it.finishAndRemoveTask()
//        }
//    }

//    private fun allowJudgeSystemFannelIntent(
////        preferenceAppDirPath: String,
//        fannelName: String,
//    ): Boolean {
////        if(
////            preferenceAppDirPath != UsePath.cmdclickSystemAppDirPath
////        ) return false
//        return !SystemFannel.allowIntentSystemFannelList.contains(
//            fannelName
//        )
//
//    }

//    private fun setDataString(
//        intent: Intent?,
//    ): Uri? {
//        intent?.dataString?.let {
//           val urlStr = UrlOrQuery.convert(it)
//           return Uri.parse(urlStr)
//        }
//        return setQueryOrUrlIntent(intent)
//    }
}

private fun execMainActivityUrlIntent(
    activity: MainActivity,
    startUpPref: FannelInfoTool.FannelInfoSharePref?,
    query: String?
){
    val execIntent = Intent(activity, activity::class.java)
    execIntent.action = Intent.ACTION_MAIN
    execIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//            execIntent.action = Intent.ACTION_WEB_SEARCH
    execIntent.data = Uri.parse(UrlOrQuery.convert(query))
    execIntent.putExtra(innerUrlIntentFlag, "on")
//            execIntent.putExtra(Browser.EXTRA_APPLICATION_ID, activity.packageName)
//            execIntent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
//                Intent.FLAG_ACTIVITY_CLEAR_TOP
//            execIntent.putExtra(
//                android.app.SearchManager.QUERY,
//                UrlOrQuery.convert(query)
//            )
//    val currentAppDirPath = FannelInfoTool.getStringFromFannelInfo(
//        startUpPref,
//        FannelInfoSetting.current_app_dir
//    )
    FannelInfoTool.putAllFannelInfo(
        startUpPref,
//        currentAppDirPath = currentAppDirPath,
        currentFannelName = FannelInfoSetting.current_fannel_name.defalutStr,
        onShortcutValue = FannelInfoSetting.on_shortcut.defalutStr,
        currentFannelState = FannelInfoSetting.current_fannel_state.defalutStr
    )
    execRestartIntent(
        activity,
        execIntent
    )
}

private fun launchUrlByPocketWebView(
    activity: MainActivity,
    urlStr: String,
//    isSaveHistory: Boolean,
    isFinish: Boolean
){
    if(
        urlStr.isEmpty()
    ) return
    activity.intent.extras?.remove(
        Browser.EXTRA_APPLICATION_ID,
    )
    activity.intent.extras?.remove(
        android.app.SearchManager.QUERY,
    )
    CoroutineScope(Dispatchers.IO).launch {
        withContext(Dispatchers.IO){
            if(
                isFinish
            ) return@withContext
            for(i in 1..40) {
//                ToastUtils.showShort("${i}")
                val terminalFragment =
                    withContext(Dispatchers.Main) {
                        TargetFragmentInstance.getCurrentTerminalFragment(
                            activity
                        )
                    }
                if (
                    terminalFragment != null
                ) break
                delay(200)
            }
        }
//        val onHistorySave = when(isSaveHistory){
//            true -> "on"
//            else -> String()
//        }
        withContext(Dispatchers.IO) {
            BroadcastSender.normalSend(
                activity,
                BroadCastIntentSchemeTerm.POCKET_WEBVIEW_LOAD_URL.action,
                listOf(
                    PocketWebviewExtra.url.schema to urlStr,
                )
            )
        }
        withContext(Dispatchers.Main){
            if(!isFinish) return@withContext
            activity.finish()
        }
    }
}

private fun execRestartIntent(
    activity: MainActivity,
    sendIntent: Intent
) {
    activity.finish()
    activity.startActivity(sendIntent)
}

//private fun setQueryOrUrlIntent(
//    intent: Intent?
//): Uri? {
//    return intent
//        ?.extras
//        ?.getString(android.app.SearchManager.QUERY)
//        ?.let {
//            val urlStr = UrlOrQuery.convert(it)
//            Uri.parse(urlStr)
//        }
//}

private fun isTwoOverActivity(
    mngr: ActivityManager?
): Boolean {
    val isTwoOverTask =
        (mngr?.appTasks?.size ?: 0) > 1
    if(
        isTwoOverTask
    ) return true
    val isTwoOverActivity =
        (mngr?.appTasks?.getOrNull(0)?.taskInfo?.numActivities?: 0) > 1
    if(
        isTwoOverActivity
    ) return true
    return false
}

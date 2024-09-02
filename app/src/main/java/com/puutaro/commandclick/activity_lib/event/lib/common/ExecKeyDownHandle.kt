package com.puutaro.commandclick.activity_lib.event.lib.common

import android.app.ActivityManager
import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.VolumeUtils
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.activity_lib.ExecMainActivityLaunchIntent
import com.puutaro.commandclick.common.variable.settings.FannelInfoSetting
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.history.url_history.UrlHistoryButtonEvent
import com.puutaro.commandclick.util.datetime.LocalDatetimeTool
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import com.puutaro.commandclick.util.url.WebUrlVariables
import java.time.LocalDateTime


object ExecBackstackHandle {
    fun execBackstackHandle(
        keyCode: Int,
        activity: MainActivity,
    ) {
        when(keyCode){
            KeyEvent.KEYCODE_BACK
            -> BackstackManager.exec(activity)
            KeyEvent.KEYCODE_VOLUME_UP
            -> VolumeAdjuster.up()
            KeyEvent.KEYCODE_VOLUME_DOWN
            -> VolumeAdjuster.down()
        }
    }

    fun initPrevBackTime(){
        BackstackManager.initPrevBackTime()
    }

//    fun initBeforeAfterUrlPair(
//        activity: MainActivity
//    ){
//        val isUrlLaunchIntent = UrlLaunchIntentAction.judge(activity)
//        BackstackManager.initBeforeAfterUrlPair(isUrlLaunchIntent)
//    }
}

private object BackstackManager {

    private const val backStackDelaySec = 5

    private var prevBackTime = LocalDateTime.parse("2020-02-15T21:30:50")
//    private var beforeAndAfterUrlPair: Pair<String?, String?> = Pair(null, null)
//    private val monitorUrlPath = WebUrlVariables.monitorUrlPath
//    private val urlPairStopUpdateMark = "STOP_UPDATE"
//    private val urlPairStopPair = Pair(urlPairStopUpdateMark, urlPairStopUpdateMark)

//    fun initBeforeAfterUrlPair(
//        isStop: Boolean
//    ){
//        when(isStop) {
//            true -> beforeAndAfterUrlPair = urlPairStopPair
//            else -> beforeAndAfterUrlPair = Pair(null, null)
//        }
//    }


    fun initPrevBackTime(){
        prevBackTime = LocalDateTime.parse("2020-02-15T21:30:50")
    }

    fun exec(
        activity: MainActivity
    ){
        val currentTerminalFragment =
            TargetFragmentInstance.getCurrentTerminalFragment(
                activity
            )
        val cmdVariableEditFragmentTag =
            TargetFragmentInstance.getCmdEditFragmentTag(activity)
        val currentBottomFragment =
            TargetFragmentInstance.getCurrentBottomFragment(
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
//        val currentBottomFragmentWeight = targetFragmentInstance.getCurrentBottomFragmentWeight(
//            currentBottomFragment,
//        )
//        if(currentBottomFragmentWeight == null){
//            execPopBackStackImmediate(
//                activity,
//                supportFragmentManager,
//            )
//            return
//        }
        execBack(
            activity,
            currentTerminalFragment,
            currentBottomFragment,
//            cmdVariableEditFragmentTag,
        )
//        when(
//            currentTerminalFragment == null
//            || currentBottomFragmentWeight == ReadLines.LONGTH
//        ) {
//            true -> {
//                val curBackstackTime = LocalDateTime.now()
//                if (
//                    !backstackExecuteJudge(
//                        activity,
//                        currentBottomFragment,
//                        curBackstackTime,
//                    )
//                ) {
//                    prevBackTime = curBackstackTime
//                    ToastUtils.showShort("End by double tap")
//                    return
//                }
//                if(
//                    currentBottomFragment is CommandIndexFragment
//                    || activity.supportFragmentManager.backStackEntryCount > 0
//                ) {
//                    execPopBackStackImmediate(
//                        activity,
//                        supportFragmentManager,
//                    )
//                    return
//                }
//                ExecMainActivityLaunchIntent.launch(activity)
//
//            }
//            else -> execBack(
//                activity,
//                currentTerminalFragment,
//                currentBottomFragment,
//                cmdVariableEditFragmentTag,
//            )
//        }
    }

    private fun execBack(
        activity: MainActivity,
        terminalFragment: TerminalFragment?,
        currentBottomFragment: Fragment,
//        cmdVariableEditFragmentTag: String,
    ){
        val supportFragmentManager = activity.supportFragmentManager
        val webVeiw = try {
            terminalFragment?.binding?.terminalWebView
        } catch(e: Exception) {
            execPopBackStackImmediate(
                activity,
                supportFragmentManager,
            )
            return
        }
//        updateBeforeAndAfterUrlPair(webVeiw.url)
        val enableGoBack = webVeiw?.canGoBack() == true
        if (enableGoBack) {
            webVeiw?.goBack()
            terminalFragment?.goBackFlag = true
            return
        }
//        enableLaunchUrlHistory(terminalFragment).let {
//            isLaunchUrlHistory ->
//            if(!isLaunchUrlHistory) return@let
//            updateBeforeAndAfterUrlPair(monitorUrlPath)
//            UrlHistoryButtonEvent(
//                currentBottomFragment,
//            ).invoke()
//            return
//        }

        when(currentBottomFragment){
            is CommandIndexFragment -> {
                val curDatetime = LocalDateTime.now()
                if (
                    LocalDatetimeTool.getDurationSec(prevBackTime, curDatetime) > backStackDelaySec
                ) {
                    UrlHistoryButtonEvent.invoke(currentBottomFragment)
                    prevBackTime = curDatetime
                    return
                }
                execPopBackStackImmediate(
                    activity,
                    supportFragmentManager,
                )
//                MonitorSizeManager.changeForCmdIndexFragment(
//                    currentBottomFragment
//                )
                return
            }
            is EditFragment -> {
                editFragmentBackstackHandle(
                    activity,
                    FannelInfoTool.getOnShortcut(currentBottomFragment.fannelInfoMap) !=
                            EditFragmentArgs.Companion.OnShortcutSettingKey.ON.key
                )

//                val curDatetime = LocalDateTime.now()
//                EditLayoutViewHideShow.exec(
//                    currentBottomFragment,
//                    true
//                )
//                ExecTerminalLongOrShort.open<EditFragment>(
//                    cmdVariableEditFragmentTag,
//                    activity.supportFragmentManager,
//                )
            }
        }
    }

    private fun editFragmentBackstackHandle(
        activity: MainActivity,
        isNotCmdEditShortcut: Boolean
    ){
        val supportFragmentManager = activity.supportFragmentManager
        val currentBackStackCount =
            supportFragmentManager.backStackEntryCount
        if(currentBackStackCount > 1){
            execPopBackStackImmediate(
                activity,
                supportFragmentManager,
            )
            return
        }
        val curDatetime = LocalDateTime.now()
        when(true){
            (currentBackStackCount == 0) -> {
                if(isNotCmdEditShortcut){
                    ExecMainActivityLaunchIntent.launch(activity)
                    return
                }
                if (
                    LocalDatetimeTool.getDurationSec(prevBackTime, curDatetime) > backStackDelaySec
                ) {
                    ToastUtils.showShort("End by more back")
                    prevBackTime = curDatetime
                    return
                }
                ExecMainActivityLaunchIntent.launch(activity)
            }
            (currentBackStackCount == 1) -> {
                val cmdIndexFragment =
                    supportFragmentManager.findFragmentByTag(
                        activity.getString(R.string.command_index_fragment)
                    )
                if(cmdIndexFragment == null) {
                    execPopBackStackImmediate(
                        activity,
                        supportFragmentManager,
                    )
                    return
                }
                if(isNotCmdEditShortcut){
                    execPopBackStackImmediate(
                        activity,
                        supportFragmentManager,
                    )
                    return
                }
                if (LocalDatetimeTool.getDurationSec(prevBackTime, curDatetime) > backStackDelaySec) {
                    ToastUtils.showShort("End by more back")
                    prevBackTime = curDatetime
                    return
                }
                execPopBackStackImmediate(
                    activity,
                    supportFragmentManager,
                )

            }
            else -> {
                execPopBackStackImmediate(
                    activity,
                    supportFragmentManager,
                )
            }
        }
    }

//    private fun enableLaunchUrlHistory(
//        terminalFragment: TerminalFragment,
//    ):Boolean {
//        val disableLaunchUrlHistoryByBackstack =
//            terminalFragment.onLaunchUrlHistoryByBackstack !=
//                    SettingVariableSelects.OnLaunchUrlHistoryByBackstack.ON.name
//        if(
//            disableLaunchUrlHistoryByBackstack
//        ) return false
////        val recentVisitUrl = beforeAndAfterUrlPair.second
//        if(
//            EnableUrlPrefix.isHttpPrefix(recentVisitUrl)
//        ) return true
//        return recentVisitUrl == "${monitorUrlPath}/"
//                && EnableUrlPrefix.isHttpPrefix(beforeAndAfterUrlPair.first)
//
//    }

//    private fun updateBeforeAndAfterUrlPair (
//        newUrl: String?
//    ){
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "mtext.txt").absolutePath,
//            "beforeAndAfterUrlPair: ${beforeAndAfterUrlPair}" + "\n------\n"
//        )
//        if(
//            beforeAndAfterUrlPair == urlPairStopPair
//        ) return
//        val recentInsertUrl = beforeAndAfterUrlPair.second
//        if(
//            recentInsertUrl == newUrl
//        ) return
//        val newBeforeAndAfterUrlPair = Pair(beforeAndAfterUrlPair.second, newUrl)
//        beforeAndAfterUrlPair = newBeforeAndAfterUrlPair
//    }


    private fun backstackExecuteJudge(
        activity: MainActivity,
        currentBottomFragment: Fragment,
        curBackstackTime: LocalDateTime,
    ): Boolean {
        if(
            currentBottomFragment is CommandIndexFragment
        ) return true
        val currentBackStackCount =
            activity.supportFragmentManager.backStackEntryCount
        if(
            currentBackStackCount > 0
        ) return true
        return LocalDatetimeTool.getDurationSec(prevBackTime, curBackstackTime) < 0.7
    }


    private fun execPopBackStackImmediate(
        activity: MainActivity,
        supportFragmentManager: FragmentManager,
    ){
//        initBeforeAfterUrlPair(false)
        activity.intent.replaceExtras(Bundle())
        activity.intent.action = ""
        activity.intent.data = null
        activity.intent.flags = 0
        if(
            supportFragmentManager.backStackEntryCount == 0
        ) {
            val activityManager = activity.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            if(activityManager?.appTasks?.size != 1) {
                val removeTimes = (activityManager?.appTasks?.size ?: 0) - 1
                var times = 0
                activityManager?.appTasks?.reversed()?.forEach {
                    task ->
                    if(
                        removeTimes > times
                    ) return@forEach
                    task.finishAndRemoveTask()
                    times++
                }
            }
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
        val sharedPref = FannelInfoTool.getSharePref(activity)
//        val currentAppDirPath = FannelInfoTool.getStringFromFannelInfo(
//            sharedPref,
//            FannelInfoSetting.current_app_dir
//        )
        val currentFannelName = FannelInfoTool.getStringFromFannelInfo(
            sharedPref,
            FannelInfoSetting.current_fannel_name
        )
        val currentFannelState = FannelInfoTool.getStringFromFannelInfo(
            sharedPref,
            FannelInfoSetting.current_fannel_state
        )
        val currentEditFragment = TargetFragmentInstance.getCurrentEditFragmentFromFragment(
            activity,
//            currentAppDirPath,
            currentFannelName,
            currentFannelState,
        )
        val currentTerminalFragment = TargetFragmentInstance.getCurrentTerminalFragment(
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
}


private object VolumeAdjuster {
    fun up(){
        val curVol = VolumeUtils.getVolume(AudioManager.STREAM_MUSIC)
        val oneUpVol = curVol + 1
        VolumeUtils.setVolume(
            AudioManager.STREAM_MUSIC,
            oneUpVol,
            AudioManager.FLAG_SHOW_UI
        )
    }

    fun down(){
        val curVol = VolumeUtils.getVolume(AudioManager.STREAM_MUSIC)
        val oneDownVol = curVol - 1
        VolumeUtils.setVolume(
            AudioManager.STREAM_MUSIC,
            oneDownVol,
            AudioManager.FLAG_SHOW_UI
        )
    }
}
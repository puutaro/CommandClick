package com.puutaro.commandclick.activity_lib.event.lib.common

import android.media.AudioManager
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.VolumeUtils
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.settings.FannelInfoSetting
import com.puutaro.commandclick.common.variable.variables.WebUrlVariables
import com.puutaro.commandclick.common.variable.variant.ReadLines
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditLayoutViewHideShow
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.EnableUrlPrefix
import com.puutaro.commandclick.proccess.history.UrlHistoryButtonEvent
import com.puutaro.commandclick.proccess.monitor.MonitorSizeManager
import com.puutaro.commandclick.util.datetime.LocalDatetimeTool
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance
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



}

private object BackstackManager {

    private var prevBackTime = LocalDateTime.parse("2020-02-15T21:30:50")
    private var beforeAndAfterUrlPair: Pair<String?, String?> = Pair(null, null)
    private val monitorUrlPath = WebUrlVariables.monitorUrlPath

    fun exec(
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
        when(
            currentTerminalFragment == null
            || currentBottomFragmentWeight == ReadLines.LONGTH
        ) {
            true -> {
                val curBackstackTime = LocalDateTime.now()
                if (
                    !backstackExecuteJudge(
                        activity,
                        curBackstackTime,
                    )
                ) {
                    prevBackTime = curBackstackTime
                    ToastUtils.showShort("End by double tap")
                    return
                }
                execPopBackStackImmediate(
                    activity,
                    supportFragmentManager,
                )
            }
            else -> execBack(
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
        updateBeforeAndAfterUrlPair(webVeiw.url)
        val enableGoBack = webVeiw.canGoBack()
        if (enableGoBack) {
            webVeiw.goBack()
            terminalFragment.goBackFlag = true
            return
        }
        enableLaunchUrlHistory(terminalFragment).let {
            isLaunchUrlHistory ->
            if(!isLaunchUrlHistory) return@let
            updateBeforeAndAfterUrlPair(monitorUrlPath)
            launchUrlHistory(
                activity,
                currentBottomFragment,
            )
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

    private fun enableLaunchUrlHistory(
        terminalFragment: TerminalFragment,
    ):Boolean {
        val disableLaunchUrlHistoryByBackstack =
            terminalFragment.onLaunchUrlHistoryByBackstack !=
                    SettingVariableSelects.OnLaunchUrlHistoryByBackstack.ON.name
        if(
            disableLaunchUrlHistoryByBackstack
        ) return false
        return EnableUrlPrefix.isHttpPrefix(beforeAndAfterUrlPair.first)
                && "${beforeAndAfterUrlPair.second}" == "${monitorUrlPath}/"
    }

    private fun updateBeforeAndAfterUrlPair (
        newUrl: String?
    ){
        val newBeforeAndAfterUrlPair = Pair(beforeAndAfterUrlPair.second, newUrl)
        beforeAndAfterUrlPair = newBeforeAndAfterUrlPair
    }


    private fun backstackExecuteJudge(
        activity: MainActivity,
        curBackstackTime: LocalDateTime,
    ): Boolean {
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

    private fun launchUrlHistory(
        activity: MainActivity,
        currentBottomFragment: Fragment,
    ){
        val sharePref = FannelInfoTool.getSharePref(activity)
        val fannelInfoMap = FannelInfoSetting.values().map {
            it.name to FannelInfoTool.getStringFromFannelInfo(
                sharePref,
                it
            )
        }.toMap()
        UrlHistoryButtonEvent(
            currentBottomFragment,
            fannelInfoMap,
        ).invoke()
    }

    private fun removeEditAndTermFragment(
        activity: MainActivity,
        supportFragmentManager: FragmentManager,
    ){
        val sharedPref = FannelInfoTool.getSharePref(activity)
        val currentAppDirPath = FannelInfoTool.getStringFromFannelInfo(
            sharedPref,
            FannelInfoSetting.current_app_dir
        )
        val currentFannelName = FannelInfoTool.getStringFromFannelInfo(
            sharedPref,
            FannelInfoSetting.current_fannel_name
        )
        val currentFannelState = FannelInfoTool.getStringFromFannelInfo(
            sharedPref,
            FannelInfoSetting.current_fannel_state
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
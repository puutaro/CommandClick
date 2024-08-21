package com.puutaro.commandclick.activity_lib.event.lib.terminal

import android.widget.Toast
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.proccess.monitor.MonitorSizeManager
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ExecMonitorSizeChangeForTerm {
    fun change(
        activity: MainActivity,
        fannelInfoMap: Map<String, String>,
    ){
        try {
            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.Main) {
                    execChange(
                        activity,
                        fannelInfoMap
                    )
                }
            }
        } catch (e: Exception){
            Toast.makeText(
                activity,
                e.toString(),
                Toast.LENGTH_SHORT
            ).show()
            LogSystems.stdErr(
                activity,
                e.toString()
            )
        }
    }
    private fun execChange(
        activity: MainActivity,
        fannelInfoMap: Map<String, String>,
    ){
        val targetFragmentInstance = TargetFragmentInstance()
//        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//            fannelInfoMap
//        )
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
        val currentFannelState = FannelInfoTool.getCurrentStateName(
            fannelInfoMap
        )

        val cmdIndexFragment =
            targetFragmentInstance.getCmdIndexFragment(activity)
        if(cmdIndexFragment != null){
            MonitorSizeManager.changeForCmdIndexFragment(
                cmdIndexFragment,
            )
            return
        }
        val editFragment = targetFragmentInstance.getCurrentEditFragmentFromActivity(
            activity,
//            currentAppDirPath,
            currentFannelName,
            currentFannelState
        ) ?: return
        MonitorSizeManager.changeForEdit(
            editFragment
        )
    }
}
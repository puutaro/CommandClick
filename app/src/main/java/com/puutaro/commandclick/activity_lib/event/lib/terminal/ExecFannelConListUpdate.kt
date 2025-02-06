package com.puutaro.commandclick.activity_lib.event.lib.terminal

import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance

object ExecFannelConListUpdate {
    fun update(
        activity: MainActivity,
        fannelInfoMap: HashMap<String, String>,
        updateFannelConList: List<String>
    ){
//        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//            fannelInfoMap
//        )
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
        val fannelState = FannelInfoTool.getCurrentStateName(
            fannelInfoMap
        )
        val editFragment = TargetFragmentInstance.getCurrentEditFragmentFromActivity(
            activity,
//            currentAppDirPath,
            currentFannelName,
            fannelState
        ) ?: return
        editFragment.currentFannelConList = updateFannelConList
    }
}
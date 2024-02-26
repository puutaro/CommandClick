package com.puutaro.commandclick.activity_lib.event.lib.terminal

import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.util.state.FannelPrefGetter
import com.puutaro.commandclick.util.state.TargetFragmentInstance

object ExecFannelConListUpdate {
    fun update(
        activity: MainActivity,
        readSharePreffernceMap: Map<String, String>,
        updateFannelConList: List<String>
    ){
        val currentAppDirPath = FannelPrefGetter.getCurrentAppDirPath(
            readSharePreffernceMap
        )
        val currentFannelName = FannelPrefGetter.getCurrentFannelName(
            readSharePreffernceMap
        )
        val fannelState = FannelPrefGetter.getCurrentStateName(
            readSharePreffernceMap
        )
        val editFragment = TargetFragmentInstance().getCurrentEditFragmentFromActivity(
            activity,
            currentAppDirPath,
            currentFannelName,
            fannelState
        ) ?: return
        editFragment.currentFannelConList = updateFannelConList
    }
}
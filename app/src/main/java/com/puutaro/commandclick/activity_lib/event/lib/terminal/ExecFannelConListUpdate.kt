package com.puutaro.commandclick.activity_lib.event.lib.terminal

import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.util.state.SharePreferenceMethod
import com.puutaro.commandclick.util.state.TargetFragmentInstance

object ExecFannelConListUpdate {
    fun update(
        activity: MainActivity,
        readSharePreffernceMap: Map<String, String>,
        updateFannelConList: List<String>
    ){
        val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_app_dir
        )
        val currentFannelName = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_fannel_name
        )
        val fannelState = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_fannel_state
        )
        val editFragment = TargetFragmentInstance().getCurrentEditFragmentFromActivity(
            activity,
            currentAppDirPath,
            currentFannelName,
            fannelState
        ) ?: return
        editFragment.currentScriptContentsList = updateFannelConList
    }
}
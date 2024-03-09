package com.puutaro.commandclick.activity_lib.event.lib.terminal

import android.content.Context
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.OkButtonHandler
import com.puutaro.commandclick.util.state.SharePrefTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance

object ExecSettingOkButton {
    fun handle(
        activity: MainActivity
    ){
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
        val currentAppDirPath = SharePrefTool.getStringFromSharePref(
            sharedPref,
            SharePrefferenceSetting.current_app_dir
        )
        val currentFannelName = SharePrefTool.getStringFromSharePref(
            sharedPref,
            SharePrefferenceSetting.current_fannel_name
        )
        val currentFannelState = SharePrefTool.getStringFromSharePref(
            sharedPref,
            SharePrefferenceSetting.current_fannel_state
        )
        val targetFragmentInstance = TargetFragmentInstance()
        val currentEditFragment = targetFragmentInstance.getCurrentEditFragmentFromFragment(
            activity,
            currentAppDirPath,
            currentFannelName,
            currentFannelState,
        ) ?: return
        OkButtonHandler.cmdValSaveAndBack(
            currentEditFragment
        )

    }
}
package com.puutaro.commandclick.activity_lib.event.lib.terminal

import android.content.Context
import android.util.Log
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.util.edit_tool.CcEditComponent
import com.puutaro.commandclick.util.state.SharePrefTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object EditTextUpdaterForTerminalFragment {
    fun update(
        activity: MainActivity,
        editTextId: Int?,
        variableValue: String
    ) {
        if(editTextId == null) return
        val sharePref = activity.getPreferences(Context.MODE_PRIVATE)
        val currentAppDirPath = SharePrefTool.getStringFromSharePref(
            sharePref,
            SharePrefferenceSetting.current_app_dir
        )
        val currentFannelName = SharePrefTool.getStringFromSharePref(
            sharePref,
            SharePrefferenceSetting.current_fannel_name
        )
        val fannelState = SharePrefTool.getStringFromSharePref(
            sharePref,
            SharePrefferenceSetting.current_fannel_state
        )
        val editExecuteFragment = TargetFragmentInstance().getCurrentEditFragmentFromActivity(
            activity,
            currentAppDirPath,
            currentFannelName,
            fannelState
        ) ?: return
        try {
            CoroutineScope(Dispatchers.Main).launch {
                val editTextInEditFragment =
                    CcEditComponent.findEditTextView(
                        editTextId,
                        CcEditComponent.makeEditLinearLayoutList(editExecuteFragment)
                    ) ?: return@launch
                editTextInEditFragment.setText(variableValue)
            }
        } catch(e: Exception){
            Log.e("edit", e.toString())
        }
    }
}
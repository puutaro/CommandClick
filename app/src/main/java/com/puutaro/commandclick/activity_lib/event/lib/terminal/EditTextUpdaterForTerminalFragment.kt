package com.puutaro.commandclick.activity_lib.event.lib.terminal

import android.content.Context
import android.util.Log
import android.widget.EditText
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.util.state.SharePreferenceMethod
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
        val editExecuteFragment = TargetFragmentInstance().getCurrentEditFragmentFromActivity(
            activity,
            SharePreferenceMethod.getStringFromSharePreference(
                sharePref,
                SharePrefferenceSetting.current_app_dir
            ),
            SharePreferenceMethod.getStringFromSharePreference(
                sharePref,
                SharePrefferenceSetting.current_fannel_name
            )
        ) ?: return
        val binding = editExecuteFragment.binding
        val editLinearLayout = binding.editLinearLayout
        try {
            CoroutineScope(Dispatchers.Main).launch {
                val editTextInEditFragment =
                    editLinearLayout.findViewById<EditText>(editTextId)
                        ?: return@launch
                editTextInEditFragment.setText(variableValue)
            }
        } catch(e: Exception){
            Log.e("edit", e.toString())
        }
    }
}
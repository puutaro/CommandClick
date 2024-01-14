package com.puutaro.commandclick.activity_lib.event.lib.terminal

import android.content.Context
import android.util.Log
import android.widget.EditText
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.util.state.FragmentTagManager
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
        val cmdEditFragmentTag = FragmentTagManager.makeTag(
            FragmentTagManager.Prefix.cmdEditPrefix.str,
            SharePreferenceMethod.getStringFromSharePreference(
                sharePref,
                SharePrefferenceSetting.current_app_dir
            ),
            SharePreferenceMethod.getStringFromSharePreference(
                sharePref,
                SharePrefferenceSetting.current_fannel_name
            ),
            FragmentTagManager.Suffix.ON.str
        )
        val editExecuteFragment = TargetFragmentInstance().getFromActivity<EditFragment>(
            activity,
            cmdEditFragmentTag
        )
        val binding = editExecuteFragment?.binding ?: return
        val editLinearLayout = binding.editLinearLayout
        try {
            CoroutineScope(Dispatchers.Main).launch {
                val editTextInEditFragment = editLinearLayout.findViewById<EditText>(editTextId)
                editTextInEditFragment.setText(variableValue)
            }
        } catch(e: Exception){
            Log.e("edit", e.toString())
        }
    }
}
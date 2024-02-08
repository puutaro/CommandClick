package com.puutaro.commandclick.activity_lib.event.lib.terminal

import android.content.Context
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.util.state.SharePreferenceMethod
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object EditableSpinnerUpdaterForTerminalFragment {
    fun update(
        activity: MainActivity,
        spinnerId: Int?,
        variableValue: String
    ) {
        if(spinnerId == null) return
        val throughMark = "-"
        val sharePref = activity.getPreferences(Context.MODE_PRIVATE)
        val currentAppDirPath = SharePreferenceMethod.getStringFromSharePreference(
            sharePref,
            SharePrefferenceSetting.current_app_dir
        )
        val currentFannelName = SharePreferenceMethod.getStringFromSharePreference(
            sharePref,
            SharePrefferenceSetting.current_fannel_name
        )
        val fannelState = SharePreferenceMethod.getStringFromSharePreference(
            sharePref,
            SharePrefferenceSetting.fannel_state
        )
        val editExecuteFragment = TargetFragmentInstance().getCurrentEditFragmentFromActivity(
            activity,
            currentAppDirPath,
            currentFannelName,
            fannelState
        )
        val binding = editExecuteFragment?.binding ?: return
        val editLinearLayout = binding.editLinearLayout
        try {
            val spinnerInEditFragment = editLinearLayout.findViewById<Spinner>(spinnerId)
            val adapter = spinnerInEditFragment.adapter as ArrayAdapter<String>

            val adapterSize = adapter.count
            val adapterList =
                (0 until adapterSize).map {
                    adapter.getItem(it).toString()
                }
            val selectUpdatedSpinnerList = if(
                variableValue == throughMark
            ){
                listOf(throughMark) + adapterList.filter {
                    it != variableValue
                }
            } else listOf(
                throughMark,
                variableValue,
            ) + adapterList.filter {
                it != throughMark
                        && it != variableValue
            }
            Toast.makeText(
                activity,
                spinnerId.toString(),
                Toast.LENGTH_LONG
            ).show()
            CoroutineScope(Dispatchers.Main).launch {
                execSpinnerUpdate(
                    spinnerInEditFragment,
                    selectUpdatedSpinnerList,
                    adapter
                )
            }
        } catch(e: Exception){
            Log.e("edit", e.toString())
            Toast.makeText(
                activity,
                e.toString(),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun execSpinnerUpdate(
        spinnerInEditFragment: Spinner,
        selectUpdatedSpinnerList: List<String>,
        adapter: ArrayAdapter<String>
    ){
        adapter.clear()
        adapter.addAll(selectUpdatedSpinnerList)
        adapter.notifyDataSetChanged()
        spinnerInEditFragment.setSelection(0)
    }
}

package com.puutaro.commandclick.activity_lib.event.lib.terminal

import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.util.TargetFragmentInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object SpinnerUpdaterForTerminalFragment {
    fun update(
        activity: MainActivity,
        spinnerId: Int?,
        variableValue: String
    ) {
        if(spinnerId == null) return
        val editExecuteFragment = TargetFragmentInstance().getFromActivity<EditFragment>(
            activity,
            activity.getString(R.string.cmd_variable_edit_fragment)
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

            val selectUpdatedSpinnerList = listOf(
                variableValue,
            ) + adapterList.filter { it != variableValue }
            CoroutineScope(Dispatchers.Main).launch {
                execSpinnerUpdate(
                    spinnerInEditFragment,
                    selectUpdatedSpinnerList,
                    adapter
                )
            }
        } catch(e: Exception){
            Log.e("edit", e.toString())
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
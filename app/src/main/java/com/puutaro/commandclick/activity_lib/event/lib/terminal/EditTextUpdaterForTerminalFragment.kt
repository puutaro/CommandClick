package com.puutaro.commandclick.activity_lib.event.lib.terminal

import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.compose.foundation.text.selection.DisableSelection
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.activity_lib.manager.WrapFragmentManager
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.util.TargetFragmentInstance
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
        val editExecuteFragment = TargetFragmentInstance().getFromActivity<EditFragment>(
            activity,
            activity.getString(R.string.cmd_variable_edit_fragment)
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
package com.puutaro.commandclick.activity_lib.event

import androidx.fragment.app.Fragment
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.activity_lib.event.lib.cmdIndex.ExecUrlLoadFragmentProccess
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment

object ExecTermLongChangeHandlerForTerm {
    fun handle(
        activity: MainActivity,
        bottomFragment: Fragment?
    ){
        when(bottomFragment) {
            is CommandIndexFragment ->
                ExecUrlLoadFragmentProccess.execUrlLoadCmdIndexFragment(
                    activity,
                )
            is EditFragment ->
                ExecUrlLoadFragmentProccess.execUrlLoadCmdVriableEditFragment(
                    activity,
                )
            else -> {}
        }
    }
}
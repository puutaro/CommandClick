package com.puutaro.commandclick.activity_lib.event

import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.activity_lib.event.lib.cmdIndex.ExecUrlLoadFragmentProccess
import com.puutaro.commandclick.fragment_lib.terminal_fragment.variable.ChangeTargetFragment

object ExecTermLongChangeHandlerForTerm {
    fun handle(
        activity: MainActivity,
        changeTargetFragment: ChangeTargetFragment?
    ){
        when(changeTargetFragment) {
            ChangeTargetFragment.CMD_INDEX_FRAGMENT -> {
                ExecUrlLoadFragmentProccess.execUrlLoadCmdIndexFragment(
                    activity,
                )
            }
            ChangeTargetFragment.CMD_VARIABLES_EDIT_FRAGMENT -> {
                ExecUrlLoadFragmentProccess.execUrlLoadCmdVriableEditFragment(
                    activity,
                )
            }
            else -> {}
        }
    }
}
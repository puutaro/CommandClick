package com.puutaro.commandclick.activity_lib.event

import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.activity_lib.event.lib.cmdIndex.ExecUrlloadFragmentProccess
import com.puutaro.commandclick.fragment_lib.terminal_fragment.variable.ChangeTargetFragment

class ExecTermLongChangeHandlerForTerm {
    companion object {
        fun handle(
            activity: MainActivity,
            changeTargetFragment: ChangeTargetFragment?
        ){
            when(changeTargetFragment) {
                ChangeTargetFragment.CMD_INDEX_FRAGMENT -> {
                    ExecUrlloadFragmentProccess.execUrlLoadCmdIndexFragment(
                        activity,
                    )
                }
                ChangeTargetFragment.CMD_VARIABLES_EDIT_FRAGMENT -> {
                    ExecUrlloadFragmentProccess.execUrlLoadCmdVriableEditFragment(
                        activity,
                    )
                }
                else -> {}
            }
        }
    }
}
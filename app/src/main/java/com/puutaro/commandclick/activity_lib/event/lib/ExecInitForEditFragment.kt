package com.puutaro.commandclick.activity_lib.event.lib

import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.activity_lib.event.lib.edit.ExecTermSizing
import com.puutaro.commandclick.activity_lib.variable.TerminalFragmentSize
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.EditInitType

object ExecInitForEditFragment {
    fun execInitForEditFragment (
        activity: MainActivity,
        editInitType: EditInitType,
    ){
        when(editInitType) {
            EditInitType.TERMINAL_SHRINK -> {

                ExecTermSizing.execTermSizing(
                    activity,
                    TerminalFragmentSize.SHRINK.size
                )
            }
            EditInitType.TERMINAL_SHOW -> {
                ExecTermSizing.execTermSizing(
                    activity,
                    TerminalFragmentSize.SHOW.size
                )
            }
        }
    }
}
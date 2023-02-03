package com.puutaro.commandclick.activity_lib.event.lib.cmdIndex

import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.activity_lib.manager.WrapFragmentManager


class ExecCommandEdit {
    companion object {
        fun execCommandEdit(
            activity: MainActivity,
            editFragmentTag: String,
            onOpenTerminal: Boolean = false,
            terminalFragmentTagSource: String? = null
        ){
            val context = activity.applicationContext
            val cmdEditFragmentTagName = context.getString(R.string.cmd_variable_edit_fragment)
            val terminalFragmentTag = if(
                onOpenTerminal
                && editFragmentTag == cmdEditFragmentTagName
                && !terminalFragmentTagSource.isNullOrEmpty()
            ) {
                terminalFragmentTagSource
            } else {
                String()
            }
            WrapFragmentManager.changeFragmentEdit(
                activity.supportFragmentManager,
                editFragmentTag,
                terminalFragmentTag,
            )
        }
    }
}

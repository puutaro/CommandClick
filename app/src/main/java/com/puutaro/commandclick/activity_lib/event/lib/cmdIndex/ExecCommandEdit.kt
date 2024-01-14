package com.puutaro.commandclick.activity_lib.event.lib.cmdIndex

import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.activity_lib.manager.WrapFragmentManager
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.util.state.FragmentTagManager


object ExecCommandEdit {
    fun execCommandEdit(
        activity: MainActivity,
        editFragmentTag: String,
        editFragmentArgs: EditFragmentArgs,
        onOpenTerminal: Boolean = false,
        terminalFragmentTagSource: String? = null
    ){
        val terminalFragmentTag = if(
            onOpenTerminal
            && editFragmentTag.startsWith(FragmentTagManager.Prefix.cmdEditPrefix.str)
            && editFragmentTag.endsWith(FragmentTagManager.Suffix.ON.str)
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
            editFragmentArgs,
        )
    }
}

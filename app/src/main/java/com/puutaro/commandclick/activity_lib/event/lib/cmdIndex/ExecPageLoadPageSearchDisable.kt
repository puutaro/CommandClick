package com.puutaro.commandclick.activity_lib.event.lib.cmdIndex

import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.proccess.CmdIndexToolbarSwitcher
import com.puutaro.commandclick.util.TargetFragmentInstance

class ExecPageLoadPageSearchDisable {
    companion object {
        fun change(
            activity: MainActivity,
        ){
            val cmdIndexCommandIndexFragment =
                TargetFragmentInstance()
                    .getFromFragment<CommandIndexFragment>(
                        activity,
                        activity.getString(R.string.command_index_fragment)
                    )
            CmdIndexToolbarSwitcher.switch(
                cmdIndexCommandIndexFragment,
                false
            )
        }
    }
}
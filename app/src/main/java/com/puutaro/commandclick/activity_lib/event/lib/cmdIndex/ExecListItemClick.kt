package com.puutaro.commandclick.activity_lib.event.lib.cmdIndex

import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.activity_lib.manager.WrapFragmentManager

object ExecListItemClick {
    fun invoke(
        activity: MainActivity,
    ) {
        WrapFragmentManager.changeFragmentAtListItemClicked(
            activity.supportFragmentManager,
            activity.getString(R.string.index_terminal_fragment),
        )
    }
}
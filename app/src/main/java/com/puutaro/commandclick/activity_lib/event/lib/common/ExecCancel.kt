package com.puutaro.commandclick.activity_lib.event.lib.common

import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.activity_lib.manager.WrapFragmentManager

object ExecCancel {
    fun execCancel(
        activity: MainActivity,
    ) {

        val context = activity.applicationContext
        val supportFragmentManagerInctance = activity.supportFragmentManager

        WrapFragmentManager.changeFragmentAtCancelClickWhenConfirm(
            supportFragmentManagerInctance,
            context.getString(R.string.index_terminal_fragment),
            context.getString(R.string.command_index_fragment),
        )
    }
}

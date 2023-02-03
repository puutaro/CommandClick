package com.puutaro.commandclick.activity_lib.event.lib.cmdIndex

import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.activity_lib.manager.WrapFragmentManager

class ExecListItemClick {
    companion object {
        fun invoke(
            activity: MainActivity,
            curentFragmentTag: String
        ) {
            val appDirAdminTag = activity.getString(R.string.app_dir_admin)
            if(
                curentFragmentTag == appDirAdminTag
            ) {
                WrapFragmentManager.changeFragmentAtCancelClickWhenConfirm(
                    activity.supportFragmentManager,
                    activity.getString(R.string.index_terminal_fragment),
                    activity.getString(R.string.command_index_fragment),
                )
            } else {
                WrapFragmentManager.changeFragmentAtListItemClicked(
                    activity.supportFragmentManager,
                    activity.getString(R.string.index_terminal_fragment),
                )
            }
        }
    }
}
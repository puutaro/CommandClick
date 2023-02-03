package com.puutaro.commandclick.activity_lib.event.lib.app_some_admin

import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.activity_lib.manager.WrapFragmentManager

class ExecSomeAdmin {
    companion object {
        fun execSomeAdmin(
            activity: MainActivity,
            someAdminTag: String
        ){
            WrapFragmentManager.changeFragmentAppDirAdmin(
                activity.supportFragmentManager,
                someAdminTag,
            )
        }
    }
}
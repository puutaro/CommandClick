package com.puutaro.commandclick.activity_lib.event.lib.edit

import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.activity_lib.manager.WrapFragmentManager
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.util.SharePreffrenceMethod

class ExecOkForEdit{
    companion object {
        fun execOkForEdit(
            activity: MainActivity,
            callOwnerFragmentTag : String?,
            readSharePreffernceMap: Map<String, String>,
        ) {

            val supportFragmentManager = activity.supportFragmentManager
            if(
                SharePreffrenceMethod.getReadSharePreffernceMap(
                    readSharePreffernceMap,
                    SharePrefferenceSetting.current_app_dir
                ) == UsePath.cmdclickAppDirAdminPath) {
                supportFragmentManager.popBackStackImmediate()
                return
            }
            val context = activity.applicationContext
            if(
                callOwnerFragmentTag != activity.getString(
                    R.string.setting_variable_edit_fragment
                )
            ) {
                supportFragmentManager.popBackStackImmediate()
                return
            }
            WrapFragmentManager.changeFragmentAtCancelClickWhenConfirm(
                supportFragmentManager,
                context.getString(R.string.index_terminal_fragment),
                context.getString(R.string.command_index_fragment),
            )
        }
    }
}
package com.puutaro.commandclick.activity_lib.event.lib.edit

import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.activity_lib.manager.WrapFragmentManager
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.state.FragmentTagPrefix
import com.puutaro.commandclick.util.state.FannelInfoTool

object ExecOkForEdit{
    fun execOkForEdit(
        activity: MainActivity,
        callOwnerFragmentTag : String?,
        fannelInfoMap: Map<String, String>,
    ) {
        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
            fannelInfoMap
        )
        val supportFragmentManager = activity.supportFragmentManager
        if(
            currentAppDirPath == UsePath.cmdclickAppDirAdminPath
        ) {
            supportFragmentManager.popBackStackImmediate()
            return
        }
        val context = activity.applicationContext
        if(
            callOwnerFragmentTag?.startsWith(
                FragmentTagPrefix.Prefix.SETTING_VAL_EDIT_PREFIX.str
            ) != true
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
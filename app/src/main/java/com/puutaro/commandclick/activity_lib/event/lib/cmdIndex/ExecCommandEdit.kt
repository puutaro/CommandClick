package com.puutaro.commandclick.activity_lib.event.lib.cmdIndex

import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.activity_lib.manager.WrapFragmentManager
import com.puutaro.commandclick.util.state.EditFragmentArgs


object ExecCommandEdit {
    fun execCommandEdit(
        activity: MainActivity,
        editFragmentTag: String,
        editFragmentArgs: EditFragmentArgs,
        terminalFragmentTag: String,
        disableAddToBackStack: Boolean = false
    ){
        WrapFragmentManager.changeFragmentEdit(
            activity.supportFragmentManager,
            editFragmentTag,
            terminalFragmentTag,
            editFragmentArgs,
            disableAddToBackStack
        )
    }
}

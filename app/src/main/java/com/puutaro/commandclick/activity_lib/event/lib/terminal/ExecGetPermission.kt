package com.puutaro.commandclick.activity_lib.event.lib.terminal

import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.activity_lib.permission.CameraSetter
import com.puutaro.commandclick.common.variable.variant.PermissionType

object ExecGetPermission {
    fun get(
        activity: MainActivity,
        permissionStr: String,
    ){
        when(permissionStr){
            PermissionType.CAMERA.str -> {
                CameraSetter.getPermissionAndSet(activity)
            }
            else -> {}
        }
    }
}
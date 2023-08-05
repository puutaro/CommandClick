package com.puutaro.commandclick.fragment_lib.command_index_fragment.setting_button

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.ToolbarMenuCategoriesVariantForCmdIndex

object InstallFannelHandler {
    fun handle(
        cmdIndexFragment: CommandIndexFragment,
        installFromFannelRepo: InstallFromFannelRepo
    ){
        val activity = cmdIndexFragment.activity ?: return
        getNotificationPermissionLauncher(
            activity,
            cmdIndexFragment
        )
        installFromFannelRepo.install()
    }

    private fun getNotificationPermissionLauncher(
        activity: Activity,
        cmdIndexIndexFragment: CommandIndexFragment
    ){
        if(Build.VERSION.SDK_INT < 33) return
        val firstPermissionCheck =
            ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.POST_NOTIFICATIONS
            )
        if(
            firstPermissionCheck == PackageManager.PERMISSION_GRANTED
        ) return
        val listener =
            cmdIndexIndexFragment.context as? CommandIndexFragment.OnToolbarMenuCategoriesListener
        listener?.onToolbarMenuCategories(
            ToolbarMenuCategoriesVariantForCmdIndex.INSTALL_FANNEL
        )
    }
}
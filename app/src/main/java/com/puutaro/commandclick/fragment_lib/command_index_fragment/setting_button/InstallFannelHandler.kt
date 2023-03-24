package com.puutaro.commandclick.fragment_lib.command_index_fragment.setting_button

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.ToolbarMenuCategoriesVariantForCmdIndex
import kotlinx.coroutines.*

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
        cmdIndexFragment.fannelInstallJob?.cancel()
        cmdIndexFragment.fannelInstallJob = CoroutineScope(Dispatchers.Main).launch {
            if (Build.VERSION.SDK_INT >= 33) {
                var checkNotificationPermission = PackageManager.PERMISSION_DENIED
                withContext(Dispatchers.IO) {
                    for (i in 0..600) {
                        checkNotificationPermission =
                            ContextCompat.checkSelfPermission(
                                activity,
                                Manifest.permission.POST_NOTIFICATIONS
                            )
                        if (checkNotificationPermission ==
                            PackageManager.PERMISSION_GRANTED
                        ) break
                        delay(100)
                    }
                }
                withContext(Dispatchers.Main) {
                    if (
                        checkNotificationPermission ==
                        PackageManager.PERMISSION_GRANTED
                        && cmdIndexFragment.isVisible
                    ) installFromFannelRepo.install()
                }
            } else {
                installFromFannelRepo.install()
            }
        }
    }

    private fun getNotificationPermissionLauncher(
        activity: Activity,
        cmdIndexFragment: CommandIndexFragment
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
            cmdIndexFragment.context as? CommandIndexFragment.OnToolbarMenuCategoriesListener
        listener?.onToolbarMenuCategories(
            ToolbarMenuCategoriesVariantForCmdIndex.INSTALL_FANNEL
        )
    }
}
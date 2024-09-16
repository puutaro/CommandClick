package com.puutaro.commandclick.activity_lib.event

import android.content.*
import android.os.Build
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.activity_lib.permission.NotifierSetter
import com.puutaro.commandclick.activity_lib.event.lib.cmdIndex.ExecCmdListAjustForKeyboard
import com.puutaro.commandclick.activity_lib.event.lib.common.ExecTerminalLongOrShort
import com.puutaro.commandclick.activity_lib.event.lib.terminal.ExecGoBack
import com.puutaro.commandclick.activity_lib.event.lib.terminal.ExecGoForward
import com.puutaro.commandclick.activity_lib.event.lib.terminal.ExecReload
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.ToolbarMenuCategoriesVariantForCmdIndex
import com.puutaro.commandclick.util.ShortCutManager
import com.puutaro.commandclick.util.state.EditFragmentArgs
import java.lang.ref.WeakReference

object ExecToolbarMenuCategoriesForCmdIndex {
    fun <T: Fragment> execToolbarMenuCategories(
        activity: MainActivity,
        fragmentTag: String,
        editFragmentArgs: EditFragmentArgs,
        toolbarMenuCategoriesVariantForCmdIndex: ToolbarMenuCategoriesVariantForCmdIndex
    ) {
        when(toolbarMenuCategoriesVariantForCmdIndex){
            ToolbarMenuCategoriesVariantForCmdIndex.TERMMAX -> {
                ExecTerminalLongOrShort.open<T>(
                    fragmentTag,
                    activity.supportFragmentManager,
                )
            }
            ToolbarMenuCategoriesVariantForCmdIndex.TERMMAX_KEYBOARD_OPEN -> {
                ExecCmdListAjustForKeyboard.ajust(
                    fragmentTag,
                    activity.supportFragmentManager,
                    indexListSize.OPEN.size
                )
            }
            ToolbarMenuCategoriesVariantForCmdIndex.TERMMAX_KEYBOARD_CLOSE -> {
                ExecCmdListAjustForKeyboard.ajust(
                    fragmentTag,
                    activity.supportFragmentManager,
                    indexListSize.SHRINK.size
                )
            }
            ToolbarMenuCategoriesVariantForCmdIndex.SHORTCUT -> {
                val shortCutManager = ShortCutManager(WeakReference(activity))
                shortCutManager.createShortCut()
            }
//            ToolbarMenuCategoriesVariantForCmdIndex.CONFIG -> {
//                SystemFannelLauncher.launchFromActivity(
//                    activity,
//                    editFragmentArgs,
////                    UsePath.cmdclickDefaultAppDirPath,
//                    UsePath.cmdclickPreferenceJsName,
//                )
//            }
//            ToolbarMenuCategoriesVariantForCmdIndex.TERMUX_SETUP -> {
//                    RunCommandSetter.getPermissionAndSet(
//                    activity
//                )
//            }
            ToolbarMenuCategoriesVariantForCmdIndex.INSTALL_FANNEL -> {
                if(Build.VERSION.SDK_INT < 33) return
                NotifierSetter.getPermissionAndSet(
                    activity
                )
            }
            ToolbarMenuCategoriesVariantForCmdIndex.HISTORY -> {
                val execIntent = Intent(activity, activity::class.java)
                execIntent.setAction(Intent.ACTION_MAIN)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                activity.finish()
                activity.startActivity(execIntent)
            }
            ToolbarMenuCategoriesVariantForCmdIndex.BACK -> {
                ExecGoBack.execGoBack(
                    activity
                )
            }
            ToolbarMenuCategoriesVariantForCmdIndex.RELOAD -> {
                ExecReload.execReload(
                    activity
                )
            }
            ToolbarMenuCategoriesVariantForCmdIndex.FORWARD -> {
                ExecGoForward.execGoForword(
                    activity
                )
            }
            else -> {
                println("pass")
            }
        }
    }
}


private enum class indexListSize(
    val size: Float
){
    OPEN(0.07F),
    SHRINK(0.04F)
}

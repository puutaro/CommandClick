package com.puutaro.commandclick.activity_lib.event

import android.content.*
import android.os.Build
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.activity_lib.permission.NotifierSetter
import com.puutaro.commandclick.activity_lib.permission.RunCommandSetter
import com.puutaro.commandclick.activity_lib.event.lib.app_some_admin.ExecSomeAdmin
import com.puutaro.commandclick.activity_lib.event.lib.cmdIndex.ExecCmdListAjustForKeyboard
import com.puutaro.commandclick.activity_lib.event.lib.common.ExecTerminalLongOrShort
import com.puutaro.commandclick.activity_lib.event.lib.terminal.ExecGoForword
import com.puutaro.commandclick.activity_lib.manager.WrapFragmentManager
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.ToolbarMenuCategoriesVariantForCmdIndex
import com.puutaro.commandclick.util.ShortCutManager
import com.puutaro.commandclick.view_model.activity.TerminalViewModel

object ExecToolbarMenuCategoriesForCmdIndex {
    fun <T: Fragment> execToolbarMenuCategories(
        activity: MainActivity,
        fragmentTag: String,
        toolbarMenuCategoriesVariantForCmdIndex: ToolbarMenuCategoriesVariantForCmdIndex
    ) {
        when(toolbarMenuCategoriesVariantForCmdIndex){
            ToolbarMenuCategoriesVariantForCmdIndex.TERMMAX -> {
                val terminalViewModel: TerminalViewModel =
                    ViewModelProvider(activity).get(TerminalViewModel::class.java)
                ExecTerminalLongOrShort.open<T>(
                    fragmentTag,
                    activity.supportFragmentManager,
                    terminalViewModel
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
                val shortCutManager = ShortCutManager(activity)
                shortCutManager.createShortCut()
            }
            ToolbarMenuCategoriesVariantForCmdIndex.CONFIG -> {
                WrapFragmentManager.changeFragmentEdit(
                    activity.supportFragmentManager,
                    activity.getString(R.string.cmd_config_variable_edit_fragment),
                    String()
                )
            }
            ToolbarMenuCategoriesVariantForCmdIndex.TERMUX_SETUP -> {
                RunCommandSetter.getPermissionAndSet(
                    activity
                )
            }
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
            ToolbarMenuCategoriesVariantForCmdIndex.CHDIR -> {
                ExecSomeAdmin.execSomeAdmin(
                    activity,
                    activity.getString(R.string.app_dir_admin)
                )
            }
            ToolbarMenuCategoriesVariantForCmdIndex.FORWARD -> {
                ExecGoForword.execGoForword(
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

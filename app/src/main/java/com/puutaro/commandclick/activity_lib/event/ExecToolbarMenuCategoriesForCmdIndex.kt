package com.puutaro.commandclick.activity_lib.event

import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.activity_lib.RunCommandSetter
import com.puutaro.commandclick.activity_lib.event.lib.app_some_admin.ExecSomeAdmin
import com.puutaro.commandclick.activity_lib.event.lib.cmdIndex.ExecCmdListAjustForKeyboard
import com.puutaro.commandclick.activity_lib.event.lib.common.ExecRestartIntent
import com.puutaro.commandclick.activity_lib.event.lib.common.ExecTerminalLongOrShort
import com.puutaro.commandclick.activity_lib.event.lib.terminal.ExecGoForword
import com.puutaro.commandclick.activity_lib.manager.InitFragmentManager
import com.puutaro.commandclick.activity_lib.manager.WrapFragmentManager
import com.puutaro.commandclick.common.variable.WebUrlVariables
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.ToolbarMenuCategoriesVariantForCmdIndex
import com.puutaro.commandclick.util.LinearLayoutAdderForDialog
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
                    0.07F
                )
            }
            ToolbarMenuCategoriesVariantForCmdIndex.TERMMAX_KEYBOARD_CLOSE -> {
                ExecCmdListAjustForKeyboard.ajust(
                    fragmentTag,
                    activity.supportFragmentManager,
                    0.04F
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

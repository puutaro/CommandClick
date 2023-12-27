package com.puutaro.commandclick.activity_lib.event

import android.content.*
import android.os.Build
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.activity_lib.permission.NotifierSetter
import com.puutaro.commandclick.activity_lib.permission.RunCommandSetter
import com.puutaro.commandclick.activity_lib.event.lib.cmdIndex.ExecCmdListAjustForKeyboard
import com.puutaro.commandclick.activity_lib.event.lib.common.ExecTerminalLongOrShort
import com.puutaro.commandclick.activity_lib.event.lib.terminal.ExecGoBack
import com.puutaro.commandclick.activity_lib.event.lib.terminal.ExecGoForword
import com.puutaro.commandclick.activity_lib.event.lib.terminal.ExecReload
import com.puutaro.commandclick.activity_lib.manager.WrapFragmentManager
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.ToolbarMenuCategoriesVariantForCmdIndex
import com.puutaro.commandclick.util.FragmentTagManager
import com.puutaro.commandclick.util.SharePreffrenceMethod
import com.puutaro.commandclick.util.ShortCutManager

object ExecToolbarMenuCategoriesForCmdIndex {
    fun <T: Fragment> execToolbarMenuCategories(
        activity: MainActivity,
        fragmentTag: String,
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
                val shortCutManager = ShortCutManager(activity)
                shortCutManager.createShortCut()
            }
            ToolbarMenuCategoriesVariantForCmdIndex.CONFIG -> {
                val cmdclickConfigFileName = UsePath.cmdclickConfigFileName
                val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
                SharePreffrenceMethod.putSharePreffrence(
                    sharedPref,
                    mapOf(
                        SharePrefferenceSetting.current_app_dir.name
                                to UsePath.cmdclickSystemAppDirPath,
                        SharePrefferenceSetting.current_script_file_name.name
                                to cmdclickConfigFileName,
                        SharePrefferenceSetting.on_shortcut.name
                                to FragmentTagManager.Suffix.ON.str
                    )
                )
                val cmdEditFragmentTag = FragmentTagManager.makeTag(
                    FragmentTagManager.Prefix.cmdEditPrefix.str,
                    UsePath.cmdclickSystemAppDirPath,
                    cmdclickConfigFileName,
                    FragmentTagManager.Suffix.ON.str
                )
                WrapFragmentManager.changeFragmentEdit(
                    activity.supportFragmentManager,
                    cmdEditFragmentTag,
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

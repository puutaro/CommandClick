package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.click.lib

import android.content.SharedPreferences
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.LongClickMenuItemsforCmdIndex
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.util.state.FragmentTagManager
import com.puutaro.commandclick.util.state.SharePreferenceMethod


object OnEditExecuteEvent {
    fun invoke(
        fragment: Fragment,
        editFragmentTag: String,
        sharedPref: SharedPreferences?,
        selectedShellFileName: String,
    ) {
        val shortcutOnMark = FragmentTagManager.Suffix.ON.name
        SharePreferenceMethod.putSharePreference(
            sharedPref,
            mapOf(
                SharePrefferenceSetting.current_fannel_name.name
                        to selectedShellFileName,
                SharePrefferenceSetting.on_shortcut.name
                        to shortcutOnMark
            )
        )
        val currentAppDirPath = SharePreferenceMethod.getStringFromSharePreference(
            sharedPref,
            SharePrefferenceSetting.current_app_dir
        )
        val readSharePreferenceMap = EditFragmentArgs.createReadSharePreferenceMap(
            currentAppDirPath,
            selectedShellFileName,
            shortcutOnMark
        )
        when(fragment){
            is CommandIndexFragment -> {
                val listener = fragment.context
                        as? CommandIndexFragment.OnLongClickMenuItemsForCmdIndexListener
                listener?.onLongClickMenuItemsforCmdIndex(
                    LongClickMenuItemsforCmdIndex.EDIT,
                    EditFragmentArgs(readSharePreferenceMap),
                    editFragmentTag,
                    true,
                    fragment.context?.getString(R.string.edit_execute_terminal_fragment)
                )
            }
            is EditFragment -> {
                val listener = fragment.context
                        as? CommandIndexFragment.OnLongClickMenuItemsForCmdIndexListener
                    listener?.onLongClickMenuItemsforCmdIndex(
                        LongClickMenuItemsforCmdIndex.EDIT,
                        EditFragmentArgs(readSharePreferenceMap),
                        editFragmentTag,
                        true,
                        fragment.context?.getString(R.string.edit_execute_terminal_fragment)
                )
            }
        }
    }
}
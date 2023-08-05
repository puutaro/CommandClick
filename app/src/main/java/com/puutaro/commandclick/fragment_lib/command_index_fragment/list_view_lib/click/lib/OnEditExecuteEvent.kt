package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.click.lib

import android.content.SharedPreferences
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.LongClickMenuItemsforCmdIndex
import com.puutaro.commandclick.util.FragmentTagManager
import com.puutaro.commandclick.util.SharePreffrenceMethod


object OnEditExecuteEvent {
    fun invoke(
        cmdIndexCommandIndexFragment: CommandIndexFragment,
        editFragmentTag: String,
        sharedPref: SharedPreferences?,
        selectedShellFileName: String,
    ) {
        SharePreffrenceMethod.putSharePreffrence(
            sharedPref,
            mapOf(
                SharePrefferenceSetting.current_script_file_name.name
                        to selectedShellFileName,
                SharePrefferenceSetting.on_shortcut.name
                        to FragmentTagManager.Suffix.ON.name
            )
        )
        val listener = cmdIndexCommandIndexFragment.context
                as? CommandIndexFragment.OnLongClickMenuItemsForCmdIndexListener
        listener?.onLongClickMenuItemsforCmdIndex(
            LongClickMenuItemsforCmdIndex.EDIT,
            editFragmentTag,
            true,
            cmdIndexCommandIndexFragment.context?.getString(R.string.edit_execute_terminal_fragment)
        )
    }
}
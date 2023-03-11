package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.click.lib

import android.content.SharedPreferences
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.ShortcutOnValueStr
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.LongClickMenuItemsforCmdIndex
import com.puutaro.commandclick.util.SharePreffrenceMethod


class OnEditExecuteEvent {
    companion object {
        fun invoke(
            cmdIndexFragment: CommandIndexFragment,
            editFragmentTag: String,
            editExecuteValue: String,
            sharedPref: SharedPreferences?,
            selectedShellFileName: String,
            settingSectionVariableList: List<String>?
        ) {
            SharePreffrenceMethod.putSharePreffrence(
                sharedPref,
                mapOf(
                    SharePrefferenceSetting.current_script_file_name.name
                            to selectedShellFileName,
                    SharePrefferenceSetting.on_shortcut.name
                            to ShortcutOnValueStr.ON.name
                )
            )
            val listener = cmdIndexFragment.context
                    as? CommandIndexFragment.OnLongClickMenuItemsForCmdIndexListener
            listener?.onLongClickMenuItemsforCmdIndex(
                LongClickMenuItemsforCmdIndex.EDIT,
                editFragmentTag,
                true,
                cmdIndexFragment.context?.getString(R.string.edit_execute_terminal_fragment)
            )
        }
    }
}
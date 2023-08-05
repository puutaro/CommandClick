package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.click.lib

import android.content.SharedPreferences
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.LongClickMenuItemsforCmdIndex
import com.puutaro.commandclick.util.SharePreffrenceMethod

object OnOnceEditExecuteEvent {
    fun invoke(
        cmdIndexFragment: CommandIndexFragment,
        sharedPref: SharedPreferences?,
        selectedShellFileName: String,
        editFragmentTag: String,
    ) {
        SharePreffrenceMethod.putSharePreffrence(
            sharedPref,
            mapOf(
                SharePrefferenceSetting.current_script_file_name.name
                        to selectedShellFileName,
            )
        )
        val listener = cmdIndexFragment.context
                as? CommandIndexFragment.OnLongClickMenuItemsForCmdIndexListener
        listener?.onLongClickMenuItemsforCmdIndex(
            LongClickMenuItemsforCmdIndex.EDIT,
            editFragmentTag
        )
    }
}
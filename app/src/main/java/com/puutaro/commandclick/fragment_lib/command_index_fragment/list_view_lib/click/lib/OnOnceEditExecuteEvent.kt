package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.click.lib

import android.content.SharedPreferences
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.LongClickMenuItemsforCmdIndex
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.util.state.SharePreferenceMethod

object OnOnceEditExecuteEvent {
    fun invoke(
        cmdIndexFragment: CommandIndexFragment,
        sharedPref: SharedPreferences?,
        selectedShellFileName: String,
        editFragmentTag: String,
    ) {
        SharePreferenceMethod.putSharePreference(
            sharedPref,
            mapOf(
                SharePrefferenceSetting.current_fannel_name.name
                        to selectedShellFileName,
            )
        )
        val currentAppDirPath = SharePreferenceMethod.getStringFromSharePreference(
            sharedPref,
            SharePrefferenceSetting.current_app_dir
        )
        val readSharePreferenceMap = EditFragmentArgs.createReadSharePreferenceMap(
            currentAppDirPath,
            selectedShellFileName,
            String()
        )
        val listener = cmdIndexFragment.context
                as? CommandIndexFragment.OnLongClickMenuItemsForCmdIndexListener
        listener?.onLongClickMenuItemsforCmdIndex(
            LongClickMenuItemsforCmdIndex.EDIT,
            EditFragmentArgs(readSharePreferenceMap),
            editFragmentTag,
        )
    }
}
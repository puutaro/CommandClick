package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.click.lib

import android.content.SharedPreferences
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.LongClickMenuItemsforCmdIndex
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.util.state.SharePrefTool

object OnOnceEditExecuteEvent {
    fun invoke(
        cmdIndexFragment: CommandIndexFragment,
        sharedPref: SharedPreferences?,
        selectedShellFileName: String,
        editFragmentTag: String,
    ) {
        val context = cmdIndexFragment.context ?: return
        SharePrefTool.putSharePref(
            sharedPref,
            mapOf(
                SharePrefferenceSetting.current_fannel_name.name
                        to selectedShellFileName,
            )
        )
        val currentAppDirPath = SharePrefTool.getStringFromSharePref(
            sharedPref,
            SharePrefferenceSetting.current_app_dir
        )
        val readSharePreferenceMap = EditFragmentArgs.createReadSharePreferenceMap(
            currentAppDirPath,
            selectedShellFileName,
            String(),
            SharePrefferenceSetting.current_fannel_state.defalutStr,
        )
        val listener = context
                as CommandIndexFragment.OnLongClickMenuItemsForCmdIndexListener
        listener.onLongClickMenuItemsforCmdIndex(
            LongClickMenuItemsforCmdIndex.EDIT,
            EditFragmentArgs(
                readSharePreferenceMap,
                EditFragmentArgs.Companion.EditTypeSettingsKey.CMD_VAL_EDIT,
            ),
            editFragmentTag,
            context.getString(R.string.edit_terminal_fragment)
        )
    }
}
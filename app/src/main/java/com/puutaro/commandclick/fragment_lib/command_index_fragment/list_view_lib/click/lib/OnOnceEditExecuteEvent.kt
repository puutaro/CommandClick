package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.click.lib

import android.content.SharedPreferences
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.settings.FannelInfoSetting
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.LongClickMenuItemsforCmdIndex
import com.puutaro.commandclick.util.SharePrefTool
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.util.state.FannelInfoTool

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
                FannelInfoSetting.current_fannel_name.name
                        to selectedShellFileName,
            )
        )
        val currentAppDirPath = FannelInfoTool.getStringFromFannelInfo(
            sharedPref,
            FannelInfoSetting.current_app_dir
        )
        val fannelInfoMap = EditFragmentArgs.createFannelInfoMap(
            currentAppDirPath,
            selectedShellFileName,
            String(),
            FannelInfoSetting.current_fannel_state.defalutStr,
        )
        val listener = context
                as CommandIndexFragment.OnLongClickMenuItemsForCmdIndexListener
        listener.onLongClickMenuItemsforCmdIndex(
            LongClickMenuItemsforCmdIndex.EDIT,
            EditFragmentArgs(
                fannelInfoMap,
                EditFragmentArgs.Companion.EditTypeSettingsKey.CMD_VAL_EDIT,
            ),
            editFragmentTag,
            context.getString(R.string.edit_terminal_fragment)
        )
    }
}
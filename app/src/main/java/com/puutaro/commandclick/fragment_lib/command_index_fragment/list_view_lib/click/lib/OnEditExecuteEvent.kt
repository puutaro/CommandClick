package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.click.lib

import android.content.Context
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.R
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.LongClickMenuItemsforCmdIndex
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.util.state.FragmentTagPrefix
import com.puutaro.commandclick.util.state.SharePrefTool

object OnEditExecuteEvent {
    fun invoke(
        fragment: Fragment,
        editFragmentTag: String,
        currentAppDirPath: String,
        selectedShellFileName: String,
        fannelState: String,
    ) {
        val context = fragment.context
            ?: return
        val sharedPref =  fragment.activity?.getPreferences(Context.MODE_PRIVATE)
        val shortcutOnMark = EditFragmentArgs.Companion.OnShortcutSettingKey.ON.key
        SharePrefTool.putAllSharePref(
            sharedPref,
            currentAppDirPath,
            selectedShellFileName,
            shortcutOnMark,
            fannelState
        )
        val readSharePreferenceMap = EditFragmentArgs.createReadSharePreferenceMap(
            currentAppDirPath,
            selectedShellFileName,
            shortcutOnMark,
            fannelState,
        )
        val cmdValEditPrefix = FragmentTagPrefix.Prefix.CMD_VAL_EDIT_PREFIX.str
        val cmdValEdit =
            when(
                editFragmentTag.startsWith(cmdValEditPrefix)
            ) {
                false -> EditFragmentArgs.Companion.EditTypeSettingsKey.SETTING_VAL_EDIT
                else -> EditFragmentArgs.Companion.EditTypeSettingsKey.CMD_VAL_EDIT
            }
        when(fragment){
            is CommandIndexFragment -> {
                val listener = fragment.context
                        as? CommandIndexFragment.OnLongClickMenuItemsForCmdIndexListener
                listener?.onLongClickMenuItemsforCmdIndex(
                    LongClickMenuItemsforCmdIndex.EDIT,
                    EditFragmentArgs(
                        readSharePreferenceMap,
                        cmdValEdit,
                    ),
                    editFragmentTag,
                    context.getString(R.string.edit_terminal_fragment)
                )
            }
            is EditFragment -> {
                val listener = fragment.context
                        as? CommandIndexFragment.OnLongClickMenuItemsForCmdIndexListener
                    listener?.onLongClickMenuItemsforCmdIndex(
                        LongClickMenuItemsforCmdIndex.EDIT,
                        EditFragmentArgs(
                            readSharePreferenceMap,
                            cmdValEdit,
                        ),
                        editFragmentTag,
                        context.getString(R.string.edit_terminal_fragment)
                )
            }
        }
    }
}
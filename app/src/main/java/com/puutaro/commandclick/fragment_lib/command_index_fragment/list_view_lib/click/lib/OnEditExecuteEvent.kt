package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.click.lib

import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.R
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.LongClickMenuItemsforCmdIndex
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.util.state.FragmentTagPrefix
import com.puutaro.commandclick.util.state.FannelInfoTool

object OnEditExecuteEvent {
    fun invoke(
        fragment: Fragment,
        editFragmentTag: String,
//        currentAppDirPath: String,
        selectedShellFileName: String,
        fannelState: String,
    ) {
        val context = fragment.context
            ?: return
        val sharedPref = FannelInfoTool.getSharePref(context)
        val shortcutOnMark = EditFragmentArgs.Companion.OnShortcutSettingKey.ON.key
        FannelInfoTool.putAllFannelInfo(sharedPref,
//            currentAppDirPath,
            selectedShellFileName,
            shortcutOnMark,
            fannelState
        )
        val fannelInfoMap = EditFragmentArgs.createFannelInfoMap(
//            currentAppDirPath,
            selectedShellFileName,
            shortcutOnMark,
            fannelState,
        )
//        val cmdValEditPrefix = FragmentTagPrefix.Prefix.CMD_VAL_EDIT_PREFIX.str
        val cmdValEdit = EditFragmentArgs.Companion.EditTypeSettingsKey.CMD_VAL_EDIT
//            when(
//                editFragmentTag.startsWith(cmdValEditPrefix)
//            ) {
//                false -> EditFragmentArgs.Companion.EditTypeSettingsKey.SETTING_VAL_EDIT
//                else -> EditFragmentArgs.Companion.EditTypeSettingsKey.CMD_VAL_EDIT
//            }
        when(fragment){
            is CommandIndexFragment -> {
                val listener = context
                        as? CommandIndexFragment.OnLongClickMenuItemsForCmdIndexListener
                listener?.onLongClickMenuItemsforCmdIndex(
                    LongClickMenuItemsforCmdIndex.EDIT,
                    EditFragmentArgs(
                        fannelInfoMap,
                        cmdValEdit,
                    ),
                    editFragmentTag,
                    context.getString(R.string.edit_terminal_fragment)
                )
            }
            is TerminalFragment -> {
                val listener = context
                        as? TerminalFragment.OnPinClickForTermListener
                listener?.onPinClickForTerm(
                    LongClickMenuItemsforCmdIndex.EDIT,
                    EditFragmentArgs(
                        fannelInfoMap,
                        cmdValEdit,
                    ),
                    editFragmentTag,
                    context.getString(R.string.edit_terminal_fragment)
                )
            }
            is EditFragment -> {
                val listener = context
                        as? CommandIndexFragment.OnLongClickMenuItemsForCmdIndexListener
                    listener?.onLongClickMenuItemsforCmdIndex(
                        LongClickMenuItemsforCmdIndex.EDIT,
                        EditFragmentArgs(
                            fannelInfoMap,
                            cmdValEdit,
                        ),
                        editFragmentTag,
                        context.getString(R.string.edit_terminal_fragment)
                )
            }
        }
    }
}
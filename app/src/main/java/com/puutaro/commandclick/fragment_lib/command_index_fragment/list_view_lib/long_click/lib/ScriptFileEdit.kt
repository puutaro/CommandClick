package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.long_click.lib

import android.content.Context
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.common.DecideEditTag
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.LongClickMenuItemsforCmdIndex
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.ValidateShell
import com.puutaro.commandclick.proccess.lib.VariationErrDialog
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.util.state.SharePreferenceMethod

object ScriptFileEdit {
    fun edit(
        cmdIndexFragment: CommandIndexFragment,
        currentAppDirPath: String,
        shellScriptName: String,
    ){
        val sharedPref = cmdIndexFragment.activity?.getPreferences(Context.MODE_PRIVATE)
        SharePreferenceMethod.putSharePreference(
            sharedPref,
            mapOf(
                SharePrefferenceSetting.current_fannel_name.name
                        to shellScriptName,
                SharePrefferenceSetting.on_shortcut.name
                        to SharePrefferenceSetting.on_shortcut.defalutStr,
            )
        )
        val shellContentsList = ReadText(
            currentAppDirPath,
            shellScriptName
        ).textToList()
        val validateErrMessage = ValidateShell.correct(
            cmdIndexFragment,
            shellContentsList,
            shellScriptName
        )
        if(validateErrMessage.isNotEmpty()){
            val shellScriptPath = "${currentAppDirPath}/${shellScriptName}"
            VariationErrDialog.show(
                cmdIndexFragment,
                shellScriptPath,
                validateErrMessage
            )
            return
        }
        val editFragmentTag = DecideEditTag(
            shellContentsList,
            currentAppDirPath,
            shellScriptName
        ).decideForEdit()
            ?: return
        val readSharePreferenceMap = EditFragmentArgs.createReadSharePreferenceMap(
            currentAppDirPath,
            shellScriptName,
            SharePrefferenceSetting.on_shortcut.defalutStr,
        )
        val context = cmdIndexFragment.context
            ?: return
        val listener = context
                as? CommandIndexFragment.OnLongClickMenuItemsForCmdIndexListener
        listener?.onLongClickMenuItemsforCmdIndex(
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
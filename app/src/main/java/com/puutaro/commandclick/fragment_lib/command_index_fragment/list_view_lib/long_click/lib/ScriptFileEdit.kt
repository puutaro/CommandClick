package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.long_click.lib

import android.content.Context
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.common.DecideEditTag
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.LongClickMenuItemsforCmdIndex
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.ValidateShell
import com.puutaro.commandclick.proccess.lib.VariationErrDialog
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.SharePreffrenceMethod

object ScriptFileEdit {
    fun edit(
        cmdIndexFragment: CommandIndexFragment,
        currentAppDirPath: String,
        shellScriptName: String,
    ){
        val sharedPref = cmdIndexFragment.activity?.getPreferences(Context.MODE_PRIVATE)
        SharePreffrenceMethod.putSharePreffrence(
            sharedPref,
            mapOf(
                SharePrefferenceSetting.current_script_file_name.name
                        to shellScriptName,
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
        val listener = cmdIndexFragment.context
                as? CommandIndexFragment.OnLongClickMenuItemsForCmdIndexListener
        listener?.onLongClickMenuItemsforCmdIndex(
            LongClickMenuItemsforCmdIndex.EDIT,
            editFragmentTag
        )
    }
}
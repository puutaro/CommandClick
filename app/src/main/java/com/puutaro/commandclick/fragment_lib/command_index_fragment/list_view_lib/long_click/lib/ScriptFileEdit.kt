package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.long_click.lib

import android.content.Context
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.settings.FannelInfoSetting
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.common.DecideEditTag
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.LongClickMenuItemsforCmdIndex
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.ValidateShell
import com.puutaro.commandclick.proccess.lib.VariationErrDialog
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.util.state.FannelInfoTool
import java.io.File

object ScriptFileEdit {
    fun edit(
        cmdIndexFragment: CommandIndexFragment,
//        currentAppDirPath: String,
        fannelName: String,
    ){
        val sharedPref =
            FannelInfoTool.getSharePref(cmdIndexFragment.context)
        FannelInfoTool.putAllFannelInfo(
            sharedPref,
//            currentAppDirPath,
            fannelName,
            FannelInfoSetting.on_shortcut.defalutStr,
            FannelInfoSetting.current_fannel_state.defalutStr
        )
        val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath
        val shellContentsList = ReadText(
            File(
                cmdclickDefaultAppDirPath,
                fannelName
            ).absolutePath,
        ).textToList()
        val validateErrMessage = ValidateShell.correct(
            cmdIndexFragment,
            shellContentsList,
            fannelName
        )
        if(validateErrMessage.isNotEmpty()){
            val shellScriptPath = "${cmdclickDefaultAppDirPath}/${fannelName}"
            VariationErrDialog.show(
                cmdIndexFragment,
                shellScriptPath,
                validateErrMessage
            )
            return
        }
        val editFragmentTag = DecideEditTag(
            shellContentsList,
//            currentAppDirPath,
            fannelName,
            FannelInfoSetting.current_fannel_state.defalutStr,
        ).decideForEdit()
            ?: return
        val fannelInfoMap = EditFragmentArgs.createFannelInfoMap(
//            currentAppDirPath,
            fannelName,
            FannelInfoSetting.on_shortcut.defalutStr,
            FannelInfoSetting.current_fannel_state.defalutStr,
        )
        val context = cmdIndexFragment.context
            ?: return
        val listener = context
                as? CommandIndexFragment.OnLongClickMenuItemsForCmdIndexListener
        listener?.onLongClickMenuItemsforCmdIndex(
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
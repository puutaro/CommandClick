package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.long_click

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.common.DecideEditTag
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.long_click.lib.*
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.LongClickMenuItemsforCmdIndex
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.ValidateShell
import com.puutaro.commandclick.proccess.ShellFileDescription
import com.puutaro.commandclick.proccess.lib.VaridateionErrDialog
import com.puutaro.commandclick.util.Editor
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.SharePreffrenceMethod
import com.puutaro.commandclick.view_model.activity.TerminalViewModel


class ExecOnLongClickDo {
    companion object {
        fun invoke(
            cmdIndexFragment: CommandIndexFragment,
            currentAppDirPath: String,
            item: MenuItem,
            contextItemSelected: Boolean,
            cmdListAdapter: ArrayAdapter<String>,
        ): Boolean {
            val terminalViewModel: TerminalViewModel by cmdIndexFragment.activityViewModels()
            val activity = cmdIndexFragment.activity
            val sharedPref =  activity?.getPreferences(Context.MODE_PRIVATE)
            val context = cmdIndexFragment.context
            val binding = cmdIndexFragment.binding
            val cmdListView = binding.cmdList

            val info: AdapterView.AdapterContextMenuInfo? = try {
                item.menuInfo as AdapterView.AdapterContextMenuInfo?
            } catch (e: ClassCastException) {
                return false
            }
            val listPosition = info?.position ?: cmdIndexFragment.mParentContextMenuListIndex
            val shellScriptName = cmdListView.adapter.getItem(listPosition).toString()
            if(shellScriptName == CommandClickShellScript.EMPTY_STRING) return true
            when (item.itemId) {
                R.id.shell_script_menu_delete -> {
                    ConfirmDialogForDelete.show(
                        cmdIndexFragment,
                        currentAppDirPath,
                        shellScriptName,
                        cmdListAdapter,
                        cmdListView
                    )
                    return contextItemSelected
                }
                R.id.shell_script_menu_edit -> {
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
                        VaridateionErrDialog.show(
                            cmdIndexFragment,
                            shellScriptPath,
                            validateErrMessage
                        )
                        return contextItemSelected
                    }
                    val editFragmentTag = DecideEditTag(
                        shellContentsList,
                        shellScriptName
                    ).decide(
                        context,
                        context?.getString(
                            com.puutaro.commandclick.R.string.setting_variable_edit_fragment
                        )
                    )
                    val listener = cmdIndexFragment.context
                            as? CommandIndexFragment.OnLongClickMenuItemsForCmdIndexListener
                    listener?.onLongClickMenuItemsforCmdIndex(
                        LongClickMenuItemsforCmdIndex.EDIT,
                        editFragmentTag
                    )
                    return contextItemSelected
                }
                R.id.shell_script_menu_write, -> {
                    val editor = Editor(
                        currentAppDirPath,
                        shellScriptName,
                        context
                    )
                    editor.open()
                    return contextItemSelected
                }
                R.id.shell_script_menu_kill  -> {
                    ConfirmDialogforKill.show(
                        cmdIndexFragment,
                        currentAppDirPath,
                        shellScriptName,
                        terminalViewModel.currentMonitorFileName,
                        cmdListAdapter,
                        cmdListView
                    )
                    return contextItemSelected
                }
                R.id.shell_script_menu_copy_path  -> {
                    val shellFilePathByTermux = "${currentAppDirPath}/${shellScriptName}"
                    val clipboard = context?.getSystemService(
                        Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip: ClipData = ClipData.newPlainText(
                        "cmdclick path",
                        shellFilePathByTermux
                    )
                    clipboard.setPrimaryClip(clip)
                }
                R.id.shell_script_menu_copy_file -> {
                    CopyFileEvent(
                        cmdIndexFragment,
                        currentAppDirPath,
                        shellScriptName,
                        cmdListAdapter,
                    ).invoke()
                }
                 R.id.shell_script_menu_copy_app_dir -> {
                    CopyAppDirEvent(
                        cmdIndexFragment,
                        currentAppDirPath,
                        shellScriptName,
                        cmdListAdapter,
                    ).invoke()
                }
                R.id.shell_script_menu_add -> {
                    AddConfirmDialog.show(
                        cmdIndexFragment,
                        cmdListAdapter,
                        currentAppDirPath,
                        cmdListView
                    )
                    return contextItemSelected
                }
                R.id.shell_script_menu_description -> {
                    ShellFileDescription.show(
                        cmdIndexFragment.context,
                        ReadText(
                            currentAppDirPath,
                            shellScriptName
                        ).textToList(),
                        shellScriptName
                    )
                }
                R.id.shell_script_menu_init -> {
                    ShellFileInitManager.initDialog(
                        cmdIndexFragment,
                        currentAppDirPath,
                        shellScriptName,
                        cmdListAdapter,
                        cmdListView
                    )
                }
                else -> {
                    cmdIndexFragment.mParentContextMenuListIndex = listPosition
                }
            }
            return contextItemSelected

        }
    }
}
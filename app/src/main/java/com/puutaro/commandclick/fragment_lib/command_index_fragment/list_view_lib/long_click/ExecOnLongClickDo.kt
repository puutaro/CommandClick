package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.long_click

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.component.adapter.FannelIndexListAdapter
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.long_click.lib.*
import com.puutaro.commandclick.proccess.ScriptFileDescription
import com.puutaro.commandclick.util.Editor
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.view_model.activity.TerminalViewModel


object ExecOnLongClickDo {

    fun invoke(
        cmdIndexFragment: CommandIndexFragment,
        currentAppDirPath: String,
        item: MenuItem,
        contextItemSelected: Boolean,
        fannelIndexListAdapter: FannelIndexListAdapter,
    ): Boolean {
        val terminalViewModel: TerminalViewModel by cmdIndexFragment.activityViewModels()
        val activity = cmdIndexFragment.activity
        val sharedPref =  activity?.getPreferences(Context.MODE_PRIVATE)
        val context = cmdIndexFragment.context
        val binding = cmdIndexFragment.binding
        val cmdListView = binding.cmdList

//        val info: AdapterView.AdapterContextMenuInfo? = try {
//            item.menuInfo as AdapterView.AdapterContextMenuInfo?
//        } catch (e: ClassCastException) {
//            return false
//        }
        val listPosition = cmdIndexFragment.recyclerViewIndex
//            info
//            ?.position
//            ?: cmdIndexFragment.mParentContextMenuListIndex
        val shellScriptName =
            fannelIndexListAdapter.fannelIndexList.get(listPosition)
        if(
            shellScriptName
            == CommandClickScriptVariable.EMPTY_STRING
        ) return true
        when (item.itemId) {
            R.id.shell_script_menu_delete -> {
                ConfirmDialogForDelete.show(
                    cmdIndexFragment,
                    currentAppDirPath,
                    shellScriptName,
                    cmdListView
                )
                return contextItemSelected
            }
            R.id.shell_script_menu_edit -> {
                ScriptFileEdit.edit(
                    cmdIndexFragment,
                    currentAppDirPath,
                    shellScriptName,
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
                ).invoke()
            }
            R.id.shell_script_menu_description -> {
                ScriptFileDescription.show(
                    cmdIndexFragment.context,
                    ReadText(
                        currentAppDirPath,
                        shellScriptName
                    ).textToList(),
                    shellScriptName
                )
            }
            else -> {
                cmdIndexFragment.mParentContextMenuListIndex = listPosition
            }
        }
        return contextItemSelected

    }
}
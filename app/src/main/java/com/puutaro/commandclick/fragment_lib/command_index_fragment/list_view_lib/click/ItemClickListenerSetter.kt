package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.click

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.common.CommandListManager
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.click.lib.OnEditExecuteEvent
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.click.lib.AppDirectoryAdminEvent
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.click.lib.OnOnceEditExecuteEvent
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.common.DecideEditTag
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.ValidateShell
import com.puutaro.commandclick.proccess.CmdIndexToolbarSwitcher
import com.puutaro.commandclick.proccess.ExecTerminalDo
import com.puutaro.commandclick.proccess.lib.VaridateionErrDialog
import com.puutaro.commandclick.util.*
import com.puutaro.commandclick.view_model.activity.TerminalViewModel


class ItemClickListenerSetter {
    companion object {
        fun set(
            cmdIndexFragment: CommandIndexFragment,
            currentAppDirPath: String,
            cmdListAdapter: ArrayAdapter<String>
        ){
            val activity = cmdIndexFragment.activity
            val sharedPref =  activity?.getPreferences(Context.MODE_PRIVATE)
            val context = cmdIndexFragment.context
            val cmdIndexFragmentTag = context?.getString(R.string.command_index_fragment)
            val binding = cmdIndexFragment.binding
            val cmdSearchEditText = binding.cmdSearchEditText
            val cmdListView = binding.cmdList

            cmdListView.setOnItemClickListener { parent,
                                                 View,
                                                 position,
                                                 id
                ->
                if (binding.cmdListSwipeToRefresh.isRefreshing()) return@setOnItemClickListener
                Keyboard.hiddenKeyboard(
                    activity,
                    View
                )
                cmdSearchEditText.setText(String())
                CmdIndexToolbarSwitcher.switch(
                    cmdIndexFragment,
                    false
                )
                val selectedShellFileName = cmdListView.getItemAtPosition(position).toString()
                if(
                    selectedShellFileName.endsWith(
                        CommandClickShellScript.JS_FILE_SUFFIX
                    )
                ) {
                    val selectecJsFileName = selectedShellFileName
                    BroadCastIntent.send(
                        cmdIndexFragment,
                        JavaScriptLoadUrl.make(
                        "${currentAppDirPath}/${selectecJsFileName}",
                        ),
                    )
                    FileSystems.updateLastModified(
                        currentAppDirPath,
                        selectecJsFileName
                    )
                    cmdListAdapter.clear()
                    CommandListManager.execListUpdate(
                        currentAppDirPath,
                        cmdListAdapter,
                        cmdListView,
                    )
                    return@setOnItemClickListener
                }
                if (
                    selectedShellFileName ==
                    CommandClickShellScript.EMPTY_STRING
                ) return@setOnItemClickListener
                val curentFragmentTag = cmdIndexFragment.tag ?: String()

                val shellContentsList = ReadText(
                    currentAppDirPath,
                    selectedShellFileName
                ).textToList()
                val validateErrMessage = ValidateShell.correct(
                    cmdIndexFragment,
                    shellContentsList
                )
                if (validateErrMessage.isNotEmpty()) {
                    val shellScriptPath =
                        "${currentAppDirPath}/${selectedShellFileName}"
                    VaridateionErrDialog.show(
                        cmdIndexFragment,
                        shellScriptPath,
                        validateErrMessage
                    )
                    return@setOnItemClickListener
                }

                val settingSectionVariableList =
                    CommandClickVariables.substituteVariableListFromHolder(
                        shellContentsList,
                        CommandClickShellScript.SETTING_SECTION_START,
                        CommandClickShellScript.SETTING_SECTION_END,
                    )

                val editExecuteValue =
                    CommandClickVariables.substituteCmdClickVariable(
                        settingSectionVariableList,
                        CommandClickShellScript.EDIT_EXECUTE,
                    ) ?: SettingVariableSelects.Companion.EditExecuteSelects.NO.name
                when (editExecuteValue) {
                    SettingVariableSelects.Companion.EditExecuteSelects.ONCE.name -> {
                        val editFragmentTag = DecideEditTag(
                            shellContentsList
                        ).decide(context) ?: return@setOnItemClickListener
                        OnOnceEditExecuteEvent.invoke(
                            cmdIndexFragment,
                            sharedPref,
                            selectedShellFileName,
                            editFragmentTag,
                        )
                        return@setOnItemClickListener
                    }
                    SettingVariableSelects.Companion.EditExecuteSelects.ALWAYS.name -> {
                        val editFragmentTag = DecideEditTag(
                            shellContentsList
                        ).decide(context) ?: return@setOnItemClickListener

                        OnEditExecuteEvent.invoke(
                            cmdIndexFragment,
                            editFragmentTag,
                            editExecuteValue,
                            sharedPref,
                            selectedShellFileName,
                            settingSectionVariableList
                        )
                        return@setOnItemClickListener
                    }
                }
                if (
                    curentFragmentTag == cmdIndexFragmentTag
                ) {
                    ExecTerminalDo.execTerminalDo(
                        cmdIndexFragment,
                        currentAppDirPath,
                        selectedShellFileName,
                    )
                } else if(
                    curentFragmentTag == context?.getString(
                        R.string.app_dir_admin
                    )
                ) {
                    AppDirectoryAdminEvent.invoke(
                        sharedPref,
                        currentAppDirPath,
                        selectedShellFileName
                    )
                }

                cmdListAdapter.clear()
                CommandListManager.execListUpdate(
                    currentAppDirPath,
                    cmdListAdapter,
                    cmdListView,
                )

                val listener = cmdIndexFragment.context as? CommandIndexFragment.OnListItemClickListener
                listener?.onListItemClicked(
                    curentFragmentTag
                )
                return@setOnItemClickListener
            }
        }
    }
}
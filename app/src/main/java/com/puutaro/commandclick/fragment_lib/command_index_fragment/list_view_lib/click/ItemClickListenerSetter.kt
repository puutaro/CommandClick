package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.click

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.ListView
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.common.CommandListManager
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.click.lib.OnEditExecuteEvent
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.click.lib.AppDirectoryAdminEvent
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.click.lib.OnOnceEditExecuteEvent
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.common.DecideEditTag
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.ValidateShell
import com.puutaro.commandclick.proccess.CmdIndexToolbarSwitcher
import com.puutaro.commandclick.proccess.intent.ExecJsOrSellHandler
import com.puutaro.commandclick.proccess.lib.VaridateionErrDialog
import com.puutaro.commandclick.util.*


object ItemClickListenerSetter {
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
            if (
                binding.cmdListSwipeToRefresh.isRefreshing
            ) return@setOnItemClickListener
            val selectedShellFileName = binding.cmdList.getItemAtPosition(
                position
            ) as String
            Keyboard.hiddenKeyboard(
                activity,
                View
            )
            cmdSearchEditText.setText(String())
            CmdIndexToolbarSwitcher.switch(
                cmdIndexFragment,
                false
            )
            if(
                selectedShellFileName.endsWith(
                    CommandClickScriptVariable.HTML_FILE_SUFFIX
                )
            ) {
                BroadCastIntent.send(
                    cmdIndexFragment,
                    "${currentAppDirPath}/$selectedShellFileName"
                )
                updateLastModifiedListView(
                    cmdListView,
                    cmdListAdapter,
                    currentAppDirPath,
                    selectedShellFileName
                )
                return@setOnItemClickListener
            }
            if (
                selectedShellFileName.contains(
                    CommandClickScriptVariable.EMPTY_STRING
                )
            ) return@setOnItemClickListener
            val currentFragmentTag =
                cmdIndexFragment.tag ?: String()
            val appDirAdminTag = context?.getString(
                R.string.app_dir_admin
            )

            val shellContentsList = ReadText(
                currentAppDirPath,
                selectedShellFileName
            ).textToList()
            val validateErrMessage = ValidateShell.correct(
                cmdIndexFragment,
                shellContentsList,
                selectedShellFileName
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
            val languageType =
                JsOrShellFromSuffix.judge(selectedShellFileName)

            val languageTypeToSectionHolderMap =
                CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(languageType)
            val settingSectionStart = languageTypeToSectionHolderMap?.get(
                CommandClickScriptVariable.Companion.HolderTypeName.SETTING_SEC_START
            ) as String
            val settingSectionEnd = languageTypeToSectionHolderMap.get(
                CommandClickScriptVariable.Companion.HolderTypeName.SETTING_SEC_END
            ) as String

            val settingSectionVariableList =
                CommandClickVariables.substituteVariableListFromHolder(
                    shellContentsList,
                    settingSectionStart,
                    settingSectionEnd,
                )

            val editExecuteValue =
                CommandClickVariables.substituteCmdClickVariable(
                    settingSectionVariableList,
                    CommandClickScriptVariable.EDIT_EXECUTE,
                ) ?: SettingVariableSelects.Companion.EditExecuteSelects.NO.name
            when (editExecuteValue) {
                SettingVariableSelects.Companion.EditExecuteSelects.ONCE.name -> {
                    val editFragmentTag = DecideEditTag(
                        shellContentsList,
                        selectedShellFileName
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
                        shellContentsList,
                        selectedShellFileName
                    ).decide(context) ?: return@setOnItemClickListener

                    OnEditExecuteEvent.invoke(
                        cmdIndexFragment,
                        editFragmentTag,
                        sharedPref,
                        selectedShellFileName,
                    )
                    return@setOnItemClickListener
                }
            }
            if (
                currentFragmentTag == cmdIndexFragmentTag
            ) {
                ExecJsOrSellHandler.handle(
                    cmdIndexFragment,
                    currentAppDirPath,
                    selectedShellFileName,
                )
            } else if(
                currentFragmentTag == appDirAdminTag
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
                currentFragmentTag
            )
            return@setOnItemClickListener
        }
    }
}

private fun updateLastModifiedListView (
    cmdListView: ListView,
    cmdListAdapter: ArrayAdapter<String>,
    currentAppDirPath: String,
    selectecJsFileName: String
) {
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
}
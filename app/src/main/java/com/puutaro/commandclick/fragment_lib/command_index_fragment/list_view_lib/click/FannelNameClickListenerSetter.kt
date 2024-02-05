package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.click

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.component.adapter.FannelIndexListAdapter
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.common.CommandListManager
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.click.lib.OnEditExecuteEvent
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.click.lib.OnOnceEditExecuteEvent
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.common.DecideEditTag
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.ValidateShell
import com.puutaro.commandclick.proccess.CmdIndexToolbarSwitcher
import com.puutaro.commandclick.proccess.intent.ExecJsOrSellHandler
import com.puutaro.commandclick.proccess.lib.VariationErrDialog
import com.puutaro.commandclick.util.*
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import java.io.File


object FannelNameClickListenerSetter {
    fun set(
        cmdIndexFragment: CommandIndexFragment,
        currentAppDirPath: String,
        fannelIndexListAdapter: FannelIndexListAdapter
    ){
        val activity = cmdIndexFragment.activity
        val sharedPref =  activity?.getPreferences(Context.MODE_PRIVATE)
        val binding = cmdIndexFragment.binding
        val cmdSearchEditText = binding.cmdSearchEditText
        val cmdListView = binding.cmdList

        fannelIndexListAdapter.fannelNameClickListener = object: FannelIndexListAdapter.OnFannelNameItemClickListener {
            override fun onFannelNameClick(
                itemView: View,
                holder: FannelIndexListAdapter.FannelIndexListViewHolder
            ) {
                if (
                    binding.cmdListSwipeToRefresh.isRefreshing
                ) return
                val selectedShellFileName = holder.fannelNameTextView.text.toString()
                Keyboard.hiddenKeyboard(
                    activity,
                    itemView
                )
                cmdSearchEditText.setText(String())
                CmdIndexToolbarSwitcher.switch(
                    cmdIndexFragment,
                    false
                )
                if(
                    selectedShellFileName.endsWith(
                        UsePath.HTML_FILE_SUFFIX
                    )
                ) {
                    BroadCastIntent.sendUrlCon(
                        cmdIndexFragment,
                        "${currentAppDirPath}/$selectedShellFileName"
                    )
                    updateLastModifiedListView(
                        cmdListView,
                        currentAppDirPath,
                        selectedShellFileName
                    )
                    return
                }
                if (
                    selectedShellFileName.contains(
                        CommandClickScriptVariable.EMPTY_STRING
                    )
                ) return
                val currentFragmentTag =
                    cmdIndexFragment.tag ?: String()

                val shellContentsList = ReadText(
                    File(currentAppDirPath, selectedShellFileName).absolutePath
                ).textToList()
                val validateErrMessage = ValidateShell.correct(
                    cmdIndexFragment,
                    shellContentsList,
                    selectedShellFileName
                )
                if (validateErrMessage.isNotEmpty()) {
                    val shellScriptPath =
                        "${currentAppDirPath}/${selectedShellFileName}"
                    VariationErrDialog.show(
                        cmdIndexFragment,
                        shellScriptPath,
                        validateErrMessage
                    )
                    return
                }
                val languageType =
                    CommandClickVariables.judgeJsOrShellFromSuffix(selectedShellFileName)

                val languageTypeToSectionHolderMap =
                    CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(languageType)
                val settingSectionStart = languageTypeToSectionHolderMap?.get(
                    CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
                ) as String
                val settingSectionEnd = languageTypeToSectionHolderMap.get(
                    CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
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
                    ) ?: SettingVariableSelects.EditExecuteSelects.NO.name
                when (editExecuteValue) {
                    SettingVariableSelects.EditExecuteSelects.ONCE.name -> {
                        val editFragmentTag = DecideEditTag(
                            shellContentsList,
                            currentAppDirPath,
                            selectedShellFileName
                        ).decide()
                            ?: return
                        OnOnceEditExecuteEvent.invoke(
                            cmdIndexFragment,
                            sharedPref,
                            selectedShellFileName,
                            editFragmentTag,
                        )
                        return
                    }
                    SettingVariableSelects.EditExecuteSelects.ALWAYS.name -> {
                        val editFragmentTag = DecideEditTag(
                            shellContentsList,
                            currentAppDirPath,
                            selectedShellFileName
                        ).decide() ?: return

                        OnEditExecuteEvent.invoke(
                            cmdIndexFragment,
                            editFragmentTag,
                            sharedPref,
                            selectedShellFileName,
                        )
                        return
                    }
                }
                ExecJsOrSellHandler.handle(
                    cmdIndexFragment,
                    currentAppDirPath,
                    selectedShellFileName,
                )
                CommandListManager.execListUpdateForCmdIndex(
                    currentAppDirPath,
                    cmdListView,
                )

                val listener = cmdIndexFragment.context as? CommandIndexFragment.OnListItemClickListener
                listener?.onListItemClicked(
                    currentFragmentTag
                )
                return
            }
        }
    }
}

private fun updateLastModifiedListView (
    cmdListView: RecyclerView,
    currentAppDirPath: String,
    selectecJsFileName: String
) {
    FileSystems.updateLastModified(
        File(
            currentAppDirPath,
            selectecJsFileName
        ).absolutePath
    )
    CommandListManager.execListUpdateForCmdIndex(
        currentAppDirPath,
        cmdListView,
    )
}
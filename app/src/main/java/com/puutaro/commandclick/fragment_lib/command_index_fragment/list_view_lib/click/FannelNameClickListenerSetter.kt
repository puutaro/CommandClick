package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.click

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment_lib.command_index_fragment.common.CommandListManager
import com.puutaro.commandclick.util.file.FileSystems
import java.io.File


//object FannelNameClickListenerSetter {
//    fun set(
//        cmdIndexFragment: CommandIndexFragment,
////        currentAppDirPath: String,
//        fannelIndexListAdapter: FannelIndexListAdapter
//    ){
//        val context = cmdIndexFragment.context
//        val activity = cmdIndexFragment.activity
//        val binding = cmdIndexFragment.binding
//        val cmdSearchEditText = binding.cmdSearchEditText
////        val cmdListView = binding.cmdList
//        val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath
//
//        fannelIndexListAdapter.fannelNameClickListener = object: FannelIndexListAdapter.OnFannelNameItemClickListener {
//            override fun onFannelNameClick(
//                itemView: View,
//                holder: FannelIndexListAdapter.FannelIndexListViewHolder
//            ) {
////                if (
////                    binding.cmdListSwipeToRefresh.isRefreshing
////                ) return
//                val selectedShellFileName = holder.fannelNameTextView.text.toString()
//                Keyboard.hiddenKeyboard(
//                    activity,
//                    itemView
//                )
//                cmdSearchEditText.setText(String())
//                CmdIndexToolbarSwitcher.switch(
//                    cmdIndexFragment,
//                    false
//                )
//                if(
//                    selectedShellFileName.endsWith(
//                        UsePath.HTML_FILE_SUFFIX
//                    )
//                ) {
//                    BroadCastIntent.sendUrlCon(
//                        context,
//                        "${cmdclickDefaultAppDirPath}/$selectedShellFileName"
//                    )
////                    updateLastModifiedListView(
////                        cmdListView,
//////                        currentAppDirPath,
////                        selectedShellFileName
////                    )
//                    return
//                }
//                if (
//                    selectedShellFileName.contains(
//                        CommandClickScriptVariable.EMPTY_STRING
//                    )
//                ) return
//                val currentFragmentTag =
//                    cmdIndexFragment.tag ?: String()
//
//                val mainFannelContentsList = ReadText(
//                    File(cmdclickDefaultAppDirPath, selectedShellFileName).absolutePath
//                ).textToList()
//                val validateErrMessage = ValidateShell.correct(
//                    cmdIndexFragment,
//                    mainFannelContentsList,
//                    selectedShellFileName
//                )
//                if (validateErrMessage.isNotEmpty()) {
//                    val shellScriptPath =
//                        "${cmdclickDefaultAppDirPath}/${selectedShellFileName}"
//                    VariationErrDialog.show(
//                        cmdIndexFragment,
//                        shellScriptPath,
//                        validateErrMessage
//                    )
//                    return
//                }
//                val setReplaceVariableMap =
//                    JavaScriptLoadUrl.createMakeReplaceVariableMapHandler(
//                        context,
//                        mainFannelContentsList,
////                        currentAppDirPath,
//                        selectedShellFileName,
//                    )
////                val languageType =
////                    CommandClickVariables.judgeJsOrShellFromSuffix(selectedShellFileName)
////                val languageTypeToSectionHolderMap =
////                    CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(languageType)
////                val settingSectionStart = languageTypeToSectionHolderMap?.get(
////                    CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
////                ) as String
////                val settingSectionEnd = languageTypeToSectionHolderMap.get(
////                    CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
////                ) as String
//                val settingSectionVariableList = CommandClickVariables.extractValListFromHolder(
//                    mainFannelContentsList,
//                    CommandClickScriptVariable.SETTING_SEC_START,
//                    CommandClickScriptVariable.SETTING_SEC_END,
////                    settingSectionStart,
////                    settingSectionEnd
//                ).let {
//                    SetReplaceVariabler.execReplaceByReplaceVariables(
//                        it?.joinToString("\n") ?: String(),
//                        setReplaceVariableMap,
////                        currentAppDirPath,
//                        selectedShellFileName,
//                    ).split("\n")
//                }
//
//                val editExecuteValue =
//                    CommandClickVariables.substituteCmdClickVariable(
//                        settingSectionVariableList,
//                        CommandClickScriptVariable.EDIT_EXECUTE,
//                    ) ?: SettingVariableSelects.EditExecuteSelects.NO.name
//                when (editExecuteValue) {
//                    SettingVariableSelects.EditExecuteSelects.ALWAYS.name -> {
//                        val fannelState = FannelStateManager.getState(
////                            currentAppDirPath,
//                            selectedShellFileName,
//                            settingSectionVariableList,
//                            setReplaceVariableMap,
//                        )
//                        val editFragmentTag = DecideEditTag(
//                            mainFannelContentsList,
////                            currentAppDirPath,
//                            selectedShellFileName,
//                            fannelState
//                        ).decide() ?: return
//                        OnEditExecuteEvent.invoke(
//                            cmdIndexFragment,
//                            editFragmentTag,
////                            currentAppDirPath,
//                            selectedShellFileName,
//                            fannelState
//                        )
//                        return
//                    }
//                }
//                ExecJsOrSellHandler.handle(
//                    cmdIndexFragment,
////                    currentAppDirPath,
//                    selectedShellFileName,
//                    mainFannelContentsList,
//                )
////                CommandListManager.execListUpdateForCmdIndex(
//////                    currentAppDirPath,
////                    cmdListView,
////                )
//
//                val listener = cmdIndexFragment.context as? CommandIndexFragment.OnListItemClickListener
//                listener?.onListItemClicked(
//                    currentFragmentTag
//                )
//                return
//            }
//        }
//    }
//}

private fun updateLastModifiedListView (
    cmdListView: RecyclerView,
//    currentAppDirPath: String,
    selectecJsFileName: String
) {
    FileSystems.updateLastModified(
        File(
            UsePath.cmdclickDefaultAppDirPath,
            selectecJsFileName
        ).absolutePath
    )
    CommandListManager.execListUpdateForCmdIndex(
//        currentAppDirPath,
        cmdListView,
    )
}
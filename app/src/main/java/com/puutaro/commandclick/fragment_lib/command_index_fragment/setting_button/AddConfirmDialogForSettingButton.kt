package com.puutaro.commandclick.fragment_lib.command_index_fragment.setting_button

import android.app.AlertDialog
import android.content.DialogInterface
import android.widget.ArrayAdapter
import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.databinding.CommandIndexFragmentBinding
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.CommandListManager
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.RecordNumToMapNameValueInHolderColumn
import com.puutaro.commandclick.proccess.CommentOutLabelingSection
import com.puutaro.commandclick.util.*


class AddConfirmDialogForSettingButton {
    companion object {
        fun invoke(
            cmdIndexFragment: CommandIndexFragment,
            binding: CommandIndexFragmentBinding,
            currentAppDirPath: String,
            shellScriptName: String,
            cmdListAdapter: ArrayAdapter<String>,
        ){
            val context = cmdIndexFragment.context
            val shellScriptPath = "${currentAppDirPath}/${shellScriptName}"
            val alertDialog = AlertDialog.Builder(context)
                .setTitle(
                    "Add bellow contents, ok?"
                )
                .setMessage(
                    "\tpath: path: ${shellScriptPath}"
                )
                .setPositiveButton("OK", DialogInterface.OnClickListener {
                        dialog, which ->
                    val shellContentsList = ReadText(
                        currentAppDirPath,
                        shellScriptName
                    ).txetToList()


                    val newShellScriptName = makeNewShellName(
                        shellContentsList,
                        shellScriptName,
                    )

                    val shellScriptContentsLabelCommenOut = CommentOutLabelingSection.commentOut(
                        shellContentsList
                    )

                    val shellScriptContentsQuoteComp = makeShellScriptContentsQuoteComp(
                        shellScriptContentsLabelCommenOut,
                        newShellScriptName
                    )

                    if(newShellScriptName != shellScriptName){
                        FileSystems.writeFile(
                            currentAppDirPath,
                            newShellScriptName,
                            shellScriptContentsQuoteComp
                        )
                        FileSystems.removeFiles(
                            currentAppDirPath,
                            shellScriptName
                        )
                    } else {
                        FileSystems.writeFile(
                            currentAppDirPath,
                            shellScriptName,
                            shellScriptContentsQuoteComp
                        )
                    }
                    CommandListManager.execListUpdate(
                        currentAppDirPath,
                        cmdListAdapter,
                        binding.cmdList,
                    )
                })
                .setNegativeButton("NO", DialogInterface.OnClickListener { dialog, which ->
                    FileSystems.removeFiles(
                        currentAppDirPath,
                        shellScriptName,
                    )
                    CommandListManager.execListUpdate(
                        currentAppDirPath,
                        cmdListAdapter,
                        binding.cmdList,
                    )
                })
                .show()
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(
                context?.getColor(android.R.color.black) as Int
            );
            alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(
                context.getColor(android.R.color.black)
            );
        }
    }
}


internal fun makeNewShellName(
    shellContentsList: List<String>,
    shellScriptName: String,
): String {
    val substituteSettingVariableList =
        CommandClickVariables.substituteVariableListFromHolder(
            shellContentsList,
            CommandClickShellScript.SETTING_SECTION_START,
            CommandClickShellScript.SETTING_SECTION_END,
        )
    val newShellScriptNameSource = CommandClickVariables.substituteCmdClickVariable(
        substituteSettingVariableList,
        CommandClickShellScript.SHELL_FILE_NAME
    )?.trim('"')?.trim('\'')
    return if(
        newShellScriptNameSource == null
        || newShellScriptNameSource == String()
    ) {
        shellScriptName
    }else if(
        newShellScriptNameSource.endsWith(
            CommandClickShellScript.SHELL_FILE_SUFFIX
        )
    ) {
        newShellScriptNameSource
    } else {
        newShellScriptNameSource +
                CommandClickShellScript.SHELL_FILE_SUFFIX
    }
}


internal fun makeShellScriptContentsQuoteComp(
    shellContentsList: List<String>,
    newShellScriptName: String
): String {
    val recordNumToMapNameValueInCommandHolder =
        RecordNumToMapNameValueInHolder.parse(
            shellContentsList,
            CommandClickShellScript.CMD_VARIABLE_SECTION_START,
            CommandClickShellScript.CMD_VARIABLE_SECTION_END
        )
    val recordNumToMapNameValueInSettingHolder =
        RecordNumToMapNameValueInHolder.parse(
            shellContentsList,
            CommandClickShellScript.SETTING_SECTION_START,
            CommandClickShellScript.SETTING_SECTION_END,
            true,
            newShellScriptName
        )
    val shellScriptListQuoteCompForCmdVariables = quoteCompShellScriptListVariables(
        shellContentsList,
        recordNumToMapNameValueInCommandHolder
    )
    return quoteCompShellScriptListVariables(
        shellScriptListQuoteCompForCmdVariables,
        recordNumToMapNameValueInSettingHolder,
    ).joinToString("\n")
}



internal fun quoteCompShellScriptListVariables(
    shellContentsList: List<String>,
    recordNumToMapNameValueInHolder: Map<Int, Map<String, String>?>?
): List<String> {
    if(recordNumToMapNameValueInHolder == null) return shellContentsList
    return (0..shellContentsList.size - 1).map {
            currentOrder ->
        val getReplaceValue = recordNumToMapNameValueInHolder.get(currentOrder)
        if(getReplaceValue.isNullOrEmpty()){
            shellContentsList[currentOrder]
        } else {
            val currentVariableName = getReplaceValue.get(
                RecordNumToMapNameValueInHolderColumn.VARIABLE_NAME.name
            )
            val currentVariableValue = getReplaceValue.get(
                RecordNumToMapNameValueInHolderColumn.VARIABLE_VALUE.name
            ) ?: String()
            "${currentVariableName}=${ComleteQuote.comp(currentVariableValue)}"
        }
    }
}
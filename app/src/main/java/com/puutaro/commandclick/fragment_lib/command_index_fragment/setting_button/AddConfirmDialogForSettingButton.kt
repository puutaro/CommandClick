package com.puutaro.commandclick.fragment_lib.command_index_fragment.setting_button

import android.app.AlertDialog
import android.content.DialogInterface
import android.widget.ArrayAdapter
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.LanguageTypeSelects
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.common.variable.edit.RecordNumToMapNameValueInHolderColumn
import com.puutaro.commandclick.databinding.CommandIndexFragmentBinding
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.common.CommandListManager
import com.puutaro.commandclick.proccess.CommentOutLabelingSection
import com.puutaro.commandclick.util.*


object AddConfirmDialogForSettingButton {
    fun invoke(
        cmdIndexFragment: CommandIndexFragment,
        binding: CommandIndexFragmentBinding,
        currentAppDirPath: String,
        shellScriptName: String,
        cmdListAdapter: ArrayAdapter<String>,
        languageTypeSelects: LanguageTypeSelects
    ){
        val context = cmdIndexFragment.context
        val shellScriptPath = "${currentAppDirPath}/${shellScriptName}"
        val languageTypeToSectionHolderMap =
            CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP
                .get(languageTypeSelects)
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
                ).textToList()


                val newShellScriptName = makeNewShellName(
                    shellContentsList,
                    shellScriptName,
                    languageTypeSelects,
                    languageTypeToSectionHolderMap
                )

                val shellScriptContentsLabelCommentOut = CommentOutLabelingSection.commentOut(
                    shellContentsList,
                    shellScriptName
                )
                val shellScriptContentsQuoteComp = makeShellScriptContentsQuoteComp(
                    shellScriptContentsLabelCommentOut,
                    newShellScriptName,
                    languageTypeToSectionHolderMap
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
            .setNegativeButton("NO", DialogInterface.OnClickListener {
                    dialog, which ->
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


private fun makeNewShellName(
    shellContentsList: List<String>,
    shellScriptName: String,
    languageTypeSelects: LanguageTypeSelects,
    languageTypeToSectionHolderMap: Map<CommandClickScriptVariable.HolderTypeName, String>?

): String {
    if(languageTypeToSectionHolderMap.isNullOrEmpty()) return shellScriptName
    val substituteSettingVariableList =
        CommandClickVariables.substituteVariableListFromHolder(
            shellContentsList,
            languageTypeToSectionHolderMap[CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START],
            languageTypeToSectionHolderMap[CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END],
        )
    val newShellScriptNameSource = CommandClickVariables.substituteCmdClickVariable(
        substituteSettingVariableList,
        CommandClickScriptVariable.SCRIPT_FILE_NAME
    )?.trim('"')?.trim('\'')
    val languageSuffix = when(
        languageTypeSelects
    ){
        LanguageTypeSelects.SHELL_SCRIPT -> UsePath.SHELL_FILE_SUFFIX
        else -> UsePath.JS_FILE_SUFFIX
    }
    return if(
        newShellScriptNameSource == null
        || newShellScriptNameSource == String()
    ) shellScriptName
    else if (
        newShellScriptNameSource.endsWith(
            languageSuffix
        )
    ) newShellScriptNameSource
    else {
        newShellScriptNameSource +
                languageSuffix
    }
}


private fun makeShellScriptContentsQuoteComp(
    shellContentsList: List<String>,
    newShellScriptName: String,
    languageTypeToSectionHolderMap: Map<CommandClickScriptVariable.HolderTypeName, String>?
): String {
    val recordNumToMapNameValueInCommandHolder =
        RecordNumToMapNameValueInHolder.parse(
            shellContentsList,
            languageTypeToSectionHolderMap?.get(
                CommandClickScriptVariable.HolderTypeName.CMD_SEC_START
            ) as String,
            languageTypeToSectionHolderMap[
                    CommandClickScriptVariable.HolderTypeName.CMD_SEC_END
            ] as String,
        )
    val recordNumToMapNameValueInSettingHolder =
        RecordNumToMapNameValueInHolder.parse(
            shellContentsList,
            languageTypeToSectionHolderMap.get(CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START) as String,
            languageTypeToSectionHolderMap[CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END] as String,
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
    return (shellContentsList.indices).map {
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
            "${currentVariableName}=${CompleteQuote.comp(currentVariableValue)}"
        }
    }
}
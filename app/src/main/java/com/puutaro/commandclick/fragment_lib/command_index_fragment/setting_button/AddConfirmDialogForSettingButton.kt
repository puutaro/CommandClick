package com.puutaro.commandclick.fragment_lib.command_index_fragment.setting_button

import android.app.Dialog
import android.view.Gravity
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.LanguageTypeSelects
import com.puutaro.commandclick.common.variable.edit.RecordNumToMapNameValueInHolderColumn
import com.puutaro.commandclick.databinding.CommandIndexFragmentBinding
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.common.CommandListManager
import com.puutaro.commandclick.proccess.CommentOutLabelingSection
import com.puutaro.commandclick.util.*


object AddConfirmDialogForSettingButton {

    private var deleteConfirmDialog: Dialog? = null
    fun invoke(
        cmdIndexFragment: CommandIndexFragment,
        currentAppDirPath: String,
        shellScriptName: String,
        languageTypeSelects: LanguageTypeSelects
    ){
        val context = cmdIndexFragment.context
            ?: return
        val binding = cmdIndexFragment.binding
        val shellScriptPath = "${currentAppDirPath}/${shellScriptName}"
        val languageTypeToSectionHolderMap =
            CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP
                .get(languageTypeSelects)


        deleteConfirmDialog = Dialog(
            context
        )
        deleteConfirmDialog?.setContentView(
            com.puutaro.commandclick.R.layout.confirm_text_dialog
        )
        val confirmTitleTextView =
            deleteConfirmDialog?.findViewById<AppCompatTextView>(
                com.puutaro.commandclick.R.id.confirm_text_dialog_title
            )
        confirmTitleTextView?.text = "Add bellow contents, ok?"
        val confirmContentTextView =
            deleteConfirmDialog?.findViewById<AppCompatTextView>(
                com.puutaro.commandclick.R.id.confirm_text_dialog_text_view
            )
        confirmContentTextView?.text = "\tpath: path: ${shellScriptPath}"
        cancelButtonListener(
            binding,
            currentAppDirPath,
            shellScriptName
        )
        okButtonListener(
            binding,
            languageTypeToSectionHolderMap,
            currentAppDirPath,
            shellScriptName,
        )
        deleteConfirmDialog?.setOnCancelListener {
            deleteConfirmDialog?.dismiss()
        }
        deleteConfirmDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        deleteConfirmDialog?.window?.setGravity(
            Gravity.BOTTOM
        )
        deleteConfirmDialog?.show()
    }

    private fun cancelButtonListener(
        binding: CommandIndexFragmentBinding,
        currentAppDirPath: String,
        shellScriptName: String
    ){
        val confirmCancelButton =
            deleteConfirmDialog?.findViewById<AppCompatImageButton>(
                com.puutaro.commandclick.R.id.confirm_text_dialog_cancel
            )
        confirmCancelButton?.setOnClickListener {
            deleteConfirmDialog?.dismiss()
            FileSystems.removeFiles(
                currentAppDirPath,
                shellScriptName,
            )
            CommandListManager.execListUpdateForCmdIndex(
                currentAppDirPath,
                binding.cmdList,
            )
        }
    }

    private fun okButtonListener(
        binding: CommandIndexFragmentBinding,
        languageTypeToSectionHolderMap: Map<CommandClickScriptVariable.HolderTypeName, String>?,
        currentAppDirPath: String,
        shellScriptName: String,
    ){
        val confirmOkButton =
            deleteConfirmDialog?.findViewById<AppCompatImageButton>(
                com.puutaro.commandclick.R.id.confirm_text_dialog_ok
            )
        confirmOkButton?.setOnClickListener {
            deleteConfirmDialog?.dismiss()
            confirmOkExecutor(
                binding,
                languageTypeToSectionHolderMap,
                currentAppDirPath,
                shellScriptName,
            )
        }
    }

    private fun confirmOkExecutor(
        binding: CommandIndexFragmentBinding,
        languageTypeToSectionHolderMap: Map<CommandClickScriptVariable.HolderTypeName, String>?,
        currentAppDirPath: String,
        shellScriptName: String,
    ){
        val shellContentsList = ReadText(
            currentAppDirPath,
            shellScriptName
        ).textToList()

        val shellScriptContentsLabelCommentOut = CommentOutLabelingSection.commentOut(
            shellContentsList,
            shellScriptName
        )
        val shellScriptContentsQuoteComp = makeShellScriptContentsQuoteComp(
            shellScriptContentsLabelCommentOut,
            languageTypeToSectionHolderMap
        )
        FileSystems.writeFile(
            currentAppDirPath,
            shellScriptName,
            shellScriptContentsQuoteComp
        )
        CommandListManager.execListUpdateForCmdIndex(
            currentAppDirPath,
            binding.cmdList,
        )
    }
}


private fun makeShellScriptContentsQuoteComp(
    shellContentsList: List<String>,
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



private fun quoteCompShellScriptListVariables(
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
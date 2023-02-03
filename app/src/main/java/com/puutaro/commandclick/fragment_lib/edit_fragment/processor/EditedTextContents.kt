package com.puutaro.commandclick.fragment_lib.edit_fragment.processor

import android.content.Context
import android.widget.EditText
import android.widget.LinearLayout
import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.databinding.EditFragmentBinding
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.EditTextIdForEdit
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.RecordNumToMapNameValueInHolderColumn
import com.puutaro.commandclick.proccess.CommentOutLabelingSection
import com.puutaro.commandclick.util.ComleteQuote
import com.puutaro.commandclick.util.CommandClickVariables.Companion.substituteCmdClickVariable
import com.puutaro.commandclick.util.CommandClickVariables.Companion.substituteVariableListFromHolder
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.SharePreffrenceMethod


class EditedTextContents(
    binding: EditFragmentBinding,
    private val editFragment: EditFragment,
    readSharePreffernceMap: Map<String, String>
) {

    private val curentAppDirPath = readSharePreffernceMap.get(
        SharePrefferenceSetting.current_app_dir.name
    ) ?: SharePrefferenceSetting.current_app_dir.defalutStr
    private val currentShellFileName = readSharePreffernceMap.get(
        SharePrefferenceSetting.current_shell_file_name.name
    ) ?: SharePrefferenceSetting.current_shell_file_name.defalutStr
    private val shellContentsList = ShellContntsList(
        binding.editLinearLayout
    )

    fun updateByCommandVariables(
        shellContentsList: List<String>,
        recordNumToMapNameValueInCommandHolder: Map<Int, Map<String, String>?>? = null,
    ): List<String> {
        return if(recordNumToMapNameValueInCommandHolder.isNullOrEmpty()) {
            shellContentsList
        } else {
            this.shellContentsList.update(
                recordNumToMapNameValueInCommandHolder,
                shellContentsList,
                EditTextIdForEdit.COMMAND_VARIABLE.id
            )
        }
    }

    fun updateBySettingVariables(
        editedShellContentsList: List<String>,
        recordNumToMapNameValueInSettingHolder: Map<Int, Map<String, String>?>? = null
    ): List<String> {
        return if (recordNumToMapNameValueInSettingHolder == null) {
            editedShellContentsList
        } else {
            shellContentsList.update(
                recordNumToMapNameValueInSettingHolder,
                editedShellContentsList,
                EditTextIdForEdit.SETTING_VARIABLE.id
            )
        }
    }

    fun save(
        lastShellContentsList: List<String>,
    ){
        if(lastShellContentsList.size == 0) return

        val submitShellContentsList = CommentOutLabelingSection.commentOut(
            lastShellContentsList
        )

        val updateShellFileName = makeUpdateShellFileName(
            submitShellContentsList,
            currentShellFileName
        )

        FileSystems.writeFile(
            curentAppDirPath,
            updateShellFileName,
            submitShellContentsList.joinToString("\n")
        )
        if(updateShellFileName == currentShellFileName) return
        val sharePref =  editFragment.activity?.getPreferences(Context.MODE_PRIVATE)
        SharePreffrenceMethod.putSharePreffrence(
            sharePref,
            mapOf(
                SharePrefferenceSetting.current_shell_file_name.name
                        to updateShellFileName
            )
        )
        FileSystems.removeFiles(
            curentAppDirPath,
            currentShellFileName,
        )
    }
}


private fun makeUpdateShellFileName(
    lastShellContentsList: List<String>,
    currentShellFileName: String
): String {
    val substituteSettingVariableList = substituteVariableListFromHolder(
        lastShellContentsList,
        CommandClickShellScript.SETTING_SECTION_START,
        CommandClickShellScript.SETTING_SECTION_END,
    )
    val updateShellFileNameSource = substituteCmdClickVariable(
        substituteSettingVariableList,
        CommandClickShellScript.SHELL_FILE_NAME
    ) ?: currentShellFileName
    val shellFileSuffix = CommandClickShellScript.SHELL_FILE_SUFFIX
    return if(
        updateShellFileNameSource.endsWith(shellFileSuffix)
    ) {
        updateShellFileNameSource
    } else {
        updateShellFileNameSource + shellFileSuffix
    }
}

private class ShellContntsList(
    private val editLinearLayout: LinearLayout,
) {
    fun update(
        recordNumToMapNameValueInHolder: Map<Int, Map<String, String>?>,
        shellContentsList: List<String>,
        startIdNum: Int
    ): List<String> {
        val factRecordNumToNameToValueInHolderSize = recordNumToMapNameValueInHolder.size - 1
        val editedRecordNumToNameToValue = (0..factRecordNumToNameToValueInHolderSize).map {
            val currentId = startIdNum + it
            val editTextView = editLinearLayout.findViewById<EditText>(currentId)
            val currentRecordNumToMapNameValue = recordNumToMapNameValueInHolder.entries.elementAt(it)
            val currentVriableValue = editTextView.text.toString()
            currentRecordNumToMapNameValue.key to
                    mapOf(
                        RecordNumToMapNameValueInHolderColumn.VARIABLE_NAME.name
                                to editTextView.tag.toString(),
                        RecordNumToMapNameValueInHolderColumn.VARIABLE_VALUE.name
                                to ComleteQuote.comp(currentVriableValue)
                    )
        }.toMap()
        return (0..shellContentsList.size - 1).map {
                currentOrder ->
            val getReplaceValue = editedRecordNumToNameToValue.get(currentOrder)
            if(getReplaceValue.isNullOrEmpty()){
                shellContentsList[currentOrder]
            } else {
                val currentVariableName = getReplaceValue.get(
                    RecordNumToMapNameValueInHolderColumn.VARIABLE_NAME.name
                )
                val currentVariableValue = getReplaceValue.get(
                    RecordNumToMapNameValueInHolderColumn.VARIABLE_VALUE.name
                )
                "${currentVariableName}=${currentVariableValue}"
            }
        }
    }
}
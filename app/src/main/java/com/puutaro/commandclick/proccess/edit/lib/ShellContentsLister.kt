package com.puutaro.commandclick.proccess.edit.lib

import android.widget.EditText
import android.widget.LinearLayout
import com.puutaro.commandclick.common.variable.edit.RecordNumToMapNameValueInHolderColumn
import com.puutaro.commandclick.util.CompleteQuote

class ShellContentsLister(
    private val editLinearLayout: LinearLayout,
) {
    fun update(
        recordNumToMapNameValueInHolder: Map<Int, Map<String, String>?>,
        shellContentsList: List<String>,
        startIdNum: Int
    ): List<String> {
        val factRecordNumToNameToValueInHolderSize = recordNumToMapNameValueInHolder.size - 1
        if(factRecordNumToNameToValueInHolderSize <= -1) return shellContentsList
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
                                to CompleteQuote.comp(currentVriableValue)
                    )
        }.toMap()
        val processShellScriptSize = shellContentsList.size - 1
        if(processShellScriptSize <= -1) return shellContentsList
        return (0..processShellScriptSize).map {
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
                )?.let {
                    CompleteQuote.comp(it)
                }
                "${currentVariableName}=${currentVariableValue}"
            }
        }
    }
}

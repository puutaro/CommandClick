package com.puutaro.commandclick.proccess.edit.lib

import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.puutaro.commandclick.common.variable.edit.RecordNumToMapNameValueInHolderColumn
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.util.CompleteQuote
import com.puutaro.commandclick.util.LogSystems
import org.eclipse.jgit.api.errors.CannotDeleteCurrentBranchException

class ScriptContentsLister(
    private val editLinearLayoutList: List<LinearLayout>
) {
    fun update(
        recordNumToMapNameValueInHolder: Map<Int, Map<String, String>?>,
        scriptContentsList: List<String>,
        startIdNum: Int
    ): List<String> {
        val factRecordNumToNameToValueInHolderSize = recordNumToMapNameValueInHolder.size - 1
        if(factRecordNumToNameToValueInHolderSize <= -1) return scriptContentsList
        val editedRecordNumToNameToValue = (0..factRecordNumToNameToValueInHolderSize).map {
            val currentId = startIdNum + it
            val editTextView = findEditTextView(
                currentId,
                editLinearLayoutList
            ) ?: return@map String() to emptyMap<String, String>()
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
        val processScriptSize = scriptContentsList.size - 1
        if(processScriptSize <= -1) return scriptContentsList
        return (0..processScriptSize).map {
                currentOrder ->
            val getReplaceValue = editedRecordNumToNameToValue.get(currentOrder)
            if(getReplaceValue.isNullOrEmpty()){
                scriptContentsList[currentOrder]
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

    private fun findEditTextView(
        currentId: Int,
        editLinearLayoutList: List<LinearLayout>,
    ): EditText? {
        editLinearLayoutList.forEach {
            val extractedEditText = it.findViewById<EditText>(currentId)
            if(
                extractedEditText != null
            ) return extractedEditText
        }
        LogSystems.stdWarn(
            "no exist editText id: ${currentId}"
        )
        return null
    }
}

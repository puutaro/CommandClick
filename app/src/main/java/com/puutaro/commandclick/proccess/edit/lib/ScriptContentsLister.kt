package com.puutaro.commandclick.proccess.edit.lib

import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import com.puutaro.commandclick.common.variable.edit.RecordNumToMapNameValueInHolderColumn
import com.puutaro.commandclick.util.CompleteQuote

object ScriptContentsLister {
//    fun update(
//        editLinearLayoutList: List<LinearLayoutCompat>,
//        recordNumToMapNameValueInHolder: Map<Int, Map<String, String>?>,
//        scriptContentsList: List<String>,
//        startIdNum: Int
//    ): List<String> {
//        val factRecordNumToNameToValueInHolderSize = recordNumToMapNameValueInHolder.size - 1
//        if(factRecordNumToNameToValueInHolderSize <= -1) return scriptContentsList
//        val editedRecordNumToNameToValue = (0..factRecordNumToNameToValueInHolderSize).map {
//            val currentId = startIdNum + it
//            val editTextView = CcEditComponent.findEditTextView(
//                currentId,
//                editLinearLayoutList
//            ) ?: return@map String() to emptyMap<String, String>()
//            val currentRecordNumToMapNameValue =
//                recordNumToMapNameValueInHolder.entries.elementAt(it)
//            val currentVriableValue = editTextView.text.toString()
//            currentRecordNumToMapNameValue.key to
//                    mapOf(
//                        RecordNumToMapNameValueInHolderColumn.VARIABLE_NAME.name
//                                to editTextView.tag.toString(),
//                        RecordNumToMapNameValueInHolderColumn.VARIABLE_VALUE.name
//                                to CompleteQuote.comp(currentVriableValue)
//                    )
//        }.toMap()
//        val processScriptSize = scriptContentsList.size - 1
//        if(processScriptSize <= -1) return scriptContentsList
//        return (0..processScriptSize).map {
//                currentOrder ->
//            val getReplaceValue = editedRecordNumToNameToValue.get(currentOrder)
//            if(getReplaceValue.isNullOrEmpty()){
//                scriptContentsList[currentOrder]
//            } else {
//                val currentVariableName = getReplaceValue.get(
//                    RecordNumToMapNameValueInHolderColumn.VARIABLE_NAME.name
//                )
//                val currentVariableValue = getReplaceValue.get(
//                    RecordNumToMapNameValueInHolderColumn.VARIABLE_VALUE.name
//                )?.let {
//                    CompleteQuote.comp(it)
//                }
//                "${currentVariableName}=${currentVariableValue}"
//            }
//        }
//    }
}

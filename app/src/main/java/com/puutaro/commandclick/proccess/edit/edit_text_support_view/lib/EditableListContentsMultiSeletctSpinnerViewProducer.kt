package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib

import android.widget.*
import androidx.core.view.isVisible
import com.abdeveloper.library.MultiSelectModel
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.common.variable.edit.SetVariableTypeColumn
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.EditTextSupportViewId
import com.puutaro.commandclick.proccess.edit.lib.ReplaceVariableMapReflecter
import com.puutaro.commandclick.util.*
import java.io.File

object EditableListContentsMultiSeletctSpinnerViewProducer {

    fun make (
        insertTextView: TextView,
        insertEditText: EditText,
        editParameters: EditParameters,
        weight: Float,
        isInsertTextViewVisible: Boolean = false
    ): Button {
        val defaultListLimit = 100
        val context = editParameters.context
        val currentId = editParameters.currentId
        val currentSetVariableMap = editParameters.setVariableMap
        val currentAppDirPath = SharePreffrenceMethod.getReadSharePreffernceMap(
            editParameters.readSharePreffernceMap,
            SharePrefferenceSetting.current_app_dir
        )
        val currentScriptName = SharePreffrenceMethod.getReadSharePreffernceMap(
            editParameters.readSharePreffernceMap,
            SharePrefferenceSetting.current_script_file_name
        )
        val throughMark = "-"

        val linearParamsForSpinner = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT,
        )
        linearParamsForSpinner.weight = weight
        val fannelDirName = currentScriptName
            .removeSuffix(CommandClickScriptVariable.JS_FILE_SUFFIX)
            .removeSuffix(CommandClickScriptVariable.SHELL_FILE_SUFFIX) +
                "Dir"
        val elcbList = currentSetVariableMap?.get(
            SetVariableTypeColumn.VARIABLE_TYPE_VALUE.name
        )
            ?.let {
                ScriptPreWordReplacer.replace(
                    it,
                    "${currentAppDirPath}/${currentScriptName}",
                    currentAppDirPath,
                    fannelDirName,
                    currentScriptName
                )
            }.let {
                ReplaceVariableMapReflecter.reflect(
                    BothEdgeQuote.trim(it),
                    editParameters
                )
            }?.split('|')
            ?.firstOrNull()
            ?.split('&')
            ?: emptyList()

        val listContentsFilePath = elcbList.firstOrNull()
            ?: String()
        val listLimit = try {
            elcbList.getOrNull(1)?.toInt()
        } catch (e: Exception){
            defaultListLimit
        } ?: defaultListLimit
        val fileObj = File(listContentsFilePath)
        val parentDir = fileObj.parent ?: String()
        val listFileName = fileObj.name
        FileSystems.createDirs(parentDir)
        val editableSpinnerList = ReadText(
            parentDir,
            listFileName
        ).textToList().filter {
            it.trim().isNotEmpty()
        }
        val updatedEditableSpinnerList = listOf(throughMark) + editableSpinnerList
        val updatedMultiModelArray = ArrayList(
            (updatedEditableSpinnerList.indices).map {
                MultiSelectModel(it, updatedEditableSpinnerList[it])
            }
        )
        val currentSelectList = insertEditText.text.toString().split(',')
        val preSelectedMultiModelIdArray = ArrayList(
            (updatedEditableSpinnerList.indices).filter {
                val currentItem = updatedEditableSpinnerList[it]
                currentSelectList.contains(currentItem)
            }
        )

        val insertButton = Button(context)
        insertButton.id = currentId + EditTextSupportViewId.BUTTON.id
        insertButton.tag = "button${currentId + EditTextSupportViewId.BUTTON.id}"
        val buttonLabel = "LSLs"
        insertButton.setText(buttonLabel)
        insertTextView.isVisible = isInsertTextViewVisible

        insertButton.setOnClickListener {
                innerButtonView ->
            val listener = context as? EditFragment.OnMultiSelectListenerForEdit
            listener?.onMultiSelectForEdit(
                insertTextView.text.toString(),
                insertEditText.id,
                updatedMultiModelArray,
                preSelectedMultiModelIdArray
            )
        }

        val linearParamsForButton = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT,
        )
        linearParamsForButton.weight = weight
        insertButton.layoutParams = linearParamsForButton

        return insertButton
    }
}

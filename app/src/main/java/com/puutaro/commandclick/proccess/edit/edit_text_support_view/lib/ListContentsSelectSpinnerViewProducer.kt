package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib

import android.content.Context
import android.view.View
import android.widget.*
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.common.variable.edit.SetVariableTypeColumn
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.EditTextSupportViewId
import com.puutaro.commandclick.proccess.edit.lib.ReplaceVariableMapReflecter
import com.puutaro.commandclick.proccess.edit.lib.SpinnerInstance
import com.puutaro.commandclick.util.*
import java.io.File

object ListContentsSelectSpinnerViewProducer {
    fun make (
        insertEditText: EditText,
        editParameters: EditParameters,
        weight: Float,
    ): Spinner {
        val defaultListLimit = 100
        val context = editParameters.context
        val currentId = editParameters.currentId
        val linearParamsForSpinner = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT,
        )
        linearParamsForSpinner.weight = weight

        val elcbList = getElcbList(
            editParameters
        )
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

        val insertSpinner = SpinnerInstance.make(
            context,
            editableSpinnerList,
            editParameters.onFixNormalSpinner
        )
        insertSpinner.id = currentId + EditTextSupportViewId.EDITABLE_SPINNER.id
        insertSpinner.tag = "spinnerEdit${currentId + EditTextSupportViewId.EDITABLE_SPINNER.id}"
        val adapter = ArrayAdapter<String>(
            context as Context,
            R.layout.sppinner_layout,
        )

        adapter.addAll(editableSpinnerList)
        insertSpinner.adapter = adapter
        insertSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                val selectedItem = adapter.getItem(pos)
                    ?: return
                val currentSpinnerList = ReadText(
                    parentDir,
                    listFileName
                ).textToList().filter {
                    it.trim().isNotEmpty()
                }
                val updateListContents = listOf(selectedItem) + currentSpinnerList.filter {
                        it != selectedItem
                    }
                FileSystems.writeFile(
                    parentDir,
                    listFileName,
                    updateListContents
                        .take(listLimit)
                        .joinToString("\n")
                )
                val selectUpdatedSpinnerList = listOf(
                    selectedItem,
                ) + currentSpinnerList.filter {
                    it != selectedItem
                }
                adapter.clear()
                adapter.addAll(selectUpdatedSpinnerList)
                adapter.notifyDataSetChanged()
                insertSpinner.setSelection(0)
                insertEditText.setText(selectedItem)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        insertSpinner.layoutParams = linearParamsForSpinner
        return insertSpinner
    }

    fun getElcbList(
        editParameters: EditParameters
    ): List<String> {
        val currentSetVariableMap = editParameters.setVariableMap
        val currentAppDirPath = SharePreffrenceMethod.getReadSharePreffernceMap(
            editParameters.readSharePreffernceMap,
            SharePrefferenceSetting.current_app_dir
        )
        val currentScriptName = SharePreffrenceMethod.getReadSharePreffernceMap(
            editParameters.readSharePreffernceMap,
            SharePrefferenceSetting.current_script_file_name
        )
        val fannelDirName = currentScriptName
            .removeSuffix(CommandClickScriptVariable.JS_FILE_SUFFIX)
            .removeSuffix(CommandClickScriptVariable.SHELL_FILE_SUFFIX) +
                "Dir"
        return currentSetVariableMap?.get(
            SetVariableTypeColumn.VARIABLE_TYPE_VALUE.name
        )?.let {
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
            ?.split('!')
            ?: emptyList()
    }
}


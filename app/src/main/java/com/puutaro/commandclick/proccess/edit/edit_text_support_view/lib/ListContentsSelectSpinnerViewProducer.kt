package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib

import android.content.Context
import android.view.View
import android.widget.*
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.common.variable.edit.SetVariableTypeColumn
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.EditTextSupportViewId
import com.puutaro.commandclick.proccess.edit.lib.ReplaceVariableMapReflecter
import com.puutaro.commandclick.proccess.edit.lib.SpinnerInstance
import com.puutaro.commandclick.util.BothEdgeQuote
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.SharePreffrenceMethod
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
        val currentSetVariableMap = editParameters.setVariableMap
        val currentAppDirPath = SharePreffrenceMethod.getReadSharePreffernceMap(
            editParameters.readSharePreffernceMap,
            SharePrefferenceSetting.current_app_dir
        )
        val linearParamsForSpinner = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT,
        )
        linearParamsForSpinner.weight = weight

        val elcbList = currentSetVariableMap?.get(
            SetVariableTypeColumn.VARIABLE_TYPE_VALUE.name
        )
            ?.replace("\${01}", currentAppDirPath)
            .let {
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
}


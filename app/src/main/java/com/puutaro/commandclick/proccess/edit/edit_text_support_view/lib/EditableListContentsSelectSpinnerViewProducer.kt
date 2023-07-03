package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib

import android.content.Context
import android.view.View
import android.widget.*
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.EditTextSupportViewId
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.SelectJsExecutor
import com.puutaro.commandclick.proccess.edit.lib.SpinnerInstance
import com.puutaro.commandclick.util.*
import java.io.File


object EditableListContentsSelectSpinnerViewProducer {

    fun make (
        insertEditText: EditText,
        editParameters: EditParameters,
        weight: Float,
    ): Spinner {
        val currentFragment = editParameters.currentFragment
        val defaultListLimit = 100
        val context = editParameters.context
        val currentId = editParameters.currentId
        val throughMark = "-"
        val linearParamsForSpinner = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT,
        )
        linearParamsForSpinner.weight = weight

        val elcbMap = ListContentsSelectSpinnerViewProducer.getElcbMap(
            editParameters
        )
        val listContentsFilePath = ListContentsSelectSpinnerViewProducer.getListPath(
            elcbMap,
        )
        val listLimit = ListContentsSelectSpinnerViewProducer.getLimitNum(
            elcbMap,
            defaultListLimit
        )
        val selectJsPath = ListContentsSelectSpinnerViewProducer.getSelectJsPath(
            elcbMap
        )
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

        val insertSpinner = SpinnerInstance.make(
            context,
            updatedEditableSpinnerList,
            editParameters.onFixNormalSpinner
        )
        insertSpinner.id = currentId + EditTextSupportViewId.EDITABLE_SPINNER.id
        insertSpinner.tag = "spinnerEdit${currentId + EditTextSupportViewId.EDITABLE_SPINNER.id}"
        val adapter = ArrayAdapter<String>(
            context as Context,
            R.layout.sppinner_layout,
        )

        adapter.addAll(updatedEditableSpinnerList)
        insertSpinner.adapter = adapter
        insertSpinner.setOnTouchListener(View.OnTouchListener {
                v, event ->
            val currentSpinnerList = ReadText(
                parentDir,
                listFileName
            ).textToList().filter {
                it.trim().isNotEmpty()
            }
            val currentEditableSpinnerList = listOf(throughMark) + currentSpinnerList
            val arrayAdapter = insertSpinner.adapter as ArrayAdapter<String>
            arrayAdapter.clear()
            arrayAdapter.addAll(currentEditableSpinnerList)
            adapter.notifyDataSetChanged()
            insertSpinner.setSelection(0)
            v.performClick()
            true
        })
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
                val updateListContents = if(selectedItem == throughMark) {
                    currentSpinnerList.filter {
                        it != selectedItem
                    }
                } else {
                    listOf(selectedItem) + currentSpinnerList.filter {
                        it != selectedItem
                                && it != throughMark
                    }
                }
                FileSystems.writeFile(
                    parentDir,
                    listFileName,
                    updateListContents
                        .take(listLimit)
                        .joinToString("\n")
                )
                val selectUpdatedSpinnerList = if(
                    selectedItem == throughMark
                ){
                    listOf(throughMark) + currentSpinnerList
                } else listOf(
                    throughMark,
                    selectedItem,
                ) + currentSpinnerList.filter {
                    it != selectedItem
                }
                adapter.clear()
                adapter.addAll(selectUpdatedSpinnerList)
                adapter.notifyDataSetChanged()
                insertSpinner.setSelection(0)
                if(
                    selectedItem == throughMark
                ) return
                insertEditText.setText(selectedItem)
                SelectJsExecutor.exec(
                    currentFragment,
                    selectJsPath,
                    selectedItem
                )
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        insertSpinner.layoutParams = linearParamsForSpinner
        return insertSpinner
    }
}


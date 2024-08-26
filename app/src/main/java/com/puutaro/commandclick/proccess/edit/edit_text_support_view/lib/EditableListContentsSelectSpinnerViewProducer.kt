package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib

import android.content.Context
import android.view.View
import android.widget.*
import androidx.appcompat.widget.LinearLayoutCompat
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.EditTextSupportViewId
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.ListContentsSelectSpinnerViewProducer.setInitMarkToListContents
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.SelectJsExecutor
import com.puutaro.commandclick.proccess.edit.lib.SpinnerInstance
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.state.FannelInfoTool
import java.io.File


object EditableListContentsSelectSpinnerViewProducer {

    fun make (
        insertEditText: EditText,
        editParameters: EditParameters,
        currentComponentIndex: Int,
        weight: Float,
    ): Spinner {
        val currentFragment = editParameters.currentFragment
        val fannelInfoMap = editParameters.fannelInfoMap
//        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//            fannelInfoMap
//        )
        val scriptName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
        val defaultListLimit = 100
        val context = editParameters.context
        val currentId = editParameters.currentId
        val throughMark = "-"
        val linearParamsForSpinner = LinearLayoutCompat.LayoutParams(
            0,
            LinearLayoutCompat.LayoutParams.MATCH_PARENT,
        )
        linearParamsForSpinner.weight = weight

        val elcbMap = ListContentsSelectSpinnerViewProducer.getElsbMap(
            editParameters,
            currentComponentIndex
        )
        when(currentFragment){
            is EditFragment -> currentFragment.listConSelectBoxMapList.add(elcbMap)
        }
        val listContentsFilePath =
            elcbMap?.get(
                ListContentsSelectSpinnerViewProducer.ListContentsEditKey.listPath.name)
                ?: String()
        val listLimit = ListContentsSelectSpinnerViewProducer.getLimitNum(
            elcbMap,
            defaultListLimit
        )
        ListContentsSelectSpinnerViewProducer.execCompList(
            editParameters,
            elcbMap,
            listContentsFilePath,
        )
        val selectJsPath = ListContentsSelectSpinnerViewProducer.getSelectJsPath(
            elcbMap,
//            currentAppDirPath,
            scriptName,
        )
        val initMark = elcbMap?.get(
            ListContentsSelectSpinnerViewProducer.ListContentsEditKey.initMark.name)
            ?: String()
        val initValue = elcbMap?.get(
            ListContentsSelectSpinnerViewProducer.ListContentsEditKey.initValue.name)
            ?: String()
        setInitMarkToListContents(
            elcbMap,
//            currentAppDirPath,
        )

        val fileObj = File(listContentsFilePath)
        val parentDir = fileObj.parent ?: String()
        FileSystems.createDirs(parentDir)
        val editableSpinnerList = ReadText(
            listContentsFilePath
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
                listContentsFilePath
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
                    listContentsFilePath
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
                    listContentsFilePath,
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
                val isInit = selectedItem == initMark
                when(isInit){
                    true -> {
                        insertEditText.setText(initValue)
                        return
                    }
                    else -> insertEditText.setText(selectedItem)
                }
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


package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib

import android.content.Context
import android.view.View
import android.widget.*
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.common.variable.edit.SetVariableTypeColumn
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.EditTextSupportViewId
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.SelectJsExecutor
import com.puutaro.commandclick.proccess.edit.lib.ReplaceVariableMapReflecter
import com.puutaro.commandclick.proccess.edit.lib.SpinnerInstance
import com.puutaro.commandclick.util.*
import java.io.File

object ListContentsSelectSpinnerViewProducer {
    fun make (
        insertEditText: EditText,
        editParameters: EditParameters,
        currentComponentIndex: Int,
        weight: Float,
    ): Spinner {
        val currentFragment = editParameters.currentFragment
        val defaultListLimit = 100
        val context = editParameters.context
        val currentId = editParameters.currentId
        val readSharePreffernceMap = editParameters.readSharePreffernceMap
        val currentAppDirPath = SharePreffrenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_app_dir
        )
        val scriptName = SharePreffrenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_script_file_name
        )
        val linearParamsForSpinner = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT,
        )
        linearParamsForSpinner.weight = weight

        val elcbMap = getElsbMap(
            editParameters,
            currentComponentIndex
        )
        val listContentsFilePath = getListPath(
            elcbMap,
        )
        val listLimit = getLimitNum(
            elcbMap,
            defaultListLimit,
        )

        val selectJsPath = getSelectJsPath(
            elcbMap,
            currentAppDirPath,
            scriptName
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
        insertSpinner.setOnTouchListener(View.OnTouchListener {
                v, event ->
            val currentEditableSpinnerList = ReadText(
                parentDir,
                listFileName
            ).textToList().filter {
                it.trim().isNotEmpty()
            }
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

    fun getListPath(
        elcbMap: Map<String, String>?,
    ): String {
        return elcbMap?.get(ListContentsEditKey.listPath.name)
            ?.let {
                if(
                    it.isEmpty()
                ) return@let String()
                it
            } ?: String()
    }

    fun getInitMarkPath(
        elcbMap: Map<String, String>?,
    ): String? {
        return elcbMap?.get(ListContentsEditKey.initMark.name)
            ?.let {
                if(
                    it.isEmpty()
                ) return@let null
                it
            }
    }

    fun getInitValuePath(
        elcbMap: Map<String, String>?,
    ): String {
        return elcbMap?.get(ListContentsEditKey.initValue.name)
            ?.let {
                if(
                    it.isEmpty()
                ) return@let String()
                it
            }?: String()
    }

    fun getLimitNum(
        elcbMap: Map<String, String>?,
        defaultListLimit: Int,
    ): Int {
        return try {
            elcbMap
                ?.get(ListContentsEditKey.limitNum.name)
                ?.toInt()
        } catch (e: Exception){
            defaultListLimit
        } ?: defaultListLimit
    }

    fun getSelectJsPath(
        elcbMap: Map<String, String>?,
        currentAppDirPath: String,
        scriptName: String,
    ): String {
        return elcbMap?.get(ListContentsEditKey.selectJsPath.name)
            ?.let {
                if(
                    it.isEmpty()
                ) return@let String()
                ScriptPreWordReplacer.replace(
                    it,
                    currentAppDirPath,
                    scriptName
                )
            } ?: String()
    }

    fun getElsbMap(
        editParameters: EditParameters,
        currentComponentIndex: Int,
    ): Map<String, String>? {
        val currentSetVariableMap = editParameters.setVariableMap
        val currentAppDirPath = SharePreffrenceMethod.getReadSharePreffernceMap(
            editParameters.readSharePreffernceMap,
            SharePrefferenceSetting.current_app_dir
        )
        val currentScriptName = SharePreffrenceMethod.getReadSharePreffernceMap(
            editParameters.readSharePreffernceMap,
            SharePrefferenceSetting.current_script_file_name
        )
        return currentSetVariableMap?.get(
            SetVariableTypeColumn.VARIABLE_TYPE_VALUE.name
        )?.let {
            ScriptPreWordReplacer.replace(
                it,
                currentAppDirPath,
                currentScriptName
            )
        }.let {
            ReplaceVariableMapReflecter.reflect(
                QuoteTool.trimBothEdgeQuote(it),
                editParameters
            )
        }?.split('|')
            ?.getOrNull(currentComponentIndex)
            ?.split('!')?.map {
                CcScript.makeKeyValuePairFromSeparatedString(
                    it,
                "="
                )
            }?.toMap()
    }

    enum class ListContentsEditKey {
        listPath,
        limitNum,
        selectJsPath,
        initMark,
        initValue
    }

    fun setInitMarkToListContents(
        elcbMap: Map<String, String>?,
        currentAppDirPath: String,
    ){
        val listContentsFilePath = getListPath(
            elcbMap
        )
        if(
            listContentsFilePath.isEmpty()
        ) return
        val deleteStr = getInitMarkPath(elcbMap)
        if(
            deleteStr.isNullOrEmpty()
        ) return
        if(
            !listContentsFilePath.startsWith(currentAppDirPath)
            || listContentsFilePath == currentAppDirPath
        ) return
        val listContentsFilePathObj = File(listContentsFilePath)
        val listContentsParentDirPath = listContentsFilePathObj.parent
            ?: return
        val listContentsFileName = listContentsFilePathObj.name
        val listContentsList = ReadText(
            listContentsParentDirPath,
            listContentsFileName
        ).textToList()
        if(
            listContentsList.contains(deleteStr)
        ) return
        val insertListConList = listOf(deleteStr) + listContentsList.filter {
            it != deleteStr
        }
        FileSystems.writeFile(
            listContentsParentDirPath,
            listContentsFileName,
            insertListConList.joinToString("\n")
        )
    }
}


package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib

import android.content.Context
import android.view.View
import android.widget.*
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.common.variable.edit.SetVariableTypeColumn
import com.puutaro.commandclick.common.variable.settings.EditSettings
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.EditTextSupportViewId
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.SelectJsExecutor
import com.puutaro.commandclick.proccess.edit.lib.ListContentsSelectBoxTool
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.edit.lib.SpinnerInstance
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.str.QuoteTool
import com.puutaro.commandclick.util.str.ScriptPreWordReplacer
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
        val fannelInfoMap = editParameters.fannelInfoMap
        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
            fannelInfoMap
        )
        val scriptName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
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
        when(currentFragment){
            is EditFragment -> currentFragment.listConSelectBoxMapList.add(elcbMap)
        }
        val listContentsFilePath = elcbMap?.get(ListContentsEditKey.listPath.name) ?: String()
        val listLimit = getLimitNum(
            elcbMap,
            defaultListLimit,
        )
        execCompList(
            editParameters,
            elcbMap,
            listContentsFilePath,
        )

        val selectJsPath = getSelectJsPath(
            elcbMap,
            currentAppDirPath,
            scriptName
        )

        val fileObj = File(listContentsFilePath)
        val parentDir = fileObj.parent ?: String()
        FileSystems.createDirs(parentDir)
        val editableSpinnerList = ReadText(
            listContentsFilePath
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
                listContentsFilePath
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
                    listContentsFilePath
                ).textToList().filter {
                    it.trim().isNotEmpty()
                }
                val updateListContents = listOf(selectedItem) + currentSpinnerList.filter {
                        it != selectedItem
                    }
                FileSystems.writeFile(
                    listContentsFilePath,
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

    fun execCompList(
        editParameters: EditParameters,
        elcbMap: Map<String, String>?,
        targetListFilePath: String?,
    ){
        if(
            targetListFilePath.isNullOrEmpty()
        ) return
        val filePrefix = EditSettings.filePrefix
        val separator = "&"
        val compListOneLineCon =
            elcbMap?.get(ListContentsEditKey.compList.name)
        if(
            compListOneLineCon.isNullOrEmpty()
        ) return
        val itemTextListCon = when(compListOneLineCon.startsWith(filePrefix)){
            true -> {
                val fannelInfoMap = editParameters.fannelInfoMap
                val setReplaceVariableMap = editParameters.setReplaceVariableMap
                val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
                    fannelInfoMap
                )
                val currentFannelName = FannelInfoTool.getCurrentFannelName(
                    fannelInfoMap
                )
                val compListFilePathObj = File(compListOneLineCon.removePrefix(filePrefix))
                ReadText(
                    compListFilePathObj.absolutePath
                ).readText().let {
                    SetReplaceVariabler.execReplaceByReplaceVariables(
                        it,
                        setReplaceVariableMap,
                        currentAppDirPath,
                        currentFannelName
                    )
                }
            }
            else -> compListOneLineCon.replace(
                separator,
                "\n"
            )
        }
        ListContentsSelectBoxTool.compListFile(
            targetListFilePath,
            itemTextListCon
        )
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
        return currentSetVariableMap?.get(
            SetVariableTypeColumn.VARIABLE_TYPE_VALUE.name
        )
            ?.let {
                QuoteTool.splitBySurroundedIgnore(
                    it,
                    '|'
                )
            }
            ?.getOrNull(currentComponentIndex)
            ?.let {
                CmdClickMap.createMap(
                    it,
                    '?'
                )
            }?.toMap()
    }

    enum class ListContentsEditKey {
        listPath,
        limitNum,
        selectJsPath,
        initMark,
        initValue,
        saveTags,
        saveFilterShellPath,
        saveValName,
        compList,
    }

    fun setInitMarkToListContents(
        elcbMap: Map<String, String>?,
        currentAppDirPath: String,
    ){
        val listContentsFilePath = elcbMap?.get(ListContentsEditKey.listPath.name)
        if(
            listContentsFilePath.isNullOrEmpty()
        ) return
        val deleteStr = elcbMap.get(ListContentsEditKey.initMark.name)
        if(
            deleteStr.isNullOrEmpty()
        ) return
        if(
            !listContentsFilePath.startsWith(currentAppDirPath)
            || listContentsFilePath == currentAppDirPath
        ) return
        val listContentsList = ReadText(
            listContentsFilePath
        ).textToList()
        if(
            listContentsList.contains(deleteStr)
        ) return
        val insertListConList = listOf(deleteStr) + listContentsList.filter {
            it != deleteStr
        }
        FileSystems.writeFile(
            listContentsFilePath,
            insertListConList.joinToString("\n")
        )
    }
}


package com.puutaro.commandclick.proccess.edit.lib

import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.DragSortListViewProducer
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.ListContentsSelectSpinnerViewProducer
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.state.SharePrefTool

object ListPathGetterForDragSort {

    fun get(
        elsbMap: Map<String, String>?,
        editFragment: EditFragment,
        currentVariableName: String,
    ): String {
        val listPathSrc = elsbMap?.get(
            ListContentsSelectSpinnerViewProducer.ListContentsEditKey.listPath.name
        )
        return getByListPathSrc(
            listPathSrc,
            editFragment,
            currentVariableName
        )
    }

    fun getByListPathSrc(
        listPathSrc: String?,
        editFragment: EditFragment,
        currentVariableName: String,
    ): String {
        if(
            listPathSrc.isNullOrEmpty()
        ) return String()
        val readSharePreffernceMap = editFragment.readSharePreferenceMap
        val currentScriptContentsList = editFragment.currentFannelConList
        val currentAppDirPath = SharePrefTool.getCurrentAppDirPath(
            readSharePreffernceMap
        )
        val currentFannelName = SharePrefTool.getCurrentFannelName(
            readSharePreffernceMap
        )
        val isMacroStrForGetFromVar =
            listPathSrc.trim().startsWith(DragSortListViewProducer.macroStrForDragSortGetListPathFromVar)
        return when(isMacroStrForGetFromVar){
            true -> getListPathFromVar(
                currentScriptContentsList,
                currentVariableName,
                listPathSrc
            )
            else -> listPathSrc
        }.let {
            SetReplaceVariabler.execReplaceByReplaceVariables(
                it,
                editFragment.setReplaceVariableMap,
                currentAppDirPath,
                currentFannelName,
            )
        }
    }

    private fun getListPathFromVar(
        currentScriptContentsList: List<String>,
        currentVariableName: String,
        macroValue: String,
    ): String {
        val macroSeparator = ":"
        val listPath = CommandClickVariables.substituteCmdClickVariable(
            currentScriptContentsList,
            currentVariableName
        )
        return when(listPath.isNullOrEmpty()){
            true -> {
                val defaultValueList = macroValue.split(macroSeparator)
                if(defaultValueList.size != 2) String()
                else defaultValueList.last()
            }
            else -> listPath
        }
    }
}
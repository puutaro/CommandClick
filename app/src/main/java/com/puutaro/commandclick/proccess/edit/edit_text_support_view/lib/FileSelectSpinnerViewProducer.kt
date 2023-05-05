package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib

import android.content.Context
import android.view.View
import android.widget.*
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.common.variable.edit.SetVariableTypeColumn
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.EditTextSupportViewId
import com.puutaro.commandclick.proccess.edit.lib.ReplaceVariableMapReflecter
import com.puutaro.commandclick.proccess.edit.lib.SpinnerInstance
import com.puutaro.commandclick.util.BothEdgeQuote
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.ScriptPreWordReplacer
import com.puutaro.commandclick.util.SharePreffrenceMethod


object FileSelectSpinnerViewProducer {

    val noExtend = "NoExtend"

    fun make (
        insertEditText: EditText,
        editParameters: EditParameters,
        weight: Float,
    ): Spinner {
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
        val fannelDirName = currentScriptName
            .removeSuffix(CommandClickScriptVariable.JS_FILE_SUFFIX)
            .removeSuffix(CommandClickScriptVariable.SHELL_FILE_SUFFIX) +
                "Dir"
        val throughMark = "-"
        val linearParamsForSpinner = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT,
        )
        linearParamsForSpinner.weight = weight
        val adapter = ArrayAdapter<String>(
            context as Context,
            R.layout.sppinner_layout,
        )
        val dirPrefixSuffixList = currentSetVariableMap?.get(
            SetVariableTypeColumn.VARIABLE_TYPE_VALUE.name
        )?.split('|')
            ?.firstOrNull()
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
            }?.split('!')
            ?: emptyList()
        val filterDirSource = dirPrefixSuffixList
            .firstOrNull()
            ?: String()
        val filterDir = if(
            filterDirSource.isEmpty()
        ) currentAppDirPath
        else filterDirSource
        val filterPrefix = dirPrefixSuffixList
            .getOrNull(1)?.let {
            BothEdgeQuote
                .trim(it)
        } ?: String()
        val filterSuffix = dirPrefixSuffixList
            .getOrNull(2)?.let {
                BothEdgeQuote
                    .trim(it)
            } ?: String()
        val editableSpinnerList = makeSpinnerList(
            filterDir,
            filterPrefix,
            filterSuffix,
        )
        val updatedEditableSpinnerList = listOf(throughMark) + editableSpinnerList
        adapter.addAll(updatedEditableSpinnerList)

        val insertSpinner = SpinnerInstance.make(
            context,
            updatedEditableSpinnerList,
            editParameters.onFixNormalSpinner
        )
        insertSpinner.id = currentId + EditTextSupportViewId.EDITABLE_SPINNER.id
        insertSpinner.tag = "spinnerEdit${currentId + EditTextSupportViewId.EDITABLE_SPINNER.id}"
        insertSpinner.adapter = adapter
        insertSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                val selectedItem = adapter.getItem(pos)
                    ?: return
                if(
                    selectedItem != throughMark
                    && currentAppDirPath != UsePath.cmdclickConfigDirPath
                    && currentAppDirPath != UsePath.cmdclickAppHistoryDirAdminPath
                ) {
                    FileSystems.updateLastModified(
                        currentAppDirPath,
                        selectedItem
                    )
                }
                val currentSpinnerList = makeSpinnerList(
                    filterDir,
                    filterPrefix,
                    filterSuffix,
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
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        insertSpinner.layoutParams = linearParamsForSpinner
        return insertSpinner
    }

    private fun judgeBySuffix(
        targetStr: String,
        filterSuffix: String,
    ): Boolean{
        if(filterSuffix != noExtend) {
            return targetStr.endsWith(filterSuffix)
        }
        return !Regex("\\..*$").containsMatchIn(targetStr)
    }

    private fun makeSpinnerList(
        filterDir: String,
        filterPrefix: String,
        filterSuffix: String,
    ): List<String> {
        return FileSystems.sortedFiles(
            filterDir,
            "on"
        ).filter {
            it.startsWith(filterPrefix)
                    && judgeBySuffix(it, filterSuffix)
        }
    }
}

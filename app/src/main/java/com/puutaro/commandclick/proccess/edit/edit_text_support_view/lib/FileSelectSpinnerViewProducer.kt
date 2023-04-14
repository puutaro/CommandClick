package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib

import android.content.Context
import android.view.View
import android.widget.*
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.edit.SetVariableTypeColumn
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.EditTextSupportViewId
import com.puutaro.commandclick.util.BothEdgeQuote
import com.puutaro.commandclick.util.FileSystems


object FileSelectSpinnerViewProducer {

    fun make (
        context: Context?,
        currentId: Int,
        insertEditText: EditText,
        currentRecordNumToSetVariableMap: Map<String,String>,
        currentAppDirPath: String,
        weight: Float,
    ): Spinner {
        val throughMark = "-"
        val linearParamsForSpinner = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT,
        )
        linearParamsForSpinner.weight = weight
        val insertSpinner = Spinner(context)
        insertSpinner.id = currentId + EditTextSupportViewId.EDITABLE_SPINNER.id
        insertSpinner.tag = "spinnerEdit${currentId + EditTextSupportViewId.EDITABLE_SPINNER.id}"
        val adapter = ArrayAdapter<String>(
            context as Context,
            R.layout.sppinner_layout,
        )
        val prefixSuffixList = currentRecordNumToSetVariableMap.get(
            SetVariableTypeColumn.VARIABLE_TYPE_VALUE.name
        )?.split('|')
            ?.firstOrNull()
            ?.replace("\${01}", currentAppDirPath)
            ?.split('&')
            ?: emptyList()
        val filterDir = prefixSuffixList
            .firstOrNull()?.let {
                BothEdgeQuote
                    .trim(it)
            } ?: String()
        val filterPrefix = prefixSuffixList
            .getOrNull(1)?.let {
            BothEdgeQuote
                .trim(it)
        } ?: String()
        val filterSuffix = prefixSuffixList
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
        insertSpinner.adapter = adapter
        insertSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                val selectedItem = adapter.getItem(pos)
                    ?: return
                if(selectedItem != throughMark) {
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
        val noExtend = "NoExtend"
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
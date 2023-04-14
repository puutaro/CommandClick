package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib

import android.content.Context
import android.view.View
import android.widget.*
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.edit.SetVariableTypeColumn
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.EditTextSupportViewId

object EditableSpinnerViewProducer {
    fun make (
        context: Context?,
        currentId: Int,
        insertEditText: EditText,
        currentRecordNumToSetVariableMap: Map<String,String>,
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
        val editableSpinnerList = currentRecordNumToSetVariableMap.get(
            SetVariableTypeColumn.VARIABLE_TYPE_VALUE.name
        )
            ?.split('|')
            ?.firstOrNull()
            ?.split('!')
            ?: listOf()
        val updatedEditableSpinnerList = listOf(throughMark) + editableSpinnerList
        adapter.addAll(updatedEditableSpinnerList)
        insertSpinner.adapter = adapter
        insertSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                val selectedItem = adapter.getItem(pos)
                val currentSpinnerList = (0 until adapter.count).map {
                    adapter.getItem(it)
                }
                val selectUpdatedSpinnerList = if(
                    selectedItem == throughMark
                ){
                    listOf(throughMark) + currentSpinnerList.filter {
                        it != selectedItem
                    }
                } else listOf(
                    throughMark,
                    selectedItem,
                ) + currentSpinnerList.filter {
                    it != throughMark
                            && it != selectedItem
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

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        insertSpinner.layoutParams = linearParamsForSpinner
        return insertSpinner
    }
}
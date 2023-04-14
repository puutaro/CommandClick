package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.*
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.edit.SetVariableTypeColumn
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.EditTextSupportViewId


class SpinnerViewProducer {
    companion object {
        fun make(
            context: Context?,
            currentId: Int,
            insertEditText: EditText,
            currentRecordNumToSetVariableMap: Map<String,String>,
            weight: Float,
        ): Spinner {
            val linearParamsForSpinner = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
            )
            linearParamsForSpinner.weight = weight
            val insertSpinner = Spinner(context)
            insertSpinner.id = currentId + EditTextSupportViewId.SPINNER.id
            insertSpinner.tag = "spinner${currentId + EditTextSupportViewId.SPINNER.id}"
            val adapter = ArrayAdapter<String>(
                context as Context,
                R.layout.sppinner_layout,
            )
            val sppinerList = currentRecordNumToSetVariableMap.get(
                SetVariableTypeColumn.VARIABLE_TYPE_VALUE.name
            )
                ?.split('|')
                ?.firstOrNull()
                ?.split('!')
                ?: listOf()
            val currentExistItem = insertEditText.text.toString()
            val updatedSppinerList = if(
                sppinerList.contains(
                    insertEditText.text.toString()
                )
            ){
                listOf(
                    currentExistItem,
                ) + sppinerList.filter { it != currentExistItem }
            } else {
                sppinerList
            }
            adapter.addAll(updatedSppinerList)
            insertSpinner.adapter = adapter
            insertSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    pos: Int,
                    id: Long
                ) {
                    val selectedItem = adapter.getItem(pos)
                    val currentSpinnerList = (0 until adapter.count).map {
                        adapter.getItem(it)
                    }
                    insertEditText.setText(selectedItem)
                    val selectUpdatedSpinnerList = listOf(
                        selectedItem,
                    ) + currentSpinnerList.filter { it != selectedItem }
                    adapter.clear()
                    adapter.addAll(selectUpdatedSpinnerList)
                    adapter.notifyDataSetChanged()
                    insertSpinner.setSelection(0)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
            insertSpinner.layoutParams = linearParamsForSpinner
            return insertSpinner
        }
    }
}
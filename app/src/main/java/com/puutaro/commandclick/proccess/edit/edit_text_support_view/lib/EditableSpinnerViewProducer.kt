package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib

import android.content.Context
import android.view.View
import android.widget.*
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.EditTextSupportViewId

object EditableSpinnerViewProducer {
//    fun make (
//        fragment: Fragment,
//        insertEditText: EditText,
//        editParameters: EditParameters,
//        currentComponentIndex: Int,
//        weight: Float,
//    ): Spinner {
//        val context = fragment.context
//        val currentId = editParameters.currentId
//        val currentSetVariableMap = editParameters.setVariableMap
//        val throughMark = "-"
//        val linearParamsForSpinner = LinearLayoutCompat.LayoutParams(
//            0,
//            LinearLayoutCompat.LayoutParams.MATCH_PARENT,
//        )
//        linearParamsForSpinner.weight = weight
//        val adapter = ArrayAdapter<String>(
//            context as Context,
//            R.layout.sppinner_layout,
//        )
//        val editableSpinnerList = currentSetVariableMap?.get(
//            SetVariableTypeColumn.VARIABLE_TYPE_VALUE.name
//        )
//            ?.split('|')
//            ?.getOrNull(currentComponentIndex)
////            .let {
////                ReplaceVariableMapReflecter.reflect(
////                    QuoteTool.trimBothEdgeQuote(it),
////                    editParameters
////                )
////            }
//            ?.split('?')
//            ?: listOf()
//
//        val updatedEditableSpinnerList = listOf(throughMark) + editableSpinnerList
//        adapter.addAll(updatedEditableSpinnerList)
//
//        val insertSpinner = SpinnerInstance.make(
//            context,
//            updatedEditableSpinnerList,
//            editParameters.onFixNormalSpinner
//        )
//
//        insertSpinner.id = currentId + EditTextSupportViewId.EDITABLE_SPINNER.id
//        insertSpinner.tag = "spinnerEdit${currentId + EditTextSupportViewId.EDITABLE_SPINNER.id}"
//        insertSpinner.adapter = adapter
//        insertSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
//                val selectedItem = adapter.getItem(pos)
//                val currentSpinnerList = (0 until adapter.count).map {
//                    adapter.getItem(it)
//                }
//                val selectUpdatedSpinnerList = if(
//                    selectedItem == throughMark
//                ){
//                    listOf(throughMark) + currentSpinnerList.filter {
//                        it != selectedItem
//                    }
//                } else listOf(
//                    throughMark,
//                    selectedItem,
//                ) + currentSpinnerList.filter {
//                    it != throughMark
//                            && it != selectedItem
//                }
//                adapter.clear()
//                adapter.addAll(selectUpdatedSpinnerList)
//                adapter.notifyDataSetChanged()
//                insertSpinner.setSelection(0)
//                if(
//                    selectedItem == throughMark
//                ) return
//                insertEditText.setText(selectedItem)
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//            }
//        }
//        insertSpinner.layoutParams = linearParamsForSpinner
//        return insertSpinner
//    }
}
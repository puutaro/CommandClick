package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib

import android.content.Context
import android.view.View
import android.widget.*
import androidx.appcompat.widget.LinearLayoutCompat
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.EditTextSupportViewId


object SpinnerViewProducer {
//    fun make(
//        context: Context?,
//        insertEditText: EditText,
//        editParameters: EditParameters,
//        currentComponentIndex: Int,
//        weight: Float,
//    ): Spinner {
//        val currentId = editParameters.currentId
//        val currentSetVariableMap = editParameters.setVariableMap
//        val linearParamsForSpinner = LinearLayoutCompat.LayoutParams(
//            0,
//            LinearLayoutCompat.LayoutParams.MATCH_PARENT,
//        )
//        linearParamsForSpinner.weight = weight
//        val adapter = ArrayAdapter<String>(
//            context as Context,
//            R.layout.sppinner_layout,
//        )
//        val sppinerList =
//            currentSetVariableMap?.get(
//                SetVariableTypeColumn.VARIABLE_TYPE_VALUE.name
//            )
//                ?.split('|')
//                ?.getOrNull(currentComponentIndex)
////                    .let {
////                        ReplaceVariableMapReflecter.reflect(
////                            QuoteTool.trimBothEdgeQuote(it),
////                            editParameters
////                        )
////                    }
//                ?.split('?')
//                ?: listOf()
//        val currentExistItem = insertEditText.text.toString()
//        val updatedSppinerList = if(
//            sppinerList.contains(
//                insertEditText.text.toString()
//            )
//        ){
//            listOf(
//                currentExistItem,
//            ) + sppinerList.filter { it != currentExistItem }
//        } else {
//            sppinerList
//        }
//        adapter.addAll(updatedSppinerList)
//
//        val insertSpinner = SpinnerInstance.make(
//            context,
//            updatedSppinerList,
//            editParameters.onFixNormalSpinner
//        )
//        insertSpinner.id = currentId + EditTextSupportViewId.SPINNER.id
//        insertSpinner.tag = "spinner${currentId + EditTextSupportViewId.SPINNER.id}"
//        insertSpinner.adapter = adapter
//        insertSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>?,
//                view: View?,
//                pos: Int,
//                id: Long
//            ) {
//                val selectedItem = adapter.getItem(pos)
//                val currentSpinnerList = (0 until adapter.count).map {
//                    adapter.getItem(it)
//                }
//                insertEditText.setText(selectedItem)
//                val selectUpdatedSpinnerList = listOf(
//                    selectedItem,
//                ) + currentSpinnerList.filter { it != selectedItem }
//                adapter.clear()
//                adapter.addAll(selectUpdatedSpinnerList)
//                adapter.notifyDataSetChanged()
//                insertSpinner.setSelection(0)
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//            }
//        }
//        insertSpinner.layoutParams = linearParamsForSpinner
//        return insertSpinner
//    }
}
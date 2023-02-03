package com.puutaro.commandclick.fragment_lib.edit_fragment.processor.edit_text_support_view

import android.content.Context
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.puutaro.commandclick.R
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.EditTextSupportViewId
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.SetVariableTypeColumn

class WithSpinnerView(
    private val context: Context?,
) {
    fun create(
        currentId: Int,
        currentVariableValue: String?,
        insertEditText: EditText,
        currentRecordNumToSetVariableMap: Map<String,String>
    ): LinearLayout {
        val horozontalLinearLayout = LinearLayout(context)
        horozontalLinearLayout.orientation = LinearLayout.HORIZONTAL
        val linearParamsForEditTextTest = LinearLayout.LayoutParams(
            0,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )
        insertEditText.inputType = InputType.TYPE_CLASS_TEXT
        insertEditText.setText(currentVariableValue)
        linearParamsForEditTextTest.weight = 0.001F
        insertEditText.layoutParams = linearParamsForEditTextTest
        horozontalLinearLayout.addView(insertEditText)
        val linearParamsForSpinner = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT,
        )
        linearParamsForSpinner.weight = 2F
        val insertSpinner = Spinner(context)
        insertSpinner.id = currentId + EditTextSupportViewId.SPINNER.id
        insertSpinner.tag = "spinner${currentId + EditTextSupportViewId.SPINNER.id}"
        val adapter = ArrayAdapter<String>(
            context as Context,
            R.layout.sppinner_layout,
        )
        val sppinerList = currentRecordNumToSetVariableMap.get(
            SetVariableTypeColumn.VARIABLE_TYPE_VALUE.name
        )?.split('!') ?: listOf()
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
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                Log.e("onItemSelected", "parent: $parent, view: $view, pos: $pos, id: $id")
                val selectedItem = updatedSppinerList[pos]
                insertEditText.setText(selectedItem)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        insertSpinner.layoutParams = linearParamsForSpinner
        horozontalLinearLayout.addView(insertSpinner)
        return horozontalLinearLayout
    }


}
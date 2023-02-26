package com.puutaro.commandclick.fragment_lib.edit_fragment.processor.edit_text_support_view

import android.content.Context
import android.text.InputType
import android.view.ViewGroup
import android.widget.*
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.edit_text_support_view.lib.SpinnerViewProducer

class WithSpinnerView(
    private val context: Context?,
) {
    fun create(
        currentId: Int,
        currentVariableValue: String?,
        insertEditText: EditText,
        currentRecordNumToSetVariableMap: Map<String,String>
    ): LinearLayout {
        val horizontalLinearLayout = LinearLayout(context)
        horizontalLinearLayout.orientation = LinearLayout.HORIZONTAL
        val linearParamsForEditTextTest = LinearLayout.LayoutParams(
            0,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )
        insertEditText.inputType = InputType.TYPE_CLASS_TEXT
        insertEditText.setText(currentVariableValue)
        linearParamsForEditTextTest.weight = 0.001F
        insertEditText.layoutParams = linearParamsForEditTextTest
        horizontalLinearLayout.addView(insertEditText)
        val insertSpinner = SpinnerViewProducer.make(
            context,
            currentId,
            insertEditText,
            currentRecordNumToSetVariableMap,
            2F,
        )
        horizontalLinearLayout.addView(insertSpinner)
        return horizontalLinearLayout
    }
}


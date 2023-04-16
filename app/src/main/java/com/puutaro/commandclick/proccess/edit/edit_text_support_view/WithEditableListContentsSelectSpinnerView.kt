package com.puutaro.commandclick.proccess.edit.edit_text_support_view

import android.text.InputType
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.EditableListContentsSelectSpinnerViewProducer

class WithEditableListContentsSelectSpinnerView {
    fun create(
        insertEditText: EditText,
        editParameters: EditParameters
    ): LinearLayout {
        val context = editParameters.context
        val currentVariableValue = editParameters.currentVariableValue
        val horizontalLinearLayout = LinearLayout(context)
        horizontalLinearLayout.orientation = LinearLayout.HORIZONTAL
        val linearParamsForEditTextTest = LinearLayout.LayoutParams(
            0,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )
        insertEditText.inputType = InputType.TYPE_CLASS_TEXT
        insertEditText.setText(currentVariableValue)
        linearParamsForEditTextTest.weight = 0.6F
        insertEditText.layoutParams = linearParamsForEditTextTest
        horizontalLinearLayout.addView(insertEditText)
        val insertSpinner = EditableListContentsSelectSpinnerViewProducer.make(
            insertEditText,
            editParameters,
            0.4F,
        )
        horizontalLinearLayout.addView(insertSpinner)
        return horizontalLinearLayout
    }
}

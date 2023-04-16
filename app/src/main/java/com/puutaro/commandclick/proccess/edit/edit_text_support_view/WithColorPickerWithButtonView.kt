package com.puutaro.commandclick.proccess.edit.edit_text_support_view

import android.text.InputType
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.ButtonViewProducer
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.ColorPickerViewProducer

class WithColorPickerWithButtonView(
    private val editFragment: EditFragment,
) {

    fun create(
        insertTextView: TextView,
        insertEditText: EditText,
        editParameters: EditParameters
    ): LinearLayout {
        val context = editParameters.context
        val currentVariableValue = editParameters.currentVariableValue
        val horizontalLinearLayout = LinearLayout(context)
        horizontalLinearLayout.orientation = LinearLayout.HORIZONTAL
        insertEditText.inputType = InputType.TYPE_CLASS_TEXT
        insertEditText.setText(currentVariableValue)
        val linearParamsForEditTextTest = LinearLayout.LayoutParams(
            0,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )
        linearParamsForEditTextTest.weight = 0.6F
        insertEditText.layoutParams = linearParamsForEditTextTest
        insertEditText.isFocusableInTouchMode = true;
        horizontalLinearLayout.addView(insertEditText)

        val colorPickerButtonView = ColorPickerViewProducer.make(
            editFragment,
            insertEditText,
            0.2F,
        )
        horizontalLinearLayout.addView(colorPickerButtonView)

        val insertButton = ButtonViewProducer.make(
            editFragment,
            insertTextView,
            insertEditText,
            editParameters,
            0.2F,
            true
        )
        horizontalLinearLayout.addView(insertButton)

        return horizontalLinearLayout
    }
}
package com.puutaro.commandclick.proccess.edit.edit_text_support_view

import android.text.InputType
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.ColorPickerViewProducer


class WithColorPickerView(
    private val editFragment: EditFragment,
) {
    fun create(
        insertEditText: EditText,
        currentVariableValue: String?,
    ): LinearLayout {
        val context = editFragment.context
        val horizontalLinearLayout = LinearLayout(context)
        horizontalLinearLayout.orientation = LinearLayout.HORIZONTAL
        insertEditText.inputType = InputType.TYPE_CLASS_TEXT
        insertEditText.setText(currentVariableValue)
        val linearParamsForEditTextTest = LinearLayout.LayoutParams(
            0,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )
        linearParamsForEditTextTest.weight = 0.8F
        insertEditText.layoutParams = linearParamsForEditTextTest
        insertEditText.isFocusableInTouchMode = true;
        horizontalLinearLayout.addView(insertEditText)

        val colorPickerButtonView = ColorPickerViewProducer.make(
            editFragment,
            insertEditText,
            0.2F,
        )
        horizontalLinearLayout.addView(colorPickerButtonView)

        return horizontalLinearLayout
    }
}


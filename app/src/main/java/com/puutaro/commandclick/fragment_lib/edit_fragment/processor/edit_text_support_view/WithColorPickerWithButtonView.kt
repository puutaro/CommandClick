package com.puutaro.commandclick.fragment_lib.edit_fragment.processor.edit_text_support_view

import android.text.InputType
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.edit_text_support_view.lib.ButtonViewProducer
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.edit_text_support_view.lib.ColorPickerViewProducer

class WithColorPickerWithButtonView(
    private val editFragment: EditFragment,
    private val readSharePreffernceMap: Map<String, String>,
    private val currentShellContentsList: List<String>,
    private val recordNumToMapNameValueInCommandHolder: Map<Int, Map<String, String>?>? = null,
) {
    fun create(
        currentId: Int,
        currentVariableValue: String?,
        insertTextView: TextView,
        insertEditText: EditText,
        currentRecordNumToSetVariableMap: Map<String,String>,
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
            readSharePreffernceMap,
            currentId,
            insertTextView,
            insertEditText,
            currentRecordNumToSetVariableMap,
            0.2F,
            currentShellContentsList,
            recordNumToMapNameValueInCommandHolder,
            true
        )
        horizontalLinearLayout.addView(insertButton)

        return horizontalLinearLayout
    }
}
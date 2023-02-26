package com.puutaro.commandclick.fragment_lib.edit_fragment.processor.edit_text_support_view

import android.text.InputType
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.edit_text_support_view.lib.DirOrFileChooseProducer


class WithDirOrFileChooseView(
    private val editFragment: EditFragment,
) {

    private val context = editFragment.context

    fun create(
        insertEditText: EditText,
        currentVariableValue: String?,
        onDirectoryPick: Boolean = true
    ): LinearLayout {
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
        val insertChooseButtonView = DirOrFileChooseProducer.make(
            editFragment,
            onDirectoryPick,
            insertEditText,
            0.2F,
        )
        horizontalLinearLayout.addView(insertChooseButtonView)
        return horizontalLinearLayout
    }
}
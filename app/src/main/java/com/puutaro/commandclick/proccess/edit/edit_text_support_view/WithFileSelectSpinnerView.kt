package com.puutaro.commandclick.proccess.edit.edit_text_support_view

import android.content.Context
import android.text.InputType
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.FileSelectSpinnerViewProducer


class WithFileSelectEditableSpinnerView(
    private val context: Context?
) {
    fun create(
        currentId: Int,
        currentVariableValue: String?,
        insertEditText: EditText,
        currentRecordNumToSetVariableMap: Map<String,String>,
        setReplaceVariableMap: Map<String, String>?,
        currentAppDirPath: String,
    ): LinearLayout {
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
        val insertSpinner = FileSelectSpinnerViewProducer.make(
            context,
            currentId,
            insertEditText,
            currentRecordNumToSetVariableMap,
            setReplaceVariableMap,
            currentAppDirPath,
            0.4F,
        )
        horizontalLinearLayout.addView(insertSpinner)
        return horizontalLinearLayout
    }
}
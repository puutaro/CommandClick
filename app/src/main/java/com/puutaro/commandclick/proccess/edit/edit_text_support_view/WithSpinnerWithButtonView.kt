package com.puutaro.commandclick.proccess.edit.edit_text_support_view

import android.graphics.Color
import android.text.InputType
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.ButtonViewProducer
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.SpinnerViewProducer
import com.puutaro.commandclick.view_model.activity.TerminalViewModel

class WithSpinnerWithButtonView(
    private val editFragment: EditFragment,
    private val currentShellContentsList: List<String>,
    private val recordNumToMapNameValueInCommandHolder: Map<Int, Map<String, String>?>? = null,
) {
    private val context = editFragment.context
    private val readSharePreffernceMap = editFragment.readSharePreffernceMap
    val terminalViewModel: TerminalViewModel by editFragment.activityViewModels()

    fun create(
        currentId: Int,
        currentVariableValue: String?,
        insertTextView: TextView,
        insertEditText: EditText,
        currentRecordNumToSetVariableMap: Map<String,String>,
    ): LinearLayout {
        val horizontalLinearLayout = LinearLayout(context)
        horizontalLinearLayout.orientation = LinearLayout.HORIZONTAL
        val linearParamsForEditTextTest = LinearLayout.LayoutParams(
            0,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )
        insertEditText.inputType = InputType.TYPE_CLASS_TEXT
        insertEditText.setText(currentVariableValue)
        insertEditText.setTextColor(Color.parseColor("#FFFFFF"))
        linearParamsForEditTextTest.weight = 0.001F
        insertEditText.layoutParams = linearParamsForEditTextTest
        horizontalLinearLayout.addView(insertEditText)
        val insertSpinner = SpinnerViewProducer.make(
            context,
            currentId,
            insertEditText,
            currentRecordNumToSetVariableMap,
        0.8F,
        )
        horizontalLinearLayout.addView(insertSpinner)
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

package com.puutaro.commandclick.proccess.edit.edit_text_support_view

import android.text.InputType
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.DirOrFileChooseProducer
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.EditableListContentsSelectGridViewProducer
import com.puutaro.commandclick.view_model.activity.TerminalViewModel

class WithEditableListContentsSelectGridWithFileChooser(
    private val editFragment: EditFragment,
) {
    val terminalViewModel: TerminalViewModel by editFragment.activityViewModels()

    fun create(
        insertEditText: EditText,
        editParameters: EditParameters
    ): LinearLayout {
        val context = editParameters.context
        val currentVariableValue = editParameters.currentVariableValue
        val onDirectoryPick = editParameters.onDirectoryPick
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
        val insertGrid = EditableListContentsSelectGridViewProducer.make(
            insertEditText,
            editParameters,
            0.2F,
        )
        horizontalLinearLayout.addView(insertGrid)
        val insertChooseButton = DirOrFileChooseProducer.make(
            editFragment,
            onDirectoryPick,
            insertEditText,
            0.2F,
        )
        horizontalLinearLayout.addView(insertChooseButton)
        return horizontalLinearLayout
    }
}

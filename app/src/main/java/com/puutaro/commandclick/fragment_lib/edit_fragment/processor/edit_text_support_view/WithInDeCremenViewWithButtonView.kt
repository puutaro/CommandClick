package com.puutaro.commandclick.fragment_lib.edit_fragment.processor.edit_text_support_view

import android.text.InputType
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.edit_text_support_view.lib.ButtonViewProducer
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.edit_text_support_view.lib.InDeCrementerViewProducer
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.edit_text_support_view.lib.numEntityMapColumn
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.SetVariableTypeColumn
import com.puutaro.commandclick.view_model.activity.TerminalViewModel

class WithInDeCremenViewWithButtonView(
    private val editFragment: EditFragment,
    private val readSharePreffernceMap: Map<String, String>,
    private val currentShellContentsList: List<String>,
    private val recordNumToMapNameValueInCommandHolder: Map<Int, Map<String, String>?>? = null,
) {
    private val context = editFragment.context
    val terminalViewModel: TerminalViewModel by editFragment.activityViewModels()

    fun create(
        currentId: Int,
        insertTextView: TextView,
        currentVariableValueSource: String?,
        insertEditText: EditText,
        setVariableMap: Map<String, String>
    ): LinearLayout {

        val linearParamsForEditTextTest = LinearLayout.LayoutParams(
            0,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
        linearParamsForEditTextTest.weight = 0.9F
        val horizontalLinearLayout = LinearLayout(context)
        horizontalLinearLayout.orientation = LinearLayout.HORIZONTAL

        val variableTypeValue = setVariableMap.get(
            SetVariableTypeColumn.VARIABLE_TYPE_VALUE.name
        )
        val numEntityMap = InDeCrementerViewProducer.makeNumEntityMap(
            variableTypeValue,
        )

        val initNumString = numEntityMap.get(
            numEntityMapColumn.INIT_NUM.name
        )
        val currentVariableValue = if (initNumString == null) {
            InDeCrementerViewProducer.stringToInt(
                currentVariableValueSource,
            ).toString()
        } else {
            initNumString
        }

        insertEditText.setText(currentVariableValue)
        insertEditText.inputType = InputType.TYPE_CLASS_NUMBER
        insertEditText.layoutParams = linearParamsForEditTextTest
        insertEditText.setFocusableInTouchMode(true);
        linearParamsForEditTextTest.weight = 0.4F
        insertEditText.layoutParams = linearParamsForEditTextTest
        horizontalLinearLayout.addView(insertEditText)

        val incButton = InDeCrementerViewProducer.make(
            editFragment,
            insertEditText,
            numEntityMap,
            0.2F,
            true,
        )
        horizontalLinearLayout.addView(incButton)
        val decButton = InDeCrementerViewProducer.make(
            editFragment,
            insertEditText,
            numEntityMap,
            0.2F,
            false
        )
        horizontalLinearLayout.addView(decButton)
        val insertButton = ButtonViewProducer.make(
            editFragment,
            readSharePreffernceMap,
            currentId,
            insertTextView,
            insertEditText,
            setVariableMap,
            0.2F,
            currentShellContentsList,
            recordNumToMapNameValueInCommandHolder,
        true
        )
        horizontalLinearLayout.addView(insertButton)
        return horizontalLinearLayout
    }
}
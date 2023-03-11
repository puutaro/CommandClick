package com.puutaro.commandclick.proccess.edit.edit_text_support_view

import android.content.Context
import android.text.InputType
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import com.puutaro.commandclick.common.variable.edit.SetVariableTypeColumn
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.InDeCrementerViewProducer
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.numEntityMapColumn

class WithInDeCremenView(
    private val context: Context?
) {

    fun create(
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
        insertEditText.isFocusableInTouchMode = true;
        linearParamsForEditTextTest.weight = 0.6F
        insertEditText.layoutParams = linearParamsForEditTextTest
        horizontalLinearLayout.addView(insertEditText)

        val incButton = InDeCrementerViewProducer.make(
            context,
            insertEditText,
            numEntityMap,
            0.2F,
            true,
        )
        horizontalLinearLayout.addView(incButton)
        val decButton = InDeCrementerViewProducer.make(
            context,
            insertEditText,
            numEntityMap,
            0.2F,
            false
        )
        horizontalLinearLayout.addView(decButton)
        return horizontalLinearLayout
    }
}
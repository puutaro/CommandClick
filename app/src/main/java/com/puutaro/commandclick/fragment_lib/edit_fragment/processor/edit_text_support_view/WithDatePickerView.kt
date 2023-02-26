package com.puutaro.commandclick.fragment_lib.edit_fragment.processor.edit_text_support_view

import android.R
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.text.InputType
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import com.puutaro.commandclick.fragment.EditFragment
import java.util.*


class WithDatePickerView(
    private val editFragment: EditFragment,
) {

    fun create(
        insertEditText: EditText,
        currentVariableValue: String?,
    ): LinearLayout {
        val context = editFragment.context
        val chooseButtonStr = "date"
        val innerLayout = LinearLayout(context)
        innerLayout.orientation = LinearLayout.HORIZONTAL
        insertEditText.inputType = InputType.TYPE_CLASS_TEXT
        insertEditText.setText(currentVariableValue)
        val linearParamsForEditTextTest = LinearLayout.LayoutParams(
            0,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )
        linearParamsForEditTextTest.weight = 0.8F
        insertEditText.layoutParams = linearParamsForEditTextTest
        insertEditText.isFocusableInTouchMode = true;
        innerLayout.addView(insertEditText)

        val insertButtonView = Button(context)
        insertButtonView.text = chooseButtonStr

        setOnButtonClickListener(
            insertButtonView,
            insertEditText
        )

        val insertButtonViewParam = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT,
        )
        insertButtonViewParam.weight = 0.2F
        insertButtonView.layoutParams = insertButtonViewParam
        innerLayout.addView(insertButtonView)
        return innerLayout
    }
}

internal fun setOnButtonClickListener(
    insertButtonView: Button,
    insertEditText: EditText
){

    val calender = Calendar.getInstance()
    val year = calender.get(Calendar.YEAR)
    val month = calender.get(Calendar.MONTH)
    val day = calender.get(Calendar.DAY_OF_MONTH)

    insertButtonView.setOnClickListener {
            innerView ->
        val innerContext = innerView.context
        val dateDialog = DatePickerDialog(
            innerContext, {
                    view, y, m, d ->
                val dateText =
                    listOf(y, m + 1, d)
                        .joinToString("-")
                insertEditText.setText(dateText)
            }, year, month, day
        )
        dateDialog.show()
        dateDialog.getButton(
            DialogInterface.BUTTON_POSITIVE
        ).setTextColor(
            innerContext?.getColor(R.color.black) as Int
        )
        dateDialog.getButton(
            DialogInterface.BUTTON_NEGATIVE
        ).setTextColor(
            innerContext.getColor(
                R.color.black
            )
        )
        dateDialog.getWindow()
            ?.setGravity(Gravity.BOTTOM)
    }
}

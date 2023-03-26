package com.puutaro.commandclick.proccess.edit.edit_text_support_view

import android.R
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.text.InputType
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import com.puutaro.commandclick.fragment.EditFragment
import java.util.*

class WithTimePickerView(
    private val editFragment: EditFragment
) {

    fun create(insertEditText: EditText,
               currentVariableValue: String?,
    ): LinearLayout {
        val context = editFragment.context
        val chooseButtonStr = "time"
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

private fun setOnButtonClickListener(
    insertButtonView: Button,
    insertEditText: EditText
){

    val calender = Calendar.getInstance()
    val hour = calender.get(Calendar.HOUR)
    val minites = calender.get(Calendar.MINUTE)

    insertButtonView.setOnClickListener {
            innerView ->
        val innerContext = innerView.context
        val dateDialog = TimePickerDialog(
            innerContext, {
                    _, getHour, getMinutes ->
                insertEditText.setText(
                    String.format(
                        "%02d:%02d",
                        getHour,
                        getMinutes)
                )
            },
            hour,
            minites,
            true
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

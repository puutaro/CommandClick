package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib

import android.R
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.proccess.edit.lib.ButtonSetter
import java.util.Calendar

object DatePickerProducer {
    fun make(
        insertEditText: EditText,
        editParameters: EditParameters,
        weight: Float,
    ): Button {
        val context = editParameters.context
        val chooseButtonStr = "date"
        val insertButtonView = Button(context)
        insertButtonView.text = chooseButtonStr
        ButtonSetter.set(
            context,
            insertButtonView,
            mapOf()
        )

        setOnButtonClickListener(
            insertButtonView,
            insertEditText
        )

        val insertButtonViewParam = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT,
        )
        insertButtonViewParam.weight = weight
        insertButtonView.layoutParams = insertButtonViewParam
        return insertButtonView
    }
}

private fun setOnButtonClickListener(
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
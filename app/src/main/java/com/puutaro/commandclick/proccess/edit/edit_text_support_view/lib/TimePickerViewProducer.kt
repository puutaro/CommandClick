package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib

import android.R
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.LinearLayoutCompat
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.lib.ButtonSetter
import java.util.Calendar

object TimePickerViewProducer {
    fun make(
        editFragment: EditFragment,
        insertEditText: EditText,
        editParameters: EditParameters,
        weight: Float
    ): Button {
        val context = editFragment.context
        val chooseButtonStr = "time"

        val insertButtonView = Button(context)
        insertButtonView.text = chooseButtonStr
        setOnButtonClickListener(
            insertButtonView,
            insertEditText
        )
        val insertButtonViewParam = LinearLayoutCompat.LayoutParams(
            0,
            LinearLayoutCompat.LayoutParams.MATCH_PARENT,
        )
        insertButtonViewParam.weight = weight
        insertButtonView.layoutParams = insertButtonViewParam
        ButtonSetter.set(
            context,
            insertButtonView,
            mapOf()
        )
        return insertButtonView
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

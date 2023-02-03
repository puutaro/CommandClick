package com.puutaro.commandclick.fragment_lib.edit_fragment.processor.edit_text_support_view

import android.R
import android.content.DialogInterface
import android.text.InputType
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import com.puutaro.commandclick.fragment.EditFragment
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener


class WithColorPickerView(
    private val editFragment: EditFragment,
) {
    fun create(
        insertEditText: EditText,
        currentVariableValue: String?,
    ): LinearLayout {
        val context = editFragment.context
        val chooseButtonStr = "clr"
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
        insertEditText.setFocusableInTouchMode(true);
        innerLayout.addView(insertEditText)

        val insertButtonView = Button(context)
        insertButtonView.setText(chooseButtonStr)

        setOnColorPickerButtonClickListener(
            editFragment,
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

internal fun setOnColorPickerButtonClickListener(
    editFragment: EditFragment,
    insertButtonView: Button,
    insertEditText: EditText
){

    insertButtonView.setOnClickListener {
            innerView ->
        val editFragmentContext = editFragment.context
        val innerContext = innerView.context
        val colorPickerDialogBuilder = ColorPickerDialog.Builder(innerContext)
            .setTitle("Choose custom color")
            .setPreferenceName("MyColorPickerDialog")
            .setPositiveButton("OK", ColorEnvelopeListener {
                envelope, fromUser ->
                val hexCodeSource = envelope.hexCode
                val hexCode = "#" + hexCodeSource.substring(2)
                insertEditText.setText(hexCode)

            })
            .setNegativeButton("NO", DialogInterface.OnClickListener {
                    dialogInterface, i -> dialogInterface.dismiss()
            })
        val colorPickerDialog = colorPickerDialogBuilder.create()
        colorPickerDialog.show()
        colorPickerDialog.getButton(
                DialogInterface.BUTTON_POSITIVE
                ).setTextColor(
                    editFragmentContext?.getColor(R.color.black) as Int
                )
        colorPickerDialog.getButton(
            DialogInterface.BUTTON_NEGATIVE
        ).setTextColor(
            editFragmentContext.getColor(
                R.color.black
            )
        )
        colorPickerDialog.getWindow()
            ?.setGravity(Gravity.BOTTOM)
    }
}

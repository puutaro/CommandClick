package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib

import android.R
import android.content.DialogInterface
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import com.puutaro.commandclick.fragment.EditFragment
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener

object ColorPickerViewProducer {
    fun make(
        editFragment: EditFragment,
        insertEditText: EditText,
        weight: Float,
    ): Button {
        val context = editFragment.context
        val chooseButtonStr = "clr"
        val insertButtonView = Button(context)
        insertButtonView.text = chooseButtonStr

        setOnColorPickerButtonClickListener(
            editFragment,
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

    private fun setOnColorPickerButtonClickListener(
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
                    val hexCode = "'#" + hexCodeSource.substring(2) + "'"
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
}
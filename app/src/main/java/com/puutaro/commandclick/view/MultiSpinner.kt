package com.puutaro.commandclick.view

import android.R
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.OnMultiChoiceClickListener
import android.util.AttributeSet
import android.widget.ArrayAdapter
import android.widget.Spinner


class MultiSpinner : androidx.appcompat.widget.AppCompatSpinner, OnMultiChoiceClickListener, DialogInterface.OnCancelListener {
    private var items: List<String>? = null
    private lateinit var selected: BooleanArray
    private var defaultText: String? = null
    private var position = 0

    constructor(context: Context) : super(context) {}
    constructor(arg0: Context, arg1: AttributeSet?) : super(arg0, arg1) {}
    constructor(arg0: Context, arg1: AttributeSet?, arg2: Int) : super(arg0, arg1, arg2) {}

    override fun onClick(dialog: DialogInterface, which: Int, isChecked: Boolean) {
        if (isChecked) {
            selected[which] = true
        } else {
            selected[which] = false
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        // refresh text on spinner
        val spinnerBuffer = StringBuffer()
        var someUnselected = false
        val tempItem = items ?: return
        for (i in tempItem.indices) {
            if (selected[i]) {
                spinnerBuffer.append(tempItem[i])
                spinnerBuffer.append(", ")
            } else {
                someUnselected = true
            }
        }
        var spinnerText: String?
        if (someUnselected) {
            spinnerText = spinnerBuffer.toString()
            if (spinnerText.length > 2) spinnerText =
                spinnerText.substring(0, spinnerText.length - 2)
        } else {
            spinnerText = defaultText
        }
        val adapter = ArrayAdapter(
            context,
            R.layout.simple_spinner_item, arrayOf(spinnerText)
        )
        setAdapter(adapter)
    }

    override fun performClick(): Boolean {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setMultiChoiceItems(items!!.toTypedArray(), selected, this)
        builder.setPositiveButton(
            R.string.ok,
            DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
        builder.setOnCancelListener(this)
        builder.show()
        return true
    }

    fun setItems(items: List<String>, allText: String?, position: Int) {
        this.items = items
        defaultText = allText
        this.position = position
        // all selected by default
        selected = BooleanArray(items.size)
        for (i in selected.indices) {
            selected[i] = true
        }

        // all text on the spinner
        val adapter = ArrayAdapter(
            context,
            R.layout.simple_spinner_item, arrayOf(allText)
        )
        setAdapter(adapter)
    }
}
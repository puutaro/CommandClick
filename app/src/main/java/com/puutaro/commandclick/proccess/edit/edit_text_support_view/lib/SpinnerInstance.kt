package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib

import android.content.Context
import android.widget.Spinner

object SpinnerInstance {
    fun make(
        spinnerMaxStringNum: Int,
        context: Context?
    ): Spinner {
        val spinnerDialogStringNumThreshold = 30
        return if(
            spinnerMaxStringNum < spinnerDialogStringNumThreshold
        ) {
            Spinner(context)
        } else Spinner(context, Spinner.MODE_DIALOG)
    }
}
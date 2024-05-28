package com.puutaro.commandclick.proccess.edit.lib

import android.content.Context
import android.widget.Spinner
import com.puutaro.commandclick.util.str.StringLength

object SpinnerInstance {
    fun make(
        context: Context?,
        editableSpinnerList: List<String>,
        onFixNormalSpinner: Boolean,
    ): Spinner {
        if(
            onFixNormalSpinner
        ) return Spinner(context)
        val spinnerMaxStringNum = StringLength.maxCountFromList(editableSpinnerList)
        val spinnerDialogStringNumThreshold = 30
        return if(
            spinnerMaxStringNum < spinnerDialogStringNumThreshold
        ) {
            Spinner(context)
        } else Spinner(context, Spinner.MODE_DIALOG)
    }
}
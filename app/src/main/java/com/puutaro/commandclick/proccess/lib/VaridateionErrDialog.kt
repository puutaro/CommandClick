package com.puutaro.commandclick.proccess.lib

import android.R
import android.app.AlertDialog
import android.content.DialogInterface
import androidx.fragment.app.Fragment


class VaridateionErrDialog {

    companion object {
        fun show(
            fragment: Fragment,
            shellScriptPath: String,
            validateErrMessage: String
        ) {
            val context = fragment.context
            val alertDialog = AlertDialog.Builder(context)
                .setTitle(
                    "Raise_validate_err"
                )
                .setMessage("\tPath: ${shellScriptPath}\n\t${validateErrMessage}")
                .setPositiveButton("OK", null)
                .show()
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(
                context?.getColor(R.color.black) as Int
            );
        }
    }
}
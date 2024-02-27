package com.puutaro.commandclick.proccess.lib

import androidx.fragment.app.Fragment
import com.puutaro.commandclick.util.dialog.DialogObject


object VariationErrDialog {

    fun show(
        fragment: Fragment,
        shellScriptPath: String,
        validateErrMessage: String
    ) {
        val context = fragment.context
            ?: return
        DialogObject.simpleTextShow(
            context,
            "Raise validate err",
            "\tPath: ${shellScriptPath}\n\t${validateErrMessage}"
        )
    }
}
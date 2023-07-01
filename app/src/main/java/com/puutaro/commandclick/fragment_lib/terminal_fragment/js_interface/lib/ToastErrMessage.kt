package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib

import android.view.Gravity
import android.widget.Toast
import com.puutaro.commandclick.fragment.TerminalFragment

object ToastErrMessage {
    fun launch(
        terminalFragment: TerminalFragment,
        errMessage: String
    ){
        if(
            errMessage.isEmpty()
        ) return
        val context = terminalFragment.context
        val ts = Toast.makeText(
            context,
            errMessage,
            Toast.LENGTH_LONG
        )
        ts.setGravity(Gravity.CENTER, 0, 0)
        ts.show()
    }
}
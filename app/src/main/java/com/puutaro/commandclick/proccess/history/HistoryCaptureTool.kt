package com.puutaro.commandclick.proccess.history

import androidx.fragment.app.Fragment
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment

object HistoryCaptureTool {
    fun launchCapture(
        fragment: Fragment,
    ){
        when(fragment){
            is CommandIndexFragment -> {
                val listener = fragment.context as? CommandIndexFragment.OnCaptureActivityListenerForIndex
                    ?: return
                listener.onCaptureActivityForIndex()
            }
            is EditFragment -> {
                val listener = fragment.context as? EditFragment.OnCaptureActivityListenerForEdit
                    ?: return
                listener.onCaptureActivityForEdit()
            }
            is TerminalFragment -> {
                val listener = fragment.context as? TerminalFragment.OnCaptureActivityListenerForTerm
                    ?: return
                listener.onCaptureActivityForTerm()
            }
            else -> return
        }
    }

}
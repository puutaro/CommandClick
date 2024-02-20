package com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs

import android.widget.Toast
import com.puutaro.commandclick.common.variable.intent.scheme.BroadCastIntentSchemeForEdit
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender

object ListSyncer {
    fun sync(
        editFragment: EditFragment
    ){
        val context = editFragment.context
            ?: return

        Toast.makeText(
            context,
            "sync ok",
            Toast.LENGTH_SHORT
        ).show()
        BroadcastSender.normalSend(
            context,
            BroadCastIntentSchemeForEdit.UPDATE_INDEX_LIST.action
        )
    }
}
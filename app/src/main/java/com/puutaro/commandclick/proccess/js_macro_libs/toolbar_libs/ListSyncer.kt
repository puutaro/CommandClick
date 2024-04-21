package com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs

import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.common.variable.intent.scheme.BroadCastIntentSchemeForEdit
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender

object ListSyncer {
    fun sync(
        editFragment: EditFragment
    ){
        val context = editFragment.context
            ?: return

        ToastUtils.showShort("sync ok")
        BroadcastSender.normalSend(
            context,
            BroadCastIntentSchemeForEdit.UPDATE_INDEX_LIST.action
        )
    }
}
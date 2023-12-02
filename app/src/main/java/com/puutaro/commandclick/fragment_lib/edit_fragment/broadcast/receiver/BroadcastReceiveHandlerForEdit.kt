package com.puutaro.commandclick.fragment_lib.edit_fragment.broadcast.receiver

import android.content.Intent
import com.puutaro.commandclick.common.variable.intent.scheme.BroadCastIntentSchemeForEdit
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.WithIndexListView

object BroadcastReceiveHandlerForEdit {
    fun handle(
        editFragment: EditFragment,
        intent: Intent
    ){
        if(
            !editFragment.isVisible
        ) return
        val action = intent.action
        when(action){
            BroadCastIntentSchemeForEdit.UPDATE_INDEX_LIST.action -> {
                WithIndexListView.listIndexListUpdateFileList(
                    editFragment,
                    WithIndexListView.makeFileList()
                )
            }
            else -> {}
        }
    }
}
package com.puutaro.commandclick.fragment_lib.edit_fragment.broadcast.receiver

import android.content.Intent
import android.widget.EditText
import androidx.core.view.isVisible
import com.puutaro.commandclick.R
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
                val editListSearchEditText = editFragment.binding.editListSearchEditText
                if(editListSearchEditText.isVisible) {
                    editListSearchEditText.setText(String())
                }
                WithIndexListView.listIndexListUpdateFileList(
                    editFragment,
                    WithIndexListView.makeFileListHandler(editFragment.isInstallFannelForListIndex)
                )
            }
            else -> {}
        }
    }
}
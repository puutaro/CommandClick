package com.puutaro.commandclick.fragment_lib.edit_fragment.broadcast.receiver

import android.content.Intent
import androidx.core.view.isVisible
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeForEdit
import com.puutaro.commandclick.component.adapter.EditComponentListAdapter
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ListViewToolForListIndexAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex

object BroadcastReceiveHandlerForEdit {
    fun handle(
        editFragment: EditFragment,
        intent: Intent
    ){
        if(
            !editFragment.isVisible
        ) return
        val action = intent.action
        val editComponentListAdapter = editFragment.binding.editListRecyclerView.adapter as EditComponentListAdapter
        when(action){
            BroadCastIntentSchemeForEdit.UPDATE_INDEX_LIST.action -> {
                val editListSearchEditText = editFragment.binding.editListSearchEditText
                if(editListSearchEditText.isVisible) {
                    editListSearchEditText.setText(String())
                }
                ListViewToolForListIndexAdapter.listIndexListUpdateFileList(
                    editComponentListAdapter,
                    ListSettingsForListIndex.ListIndexListMaker.makeLineMapListHandler(
                        editFragment.fannelInfoMap,
                        editFragment.setReplaceVariableMap,
                        editComponentListAdapter.editListMap,
                        editFragment.busyboxExecutor,
                    )
                )
            }
            else -> {}
        }
    }
}
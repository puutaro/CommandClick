package com.puutaro.commandclick.component.adapter.lib.list_index_adapter

import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.puutaro.commandclick.component.adapter.EditComponentListAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ListViewToolForListIndexAdapter {

    private var listIndexScrollToBottomJob: Job? = null

    fun listIndexListUpdateFileList(
        editFragment: EditFragment,
        updateLineMapList: List<Map<String, String>>,
    ){
        val editListRecyclerView = editFragment.binding.editListRecyclerView
        if(
            !editListRecyclerView.isVisible
        ) return
        val listIndexForEditAdapter =
            editListRecyclerView.adapter as? EditComponentListAdapter
                ?: return
        if(
            listIndexForEditAdapter.lineMapList ==
            updateLineMapList
        ) return
        listIndexForEditAdapter.lineMapList.clear()
        listIndexForEditAdapter.lineMapList.addAll(updateLineMapList)
        listIndexForEditAdapter.notifyDataSetChanged()
        val isReverseLayout = ListSettingsForListIndex.howReverseLayout(
            listIndexForEditAdapter.fannelInfoMap,
            listIndexForEditAdapter.setReplaceVariablesMap,
            listIndexForEditAdapter.indexListMap
        )
        if(!isReverseLayout) return
        scrollToBottom(
            editListRecyclerView,
            listIndexForEditAdapter,
        )
    }

    fun scrollToBottom(
        editListRecyclerView: RecyclerView,
        editComponentListAdapter: EditComponentListAdapter,
    ){
        listIndexScrollToBottomJob?.cancel()
        listIndexScrollToBottomJob = CoroutineScope(Dispatchers.Main).launch {
            val layoutManager =
                editListRecyclerView.layoutManager as? LinearLayoutManager
            val scrollToPosi = editComponentListAdapter.itemCount - 1
            withContext(Dispatchers.Main){
                for(i in 1..30){
                    val prePosi = layoutManager?.findLastCompletelyVisibleItemPosition()
                    if(
                        prePosi == scrollToPosi
                    ) break
                    execScroll(
                        layoutManager,
                        scrollToPosi,
                    )
                    delay(150)
                }
            }
            withContext(Dispatchers.Main){
                for(i in 1..3){
                    delay(150)
                    execScroll(
                        layoutManager,
                        scrollToPosi,
                    )
                }
            }
        }
    }

    private fun execScroll(
        layoutManager: LinearLayoutManager?,
        scrollToPosi: Int,
    ){
        try {
            layoutManager?.scrollToPosition(
                scrollToPosi
            )
        }catch (e: Exception){
            return
        }
    }
}
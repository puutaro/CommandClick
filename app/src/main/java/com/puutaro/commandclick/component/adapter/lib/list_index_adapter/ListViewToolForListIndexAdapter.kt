package com.puutaro.commandclick.component.adapter.lib.list_index_adapter

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.custom_manager.PreLoadLayoutManager
import com.puutaro.commandclick.fragment.EditFragment
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
        updateList: List<String>,
    ){
        val editListRecyclerView = editFragment.binding.editListRecyclerView
        if(
            !editListRecyclerView.isVisible
        ) return
        val listIndexForEditAdapter =
            editListRecyclerView.adapter as? ListIndexForEditAdapter
                ?: return
        if(
            listIndexForEditAdapter.listIndexList ==
            updateList
        ) return
        listIndexForEditAdapter.listIndexList.clear()
        listIndexForEditAdapter.listIndexList.addAll(updateList)
        listIndexForEditAdapter.notifyDataSetChanged()
        scrollToBottom(
            editListRecyclerView,
            listIndexForEditAdapter,
        )
    }

    fun scrollToBottom(
        editListRecyclerView: RecyclerView,
        listIndexForEditAdapter: ListIndexForEditAdapter,
    ){
        listIndexScrollToBottomJob?.cancel()
        listIndexScrollToBottomJob = CoroutineScope(Dispatchers.Main).launch {
            val layoutManager = editListRecyclerView.layoutManager as? PreLoadLayoutManager
            val scrollToPosi = listIndexForEditAdapter.itemCount - 1
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

    fun execScroll(
        layoutManager: PreLoadLayoutManager?,
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
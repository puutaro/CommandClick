package com.puutaro.commandclick.component.adapter.lib.list_index_adapter

import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.puutaro.commandclick.component.adapter.ListIndexAdapter
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
            editListRecyclerView.adapter as? ListIndexAdapter
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
        listIndexForEditAdapter: ListIndexAdapter,
    ){
        listIndexScrollToBottomJob?.cancel()
        listIndexScrollToBottomJob = CoroutineScope(Dispatchers.Main).launch {
            val layoutManager =
                editListRecyclerView.layoutManager as? LinearLayoutManager
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
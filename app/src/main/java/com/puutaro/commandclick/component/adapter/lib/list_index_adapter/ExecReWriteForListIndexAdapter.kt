package com.puutaro.commandclick.component.adapter.lib.list_index_adapter

import com.puutaro.commandclick.component.adapter.ListIndexAdapter
import com.puutaro.commandclick.fragment.EditFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ExecReWriteForListIndexAdapter {
    fun replaceListElementForTsv(
        editFragment: EditFragment,
        srcAndRepLinePairList: List<Pair<String, String>>,
    ){
        val binding = editFragment.binding
        val listIndexAdapter =
            binding.editListRecyclerView.adapter as ListIndexAdapter
        val srcAndRepLinePairListSize = srcAndRepLinePairList.size
        CoroutineScope(Dispatchers.IO).launch {
            srcAndRepLinePairList.forEach {
                val replaceItemIndex = withContext(Dispatchers.IO) {
                    val srcTsvLine = it.first
                    listIndexAdapter.listIndexList.indexOf(srcTsvLine)
                }
                if (replaceItemIndex < 0) return@forEach
                withContext(Dispatchers.IO) {
                    listIndexAdapter.listIndexList[replaceItemIndex] = it.second
                }
                withContext(Dispatchers.Main) {
                    listIndexAdapter.notifyItemChanged(replaceItemIndex)
                }
                if (srcAndRepLinePairListSize != 1) return@forEach
                val editListRecyclerView = binding.editListRecyclerView
                withContext(Dispatchers.Main) {
                    editListRecyclerView.layoutManager?.scrollToPosition(replaceItemIndex)
                }
            }
        }
    }
}
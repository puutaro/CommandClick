package com.puutaro.commandclick.component.adapter.lib.list_index_adapter

import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.fragment.EditFragment

object ExecReWriteForListIndexAdapter {
    fun replaceListElementForTsv(
        editFragment: EditFragment,
        srcAndRepLinePairList: List<Pair<String, String>>,
    ){
        val binding = editFragment.binding
        val listIndexAdapter =
            binding.editListRecyclerView.adapter as ListIndexForEditAdapter
        val srcAndRepLinePairListSize = srcAndRepLinePairList.size
        srcAndRepLinePairList.forEach {
            val srcTsvLine = it.first
            val replaceItemIndex = listIndexAdapter.listIndexList.indexOf(srcTsvLine)
            if(replaceItemIndex < 0) return@forEach
            listIndexAdapter.listIndexList[replaceItemIndex] = it.second
            listIndexAdapter.notifyItemChanged(replaceItemIndex)
            if(srcAndRepLinePairListSize != 1) return@forEach
            val editListRecyclerView = binding.editListRecyclerView
            editListRecyclerView.layoutManager?.scrollToPosition(replaceItemIndex)
        }
    }
}
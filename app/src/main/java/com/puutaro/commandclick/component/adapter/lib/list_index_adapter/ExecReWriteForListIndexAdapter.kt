package com.puutaro.commandclick.component.adapter.lib.list_index_adapter

import com.puutaro.commandclick.component.adapter.EditComponentListAdapter
import com.puutaro.commandclick.fragment.EditFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ExecReWriteForListIndexAdapter {
    fun replaceListElementForTsv(
        editFragment: EditFragment,
        srcAndRepLineMapPairList: List<Pair<Map<String, String>, Map<String, String>>>,
    ){
        val binding = editFragment.binding
        val editComponentListAdapter =
            binding.editListRecyclerView.adapter as EditComponentListAdapter
        val srcAndRepLinePairListSize = srcAndRepLineMapPairList.size
        CoroutineScope(Dispatchers.IO).launch {
            srcAndRepLineMapPairList.forEach {
                val replaceItemIndex = withContext(Dispatchers.IO) {
                    val srcTsvLine = it.first
                    editComponentListAdapter.lineMapList.indexOf(srcTsvLine)
                }
                if (replaceItemIndex < 0) return@forEach
                withContext(Dispatchers.IO) {
                    editComponentListAdapter.lineMapList[replaceItemIndex] = it.second
                }
                withContext(Dispatchers.Main) {
                    editComponentListAdapter.notifyItemChanged(replaceItemIndex)
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
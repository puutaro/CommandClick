package com.puutaro.commandclick.component.adapter.lib.list_index_adapter

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.puutaro.commandclick.component.adapter.EditComponentListAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ExecReWriteForListIndexAdapter {
    fun replaceListElementForTsv(
//        fragment: Fragment,
        editListRecyclerView: RecyclerView,
        srcAndRepLineMapPairList: List<Pair<Map<String, String>, Map<String, String>>>,
    ){
//        val binding = fragment.binding
        val editComponentListAdapter =
            editListRecyclerView.adapter as EditComponentListAdapter
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
//                val editListRecyclerView = binding.editListRecyclerView
                withContext(Dispatchers.Main) {
                    editListRecyclerView.layoutManager?.scrollToPosition(replaceItemIndex)
                }
            }
        }
    }
}
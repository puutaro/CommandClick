package com.puutaro.commandclick.component.adapter.lib.edit_list_adapter

import androidx.recyclerview.widget.RecyclerView
import com.puutaro.commandclick.component.adapter.EditConstraintListAdapter
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
        val editConstraintListAdapter =
            editListRecyclerView.adapter as EditConstraintListAdapter
        val srcAndRepLinePairListSize = srcAndRepLineMapPairList.size
        CoroutineScope(Dispatchers.IO).launch {
            srcAndRepLineMapPairList.forEach {
                val replaceItemIndex = withContext(Dispatchers.IO) {
                    val srcTsvLine = it.first
                    editConstraintListAdapter.lineMapList.indexOf(srcTsvLine)
                }
                if (replaceItemIndex < 0) return@forEach
                withContext(Dispatchers.IO) {
                    editConstraintListAdapter.lineMapList[replaceItemIndex] = it.second
                }
                withContext(Dispatchers.Main) {
                    editConstraintListAdapter.notifyItemChanged(replaceItemIndex)
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
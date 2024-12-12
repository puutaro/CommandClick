package com.puutaro.commandclick.component.adapter.lib.edit_list_adapter

import androidx.recyclerview.widget.LinearLayoutManager
import com.puutaro.commandclick.component.adapter.EditConstraintListAdapter

object ListViewToolForEditListAdapter {

//    private var listIndexScrollToBottomJob: Job? = null

    fun editListUpdateFileList(
        editConstraintListAdapter: EditConstraintListAdapter,
        updateLineMapList: List<Map<String, String>>,
    ){
        if(
            editConstraintListAdapter.lineMapList ==
            updateLineMapList
        ) return
        editConstraintListAdapter.lineMapList.clear()
        editConstraintListAdapter.lineMapList.addAll(updateLineMapList)
        editConstraintListAdapter.notifyDataSetChanged()
    }

    fun editConstraintListUpdateFileList(
        editConstraintListAdapter: EditConstraintListAdapter,
        updateLineMapList: List<Map<String, String>>,
    ){
        if(
            editConstraintListAdapter.lineMapList ==
            updateLineMapList
        ) return
        editConstraintListAdapter.lineMapList.clear()
        editConstraintListAdapter.lineMapList.addAll(updateLineMapList)
        editConstraintListAdapter.notifyDataSetChanged()
    }

//    fun scrollToBottom(
//        editListRecyclerView: RecyclerView,
//        editComponentListAdapter: EditComponentListAdapter,
//    ){
//        return
//        listIndexScrollToBottomJob?.cancel()
//        listIndexScrollToBottomJob = CoroutineScope(Dispatchers.Main).launch {
//            val layoutManager =
//                editListRecyclerView.layoutManager as? LinearLayoutManager
//            val scrollToPosi = editComponentListAdapter.itemCount - 1
//            withContext(Dispatchers.Main){
//                for(i in 1..30){
//                    val prePosi = layoutManager?.findLastCompletelyVisibleItemPosition()
//                    if(
//                        prePosi == scrollToPosi
//                    ) break
//                    execScroll(
//                        layoutManager,
//                        scrollToPosi,
//                    )
//                    delay(150)
//                }
//            }
//            withContext(Dispatchers.Main){
//                for(i in 1..3){
//                    delay(150)
//                    execScroll(
//                        layoutManager,
//                        scrollToPosi,
//                    )
//                }
//            }
//        }
//    }

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
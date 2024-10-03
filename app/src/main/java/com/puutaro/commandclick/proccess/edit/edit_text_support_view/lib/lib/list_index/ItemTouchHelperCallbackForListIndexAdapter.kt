package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.list_index

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_DRAG
import androidx.recyclerview.widget.RecyclerView
import com.puutaro.commandclick.component.adapter.EditComponentListAdapter
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ExecSwitcherForListIndexAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs.ExecSimpleDelete
import com.puutaro.commandclick.util.list.ListTool

object ItemTouchHelperCallbackForListIndexAdapter {

    fun set(
        editFragment: EditFragment,
        recyclerView: RecyclerView,
        editComponentListAdapter: EditComponentListAdapter,
    ){
        val editByDragMap = ListSettingsForListIndex.makeEditByDragMap(
            editFragment.listIndexConfigMap,
        )
        val enableEditByDrag = !ListSettingsForListIndex.howDisableEditByDrag(
            editFragment.fannelInfoMap,
            editFragment.setReplaceVariableMap,
            editByDragMap
        )
        if(!enableEditByDrag) return
        val mIth = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT
            ) {

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val adapter = recyclerView.adapter as EditComponentListAdapter
                    val fromViewHolder = viewHolder as
                            EditComponentListAdapter.ListIndexListViewHolder
                    val toViewHolder = target as
                            EditComponentListAdapter.ListIndexListViewHolder
                    val from = fromViewHolder.bindingAdapterPosition
                    val to = toViewHolder.bindingAdapterPosition
                    adapter.notifyItemMoved(from, to)
                    ListTool.switchMapList(
                        editComponentListAdapter.lineMapList,
                        from,
                        to,
                    )
                    switchHandler(
                        editFragment,
                        editComponentListAdapter,
                        fromViewHolder,
                        toViewHolder
                    )
                    return true
                }

                override fun onSwiped(
                    viewHolder: RecyclerView.ViewHolder,
                    direction: Int
                ) {
                    if(
                        direction != ItemTouchHelper.LEFT
                    ) return
                    val listIndexViewHolder =
                        viewHolder as EditComponentListAdapter.ListIndexListViewHolder
                    ExecSimpleDelete.removeController(
                        editFragment,
                        recyclerView,
                        editComponentListAdapter,
                        editComponentListAdapter.lineMapList[listIndexViewHolder.bindingAdapterPosition],
                        listIndexViewHolder.bindingAdapterPosition,
                    )
                }

                override fun onSelectedChanged(
                    viewHolder: RecyclerView.ViewHolder?, actionState: Int
                ) {
                    super.onSelectedChanged(viewHolder, actionState)

                    if (actionState == ACTION_STATE_DRAG) {
                        viewHolder?.itemView?.alpha = 0.5f
                    }
                }

                override fun clearView(
                    recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder
                ) {
                    super.clearView(recyclerView, viewHolder)

                    viewHolder.itemView.alpha = 1.0f
                }
            })
        mIth.attachToRecyclerView(recyclerView)
    }

    private fun switchHandler(
        editFragment: EditFragment,
        listIndexForEditAdapter: EditComponentListAdapter,
        fromViewHolder: EditComponentListAdapter.ListIndexListViewHolder,
        toViewHolder: EditComponentListAdapter.ListIndexListViewHolder
    ){
        ExecSwitcherForListIndexAdapter.updateTsv(
            editFragment,
            listIndexForEditAdapter.lineMapList
        )
//        val listIndexTypeKey = ListIndexAdapter.listIndexTypeKey
//        when(listIndexTypeKey){
////            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL -> {}
//            TypeSettingsForListIndex.ListIndexTypeKey.NORMAL -> {
//                val fromFileName = fromViewHolder.fileName
//                val toFileName = toViewHolder.fileName
//                val fromMaterialCardView = fromViewHolder.materialCardView
//                val toMaterialCardView = toViewHolder.materialCardView
//                val fromChecked = fromMaterialCardView.isChecked
//                val toChecked = toMaterialCardView.isChecked
//                fromMaterialCardView.isChecked = toChecked
//                toMaterialCardView.isChecked = fromChecked
//                val parentDirPath = ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
//                    editFragment,
//                    ListIndexAdapter.indexListMap,
//                    ListIndexAdapter.listIndexTypeKey
//                )
//                FileSystems.switchLastModify(
//                    File(parentDirPath, fromFileName),
//                    File(parentDirPath, toFileName)
//                )
//            }
//            TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT ->
//                ExecSwitcherForListIndexAdapter.updateTsv(
//                    editFragment,
//                    listIndexForEditAdapter.listIndexList
//                )
//        }
    }
}

package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.list_index

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_DRAG
import androidx.recyclerview.widget.RecyclerView
import com.puutaro.commandclick.component.adapter.EditConstraintListAdapter
import com.puutaro.commandclick.component.adapter.lib.edit_list_adapter.ExecSwitcherForListIndexAdapter
import com.puutaro.commandclick.proccess.js_macro_libs.edit_list_libs.ExecSimpleDelete
import com.puutaro.commandclick.proccess.edit_list.config_settings.LayoutSettingsForEditList
import com.puutaro.commandclick.util.list.ListTool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

object ItemTouchHelperCallbackForEditListAdapter {

    suspend fun set(
//        editFragment: EditFragment,
        fragment: Fragment,
        fannelInfoMap: HashMap<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        recyclerView: RecyclerView,
        editConstraintListAdapter: EditConstraintListAdapter,
        layoutConfigMap: Map<String, String>
    ){
        delay(1500)
        val editByDragMap = LayoutSettingsForEditList.makeEditByDragMap(
            layoutConfigMap,
        )
        val enableEditByDrag = !LayoutSettingsForEditList.howDisableEditByDrag(
            fannelInfoMap,
            setReplaceVariableMap,
            editByDragMap
        )
        if(!enableEditByDrag) return
        withContext(Dispatchers.Main) {
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
                        val editConstraintListAdapter = recyclerView.adapter as EditConstraintListAdapter
                        val fromViewHolder = viewHolder as
                                EditConstraintListAdapter.EditListViewHolder
                        val toViewHolder = target as
                                EditConstraintListAdapter.EditListViewHolder
                        val from = fromViewHolder.bindingAdapterPosition
                        val to = toViewHolder.bindingAdapterPosition
                        editConstraintListAdapter.notifyItemMoved(from, to)
                        ListTool.switchMapList(
                            editConstraintListAdapter.lineMapList,
                            from,
                            to,
                        )
                        switchHandler(
                            fannelInfoMap,
                            setReplaceVariableMap,
                            editConstraintListAdapter,
                            fromViewHolder,
                            toViewHolder
                        )
                        return true
                    }

                    override fun onSwiped(
                        viewHolder: RecyclerView.ViewHolder,
                        direction: Int
                    ) {
                        if (
                            direction != ItemTouchHelper.LEFT
                        ) return
                        val listIndexViewHolder =
                            viewHolder as EditConstraintListAdapter.EditListViewHolder
                        ExecSimpleDelete.removeController(
                            fragment,
                            recyclerView,
                            editConstraintListAdapter,
                            editConstraintListAdapter.lineMapList[listIndexViewHolder.bindingAdapterPosition],
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
    }

    private fun switchHandler(
        fannelInfoMap: HashMap<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        editConstraintListAdapter: EditConstraintListAdapter,
        fromViewHolder: EditConstraintListAdapter.EditListViewHolder,
        toViewHolder: EditConstraintListAdapter.EditListViewHolder
    ){
        ExecSwitcherForListIndexAdapter.updateTsv(
            fannelInfoMap,
            setReplaceVariableMap,
            editConstraintListAdapter,
            editConstraintListAdapter.lineMapList
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

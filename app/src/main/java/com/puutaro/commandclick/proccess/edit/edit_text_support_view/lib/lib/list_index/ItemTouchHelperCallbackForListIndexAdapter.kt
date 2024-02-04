package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.list_index

import android.app.Dialog
import android.view.Gravity
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_DRAG
import androidx.recyclerview.widget.RecyclerView
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ExecRemoveForListIndexAdapter
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ExecSwitcherForListIndexAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.TypeSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.libs.js_path_handler_for_list_index.ExecItemDelete
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.list.ListTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object ItemTouchHelperCallbackForListIndexAdapter {

    fun set(
        editFragment: EditFragment,
        recyclerView: RecyclerView,
        listIndexForEditAdapter: ListIndexForEditAdapter,
    ){
        val editByDragMap = ListSettingsForListIndex.makeEditByDragMap(
            editFragment.listIndexConfigMap,
        )
        val enableEditByDrag = !ListSettingsForListIndex.howDisableEditByDrag(editByDragMap)
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
                    val adapter = recyclerView.adapter as ListIndexForEditAdapter
                    val fromViewHolder = viewHolder as
                            ListIndexForEditAdapter.ListIndexListViewHolder
                    val toViewHolder = target as
                            ListIndexForEditAdapter.ListIndexListViewHolder
                    val from = fromViewHolder.bindingAdapterPosition
                    val to = toViewHolder.bindingAdapterPosition
                    adapter.notifyItemMoved(from, to)
                    ListTool.switchList(
                        listIndexForEditAdapter.listIndexList,
                        from,
                        to,
                    )
                    switchHandler(
                        editFragment,
                        listIndexForEditAdapter,
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
                    removeController(
                        editFragment,
                        recyclerView,
                        listIndexForEditAdapter,
                        viewHolder as ListIndexForEditAdapter.ListIndexListViewHolder
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
        listIndexForEditAdapter: ListIndexForEditAdapter,
        fromViewHolder: ListIndexForEditAdapter.ListIndexListViewHolder,
        toViewHolder: ListIndexForEditAdapter.ListIndexListViewHolder
    ){
        val listIndexTypeKey = ListIndexForEditAdapter.listIndexTypeKey
        when(listIndexTypeKey){
            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL -> {}
            TypeSettingsForListIndex.ListIndexTypeKey.NORMAL -> {
                val fromFileName = fromViewHolder.fileName
                val toFileName = toViewHolder.fileName
                val fromMaterialCardView = fromViewHolder.materialCardView
                val toMaterialCardView = toViewHolder.materialCardView
                val fromChecked = fromMaterialCardView.isChecked
                val toChecked = toMaterialCardView.isChecked
                fromMaterialCardView.isChecked = toChecked
                toMaterialCardView.isChecked = fromChecked
                val parentDirPath = ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
                    editFragment,
                    ListIndexForEditAdapter.indexListMap,
                    ListIndexForEditAdapter.listIndexTypeKey
                )
                FileSystems.switchLastModify(
                    File(parentDirPath, fromFileName),
                    File(parentDirPath, toFileName)
                )
            }
            TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT ->
                ExecSwitcherForListIndexAdapter.updateTsv(
                    editFragment,
                    listIndexForEditAdapter.listIndexList
                )
        }
    }

    private fun removeController(
        editFragment: EditFragment,
        recyclerView: RecyclerView,
        listIndexForEditAdapter: ListIndexForEditAdapter,
        listIndexViewHolder: ListIndexForEditAdapter.ListIndexListViewHolder
    ){
        val editByDragMap = ListSettingsForListIndex.makeEditByDragMap(
            editFragment.listIndexConfigMap,
        )
        val enableDeleteConfirm = !ListSettingsForListIndex.howDisableDeleteConfirm(editByDragMap)
        when(enableDeleteConfirm){
            false -> removeItem(
                editFragment,
                listIndexForEditAdapter,
                listIndexViewHolder
            )
            else -> DeleteConfirmDialog.launch(
                editFragment,
                recyclerView,
                listIndexViewHolder,
                listIndexForEditAdapter
            )
        }
    }

    private fun removeItem(
        editFragment: EditFragment,
        listIndexForEditAdapter: ListIndexForEditAdapter,
        listIndexViewHolder: ListIndexForEditAdapter.ListIndexListViewHolder
    ){
        val position = listIndexViewHolder.layoutPosition
        listIndexForEditAdapter.notifyItemRemoved(position)
        val removeItemLine = listIndexForEditAdapter.listIndexList[position]
        listIndexForEditAdapter.listIndexList.removeAt(position)
        ExecRemoveForListIndexAdapter.removeCon(
            ListIndexForEditAdapter.listIndexTypeKey,
            removeItemLine
        )

        ListIndexForEditAdapter.onDeleteConFile
        execRemoveItemHandler(
            editFragment,
            listIndexViewHolder,
            removeItemLine
        )
    }

    private fun execRemoveItemHandler(
        editFragment: EditFragment,
        listIndexViewHolder: ListIndexForEditAdapter.ListIndexListViewHolder,
        removeItemLine: String,
    ){
        when(ListIndexForEditAdapter.listIndexTypeKey){
            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL -> {}
            TypeSettingsForListIndex.ListIndexTypeKey.NORMAL -> {
                val filterDir = ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
                    editFragment,
                    ListIndexForEditAdapter.indexListMap,
                    ListIndexForEditAdapter.listIndexTypeKey
                )
                ExecItemDelete.execDeleteAfterConfirm(
                    editFragment,
                    filterDir,
                    listIndexViewHolder.fileName,
                )
            }
            TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT ->
                ExecRemoveForListIndexAdapter.updateTsv(
                    editFragment,
                    listOf(removeItemLine),
                )
        }
    }

    private object DeleteConfirmDialog {

        private var getPermissionConfirmDialog: Dialog? = null

        fun launch(
            editFragment: EditFragment,
            recyclerView: RecyclerView,
            listIndexViewHolder: ListIndexForEditAdapter.ListIndexListViewHolder,
            listIndexForEditAdapter: ListIndexForEditAdapter
        ){
            val context = editFragment.context
                ?: return
            getPermissionConfirmDialog = Dialog(
                context
            )
            getPermissionConfirmDialog?.setContentView(
                com.puutaro.commandclick.R.layout.confirm_text_dialog
            )
            val confirmTitleTextView =
                getPermissionConfirmDialog?.findViewById<AppCompatTextView>(
                    com.puutaro.commandclick.R.id.confirm_text_dialog_title
                )
            confirmTitleTextView?.text = "Delete ok?"
            val confirmContentTextView =
                getPermissionConfirmDialog?.findViewById<AppCompatTextView>(
                    com.puutaro.commandclick.R.id.confirm_text_dialog_text_view
                )
            confirmContentTextView?.text = listIndexViewHolder.fileName
            val confirmCancelButton =
                getPermissionConfirmDialog?.findViewById<AppCompatImageButton>(
                    com.puutaro.commandclick.R.id.confirm_text_dialog_cancel
                )
            confirmCancelButton?.setOnClickListener {
                getPermissionConfirmDialog?.dismiss()
                cancelProcess(
                    recyclerView,
                    listIndexViewHolder,
                )
            }
            getPermissionConfirmDialog?.setOnCancelListener {
                getPermissionConfirmDialog?.dismiss()
                cancelProcess(
                    recyclerView,
                    listIndexViewHolder,
                )
            }
            val confirmOkButton =
                getPermissionConfirmDialog?.findViewById<AppCompatImageButton>(
                    com.puutaro.commandclick.R.id.confirm_text_dialog_ok
                )
            confirmOkButton?.setOnClickListener {
                getPermissionConfirmDialog?.dismiss()
                removeItem(
                    editFragment,
                    listIndexForEditAdapter,
                    listIndexViewHolder
                )
            }
            getPermissionConfirmDialog?.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            getPermissionConfirmDialog?.window?.setGravity(
                Gravity.CENTER
            )
            getPermissionConfirmDialog?.show()
        }

        private fun cancelProcess(
            recyclerView: RecyclerView,
            listIndexViewHolder: ListIndexForEditAdapter.ListIndexListViewHolder,
        ){
            val posi = listIndexViewHolder.bindingAdapterPosition
            recyclerView.adapter?.notifyItemChanged(
                posi
            )
            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.Main) {
                    delay(300)
                    recyclerView.layoutManager?.scrollToPosition(
                        posi
                    )
                }
            }
        }
    }
}

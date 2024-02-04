package com.puutaro.commandclick.proccess.list_index_for_edit.libs.js_path_handler_for_list_index

import android.app.Dialog
import android.view.Gravity
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ExecRemoveForListIndexAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.DeleteSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.TypeSettingsForListIndex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ExecSimpleDelete {

    fun removeController(
        editFragment: EditFragment,
        recyclerView: RecyclerView,
        listIndexForEditAdapter: ListIndexForEditAdapter,
        listIndexViewHolder: ListIndexForEditAdapter.ListIndexListViewHolder
    ){
        val enableDeleteConfirm = !DeleteSettingsForListIndex.howDisableDeleteConfirm(
            ListIndexForEditAdapter.deleteConfigMap
        )
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
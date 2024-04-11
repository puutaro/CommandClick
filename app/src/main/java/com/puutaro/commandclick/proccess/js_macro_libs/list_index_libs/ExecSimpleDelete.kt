package com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs

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
        selectedItem: String,
        listIndexPosition: Int,
    ){
        val enableDeleteConfirm = !DeleteSettingsForListIndex.howDisableDeleteConfirm(
            ListIndexForEditAdapter.deleteConfigMap
        )
        when(enableDeleteConfirm){
            false -> removeItem(
                editFragment,
                listIndexForEditAdapter,
                listIndexPosition,
                selectedItem,
            )
            else -> DeleteConfirmDialog.launch(
                editFragment,
                recyclerView,
                listIndexPosition,
                selectedItem,
                listIndexForEditAdapter,
            )
        }
    }

    private fun removeItem(
        editFragment: EditFragment,
        listIndexForEditAdapter: ListIndexForEditAdapter,
        listIndexPosition: Int,
        selectedItem: String,
    ){
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                listIndexForEditAdapter.notifyItemRemoved(listIndexPosition)
            }
            val removeItemLine = withContext(Dispatchers.IO) {
                listIndexForEditAdapter.listIndexList[listIndexPosition]
            }
            listIndexForEditAdapter.listIndexList.removeAt(listIndexPosition)
            withContext(Dispatchers.IO) {
                ExecRemoveForListIndexAdapter.removeCon(
                    editFragment,
                    ListIndexForEditAdapter.listIndexTypeKey,
                    removeItemLine
                )
            }
            withContext(Dispatchers.IO) {
                execRemoveItemHandler(
                    editFragment,
                    selectedItem,
                    removeItemLine
                )
            }
        }
    }

    private fun execRemoveItemHandler(
        editFragment: EditFragment,
        selectedItem: String,
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
                    selectedItem,
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
            listIndexPosition: Int,
            selectedItem: String,
            listIndexForEditAdapter: ListIndexForEditAdapter
        ){
            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.Main) {
                    execLaunch(
                        editFragment,
                        recyclerView,
                        listIndexPosition,
                        selectedItem,
                        listIndexForEditAdapter
                    )
                }
            }
        }
        private fun execLaunch(
            editFragment: EditFragment,
            recyclerView: RecyclerView,
            listIndexPosition: Int,
            selectedItem: String,
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
            confirmContentTextView?.text = selectedItem
            val confirmCancelButton =
                getPermissionConfirmDialog?.findViewById<AppCompatImageButton>(
                    com.puutaro.commandclick.R.id.confirm_text_dialog_cancel
                )
            confirmCancelButton?.setOnClickListener {
                getPermissionConfirmDialog?.dismiss()
                cancelProcess(
                    recyclerView,
                    listIndexPosition,
                )
            }
            getPermissionConfirmDialog?.setOnCancelListener {
                getPermissionConfirmDialog?.dismiss()
                cancelProcess(
                    recyclerView,
                    listIndexPosition,
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
                    listIndexPosition,
                    selectedItem,
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
            listIndexPosition: Int,
        ){
            recyclerView.adapter?.notifyItemChanged(
                listIndexPosition
            )
            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.Main) {
                    delay(300)
                    recyclerView.layoutManager?.scrollToPosition(
                        listIndexPosition
                    )
                }
            }
        }
    }
}
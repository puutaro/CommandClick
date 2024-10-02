package com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs

import android.app.Dialog
import android.view.Gravity
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.puutaro.commandclick.component.adapter.ListIndexAdapter
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ExecRemoveForListIndexAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.DeleteSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ExecSimpleDelete {

    fun removeController(
        editFragment: EditFragment,
        recyclerView: RecyclerView,
        listIndexForEditAdapter: ListIndexAdapter,
        selectedItem: String,
        listIndexPosition: Int,
    ){
        val enableDeleteConfirm = !DeleteSettingsForListIndex.howDisableDeleteConfirm(
            ListIndexAdapter.deleteConfigMap
        )
        when(enableDeleteConfirm){
            false -> removeItem(
                editFragment,
                listIndexForEditAdapter,
                selectedItem,
                listIndexPosition,
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
        listIndexForEditAdapter: ListIndexAdapter,
        selectedItem: String,
        listIndexPosition: Int,
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
//                    ListIndexAdapter.listIndexTypeKey,
                    removeItemLine,
                )
            }
            withContext(Dispatchers.IO) {
                execRemoveItemHandler(
                    editFragment,
                    selectedItem,
                    removeItemLine,
                    listIndexPosition,
                )
            }
        }
    }

    private fun execRemoveItemHandler(
        editFragment: EditFragment,
        selectedItem: String,
        removeItemLine: String,
        listIndexPosition: Int,
    ){
        ExecRemoveForListIndexAdapter.updateTsv(
            editFragment,
            listOf(removeItemLine),
        )
//        when(ListIndexAdapter.listIndexTypeKey){
////            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL -> {}
//            TypeSettingsForListIndex.ListIndexTypeKey.NORMAL -> {
//                val filterDir = ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
//                    editFragment,
//                    ListIndexAdapter.indexListMap,
//                    ListIndexAdapter.listIndexTypeKey
//                )
//                ExecItemDelete.DeleteAfterConfirm.execDeleteAfterConfirm(
//                    editFragment,
//                    filterDir,
//                    selectedItem,
//                )
//            }
//            TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT ->
//                ExecRemoveForListIndexAdapter.updateTsv(
//                    editFragment,
//                    listOf(removeItemLine),
//                )
//        }

        DeleteSettingsForListIndex.doWithJsAction(
            editFragment,
            selectedItem,
            listIndexPosition,
        )
    }

    private object DeleteConfirmDialog {

        private var delteConfirmDialog: Dialog? = null

        fun launch(
            editFragment: EditFragment,
            recyclerView: RecyclerView,
            listIndexPosition: Int,
            selectedItem: String,
            listIndexForEditAdapter: ListIndexAdapter
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
            listIndexForEditAdapter: ListIndexAdapter
        ){
            val context = editFragment.context
                ?: return
            delteConfirmDialog = Dialog(
                context
            )
            delteConfirmDialog?.setContentView(
                com.puutaro.commandclick.R.layout.confirm_text_dialog
            )
            val confirmTitleTextView =
                delteConfirmDialog?.findViewById<AppCompatTextView>(
                    com.puutaro.commandclick.R.id.confirm_text_dialog_title
                )
            confirmTitleTextView?.text = "Delete ok?"
            val confirmContentTextView =
                delteConfirmDialog?.findViewById<AppCompatTextView>(
                    com.puutaro.commandclick.R.id.confirm_text_dialog_text_view
                )
            confirmContentTextView?.text = selectedItem
            val confirmCancelButton =
                delteConfirmDialog?.findViewById<AppCompatImageButton>(
                    com.puutaro.commandclick.R.id.confirm_text_dialog_cancel
                )
            confirmCancelButton?.setOnClickListener {
                delteConfirmDialog?.dismiss()
                delteConfirmDialog = null
                cancelProcess(
                    recyclerView,
                    listIndexPosition,
                )
            }
            delteConfirmDialog?.setOnCancelListener {
                delteConfirmDialog?.dismiss()
                delteConfirmDialog = null
                cancelProcess(
                    recyclerView,
                    listIndexPosition,
                )
            }
            val confirmOkButton =
                delteConfirmDialog?.findViewById<AppCompatImageButton>(
                    com.puutaro.commandclick.R.id.confirm_text_dialog_ok
                )
            confirmOkButton?.setOnClickListener {
                delteConfirmDialog?.dismiss()
                delteConfirmDialog = null
                removeItem(
                    editFragment,
                    listIndexForEditAdapter,
                    selectedItem,
                    listIndexPosition,
                )
            }
            delteConfirmDialog?.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            delteConfirmDialog?.window?.setGravity(
                Gravity.CENTER
            )
            delteConfirmDialog?.show()
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
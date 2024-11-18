package com.puutaro.commandclick.proccess.js_macro_libs.edit_list_libs

import android.app.Dialog
import android.webkit.ValueCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.puutaro.commandclick.component.adapter.EditComponentListAdapter
import com.puutaro.commandclick.component.adapter.lib.edit_list_adapter.ExecRemoveForListIndexAdapter
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.dialog.JsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.libs.ExecJsInterfaceAdder
import com.puutaro.commandclick.proccess.edit_list.config_settings.DeleteSettingsForListIndex
import com.puutaro.commandclick.proccess.edit_list.config_settings.ListSettingsForEditList
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ExecSimpleDelete {

    fun removeController(
        fragment: Fragment,
        recyclerView: RecyclerView,
        editComponentListAdapter: EditComponentListAdapter,
        selectedItemMap: Map<String, String>,
        listIndexPosition: Int,
    ){
        val enableDeleteConfirm = !DeleteSettingsForListIndex.howDisableDeleteConfirm(
            editComponentListAdapter.deleteConfigMap
        )
        when(enableDeleteConfirm){
            false -> removeItem(
                fragment,
                editComponentListAdapter,
                selectedItemMap,
                listIndexPosition,
            )
            else -> DeleteConfirmDialog.launch(
                fragment,
                recyclerView,
                listIndexPosition,
                selectedItemMap,
                editComponentListAdapter,
            )
        }
    }

    private fun removeItem(
        fragment: Fragment,
        editComponentListAdapter: EditComponentListAdapter,
        selectedItemMap: Map<String, String>,
        listIndexPosition: Int,
    ){
        CoroutineScope(Dispatchers.IO).launch {
//            withContext(Dispatchers.Main) {
//                editComponentListAdapter.notifyItemRemoved(listIndexPosition)
//            }
            val removeItemLineMap = withContext(Dispatchers.IO) {
                editComponentListAdapter.lineMapList[listIndexPosition]
            }
            withContext(Dispatchers.IO) {
                editComponentListAdapter.lineMapList.removeAt(listIndexPosition)
            }
            withContext(Dispatchers.Main) {
                editComponentListAdapter.notifyItemRemoved(listIndexPosition)
            }
            withContext(Dispatchers.IO) {
                ExecRemoveForListIndexAdapter.removeCon(
                    editComponentListAdapter,
//                    ListIndexAdapter.listIndexTypeKey,
                    removeItemLineMap,
                )
            }
            withContext(Dispatchers.IO) {
                execRemoveItemHandler(
                    fragment,
                    editComponentListAdapter,
                    selectedItemMap,
                    removeItemLineMap,
                    listIndexPosition,
                )
            }
        }
    }

    private fun execRemoveItemHandler(
        fragment: Fragment,
        editComponentListAdapter: EditComponentListAdapter,
        selectedItemMap: Map<String, String>,
        removeItemLineMap: Map<String, String>,
        listIndexPosition: Int,
    ){
        ExecRemoveForListIndexAdapter.updateTsv(
            editComponentListAdapter,
            listOf(removeItemLineMap),
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
            fragment,
            editComponentListAdapter,
            selectedItemMap,
            listIndexPosition,
        )
    }

    private object DeleteConfirmDialog {

        private var delteConfirmDialog: Dialog? = null

        fun launch(
            fragment: Fragment,
            recyclerView: RecyclerView,
            listIndexPosition: Int,
            selectedItemMap: Map<String, String>,
            editComponentListAdapter: EditComponentListAdapter
        ){
            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.Main) {
                    execLaunch(
                        fragment,
                        recyclerView,
                        listIndexPosition,
                        selectedItemMap,
                        editComponentListAdapter
                    )
                }
            }
        }
        private fun execLaunch(
            fragment: Fragment,
            recyclerView: RecyclerView,
            listIndexPosition: Int,
            selectedItemMap: Map<String, String>,
            editComponentListAdapter: EditComponentListAdapter
        ){
            val context = fragment.context
                ?: return
            val terminalFragment = when(fragment){
                is TerminalFragment -> fragment
                else -> TargetFragmentInstance.getCurrentTerminalFragmentFromFrag(
                    fragment.activity,
                )
            } ?: return
            val message = selectedItemMap.get(
                ListSettingsForEditList.MapListPathManager.Key.SRC_TITLE.key
            ) ?: String()
            val jsDialogStr = ExecJsInterfaceAdder.convertUseJsInterfaceName(
                JsDialog::class.java.simpleName
            )
            val confirmScript = """
                ${jsDialogStr}.confirm(
                    "Delete ok?",
                    "$message",
                );
            """.trimIndent()
            terminalFragment.binding.terminalWebView.evaluateJavascript(
                confirmScript,
                ValueCallback<String> { isDelete ->
                    when(isDelete){
                        true.toString() ->  removeItem(
                            fragment,
                            editComponentListAdapter,
                            selectedItemMap,
                            listIndexPosition,
                        )
                        else -> cancelProcess(
                            recyclerView,
                            listIndexPosition,
                        )
                    }
                })
//            delteConfirmDialog = Dialog(
//                context
//            )
//            delteConfirmDialog?.setContentView(
//                com.puutaro.commandclick.R.layout.confirm_text_dialog
//            )
//            val confirmTitleTextView =
//                delteConfirmDialog?.findViewById<AppCompatTextView>(
//                    com.puutaro.commandclick.R.id.confirm_text_dialog_title
//                )
//            confirmTitleTextView?.text = "Delete ok?"
//            val confirmContentTextView =
//                delteConfirmDialog?.findViewById<AppCompatTextView>(
//                    com.puutaro.commandclick.R.id.confirm_text_dialog_text_view
//                )
//            confirmContentTextView?.text = selectedItemMap.get(
//                ListSettingsForListIndex.MapListPathManager.Key.SRC_TITLE.key
//            )
//            val confirmCancelButton =
//                delteConfirmDialog?.findViewById<AppCompatImageButton>(
//                    com.puutaro.commandclick.R.id.confirm_text_dialog_cancel
//                )
//            confirmCancelButton?.setOnClickListener {
//                delteConfirmDialog?.dismiss()
//                delteConfirmDialog = null
//                cancelProcess(
//                    recyclerView,
//                    listIndexPosition,
//                )
//            }
//            delteConfirmDialog?.setOnCancelListener {
//                delteConfirmDialog?.dismiss()
//                delteConfirmDialog = null
//                cancelProcess(
//                    recyclerView,
//                    listIndexPosition,
//                )
//            }
//            val confirmOkButton =
//                delteConfirmDialog?.findViewById<AppCompatImageButton>(
//                    com.puutaro.commandclick.R.id.confirm_text_dialog_ok
//                )
//            confirmOkButton?.setOnClickListener {
//                delteConfirmDialog?.dismiss()
//                delteConfirmDialog = null
//                removeItem(
//                    fragment,
//                    editComponentListAdapter,
//                    selectedItemMap,
//                    listIndexPosition,
//                )
//            }
//            delteConfirmDialog?.window?.setLayout(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT
//            )
//            delteConfirmDialog?.window?.setGravity(
//                Gravity.CENTER
//            )
//            delteConfirmDialog?.show()
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
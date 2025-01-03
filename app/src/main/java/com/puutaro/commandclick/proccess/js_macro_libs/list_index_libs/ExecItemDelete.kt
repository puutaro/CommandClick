package com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs

import android.app.Dialog
import android.view.Gravity
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.component.adapter.ListIndexAdapter
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ListViewToolForListIndexAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.DeleteSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.TypeSettingsForListIndex
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.NoFileChecker
import com.puutaro.commandclick.util.file.ReadText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object ExecItemDelete {

    fun execItemDelete(
        editFragment: EditFragment,
        parentDirPath: String,
        selectedItem: String,
        listIndexPosition: Int,
    ){
        val type = ListIndexEditConfig.getListIndexType(
            editFragment
        )
        when(type){
//            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL,
            TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT
            -> return
            TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
            -> {}
        }
        CoroutineScope(Dispatchers.Main).launch {
            if (
                NoFileChecker.isNoFile(
                    parentDirPath,
                    selectedItem,
                )
            ) return@launch
            withContext(Dispatchers.Main) {
                DeleteAfterConfirm.execDeleteByDialog(
                    editFragment,
                    parentDirPath,
                    selectedItem,
                    listIndexPosition,
                )
            }
        }
    }

    object DeleteAfterConfirm{

        private var confirmDialog: Dialog? = null

        fun execDeleteByDialog(
            editFragment: EditFragment,
            parentDirPath: String,
            selectedItem: String,
            listIndexPosition: Int,
        ){
            val context = editFragment.context
                ?: return
            val scriptContents = ReadText(
                File(
                    parentDirPath,
                    selectedItem
                ).absolutePath
            ).readText()
            val displayContents = "\tpath: ${parentDirPath}/${selectedItem}" +
                    "\n---\n${scriptContents}"
            confirmDialog = Dialog(
                context
            )
            confirmDialog?.setContentView(
                R.layout.confirm_text_dialog
            )
            val confirmTitleTextView =
                confirmDialog?.findViewById<AppCompatTextView>(
                    R.id.confirm_text_dialog_title
                )
            confirmTitleTextView?.text = "Delete bellow contents, ok?"
            val confirmContentTextView =
                confirmDialog?.findViewById<AppCompatTextView>(
                    R.id.confirm_text_dialog_text_view
                )
            confirmContentTextView?.text = displayContents
            val confirmCancelButton =
                confirmDialog?.findViewById<AppCompatImageButton>(
                    R.id.confirm_text_dialog_cancel
                )
            confirmCancelButton?.setOnClickListener {
                confirmDialog?.dismiss()
                confirmDialog = null
            }
            val confirmOkButton =
                confirmDialog?.findViewById<AppCompatImageButton>(
                    R.id.confirm_text_dialog_ok
                )
            confirmOkButton?.setOnClickListener {
                confirmDialog?.dismiss()
                confirmDialog = null
                execDeleteAfterConfirm(
                    editFragment,
                    parentDirPath,
                    selectedItem,
                )
                DeleteSettingsForListIndex.doWithJsAction(
                    editFragment,
                    selectedItem,
                    listIndexPosition,
                )
            }
            confirmDialog?.setOnCancelListener {
                confirmDialog?.dismiss()
                confirmDialog = null
            }
            confirmDialog?.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            confirmDialog?.window?.setGravity(
                Gravity.BOTTOM
            )
            confirmDialog?.show()
        }

        fun execDeleteAfterConfirm(
            editFragment: EditFragment,
            parentDirPath: String,
            selectedItem: String,
        ){
            val removeTargetPathPbj = File(
                parentDirPath,
                selectedItem
            )
            when(true){
                removeTargetPathPbj.isFile
                -> deleteFile(
                    editFragment,
                    parentDirPath,
                    selectedItem,
                )
                removeTargetPathPbj.isDirectory
                -> FileSystems.removeDir(
                    removeTargetPathPbj.absolutePath
                )
                else -> {}
            }
        }

        private fun deleteFile(
            editFragment: EditFragment,
            parentDirPath: String,
            selectedItem: String,
        ){
            FileSystems.removeFiles(
                File(
                    parentDirPath,
                    selectedItem
                ).absolutePath
            )
            val deleteFannelDir =
                CcPathTool.makeFannelDirName(
                    selectedItem
                )
            FileSystems.removeDir(
                "${parentDirPath}/${deleteFannelDir}"
            )
            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.Main) {
                    ListViewToolForListIndexAdapter.listIndexListUpdateFileList(
                        editFragment,
                        ListSettingsForListIndex.ListIndexListMaker.makeFileListHandler(
                            editFragment,
                            ListIndexAdapter.indexListMap,
                            ListIndexAdapter.listIndexTypeKey
                        )
                    )
                }
            }
        }
    }
}
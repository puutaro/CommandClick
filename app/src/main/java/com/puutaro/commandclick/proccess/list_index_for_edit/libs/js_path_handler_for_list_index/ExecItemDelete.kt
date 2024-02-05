package com.puutaro.commandclick.proccess.list_index_for_edit.libs.js_path_handler_for_list_index

import android.app.Dialog
import android.view.Gravity
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ListViewToolForListIndexAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.TypeSettingsForListIndex
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.NoFileChecker
import com.puutaro.commandclick.util.file.ReadText
import java.io.File

object ExecItemDelete {

    private var confirmDialog: Dialog? = null
    private var confirmDialog2: Dialog? = null

    fun execItemDelete(
        editFragment: EditFragment,
        parentDirPath: String,
        selectedItem: String,
        extraMapForJsPath: Map<String, String>?,
    ){
        val type = ListIndexEditConfig.getListIndexType(
            editFragment
        )
        when(type){
            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL,
            TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT
            -> return
            TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
            -> {}
        }
        val context = editFragment.context ?: return
        if(
            NoFileChecker.isNoFile(
                context,
                parentDirPath,
                selectedItem,
            )
        ) return

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
        }
        val confirmOkButton =
            confirmDialog?.findViewById<AppCompatImageButton>(
                R.id.confirm_text_dialog_ok
            )
        confirmOkButton?.setOnClickListener {
            confirmDialog?.dismiss()
            execDeleteAfterConfirm(
                editFragment,
                parentDirPath,
                selectedItem,
            )
        }
        confirmDialog?.setOnCancelListener {
            confirmDialog?.dismiss()
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
        val context = editFragment.context
            ?: return
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
        ListViewToolForListIndexAdapter.listIndexListUpdateFileList(
            editFragment,
            ListSettingsForListIndex.ListIndexListMaker.makeFileListHandler(
                editFragment,
                ListIndexForEditAdapter.indexListMap,
                ListIndexForEditAdapter.listIndexTypeKey
            )
        )
        if (
            parentDirPath.removeSuffix("/")
            == UsePath.cmdclickAppDirAdminPath
        ) {
            val deleteAppDirName = selectedItem.removeSuffix(
                UsePath.JS_FILE_SUFFIX
            )
            val cmdclickAppDirPath = UsePath.cmdclickAppDirPath
            val displayDeleteAppDirPath =
                "${
                    UsePath.makeTermuxPathByReplace(
                        cmdclickAppDirPath
                    )
                }/${deleteAppDirName}"


            confirmDialog2 = Dialog(
                context
            )
            confirmDialog2?.setContentView(
                R.layout.confirm_text_dialog
            )
            val confirmTitleForDeleteAppDirTextView =
                confirmDialog2?.findViewById<AppCompatTextView>(
                    R.id.confirm_text_dialog_title
                )
            confirmTitleForDeleteAppDirTextView?.text =
                "Delete bellow App dir, ok?"
            val confirmContentTextViewForDeleteAppDir =
                confirmDialog2?.findViewById<AppCompatTextView>(
                    R.id.confirm_text_dialog_text_view
                )
            confirmContentTextViewForDeleteAppDir?.text =
                "\tpath: ${displayDeleteAppDirPath}"
            val confirmCancelButtonForDeleteAppDir =
                confirmDialog2?.findViewById<AppCompatImageButton>(
                    R.id.confirm_text_dialog_cancel
                )
            confirmCancelButtonForDeleteAppDir?.setOnClickListener {
                confirmDialog2?.dismiss()
            }
            val confirmOkButtonForDeleteAppDir =
                confirmDialog2?.findViewById<AppCompatImageButton>(
                    R.id.confirm_text_dialog_ok
                )
            confirmOkButtonForDeleteAppDir?.setOnClickListener {
                confirmDialog2?.dismiss()
                val deleteAppDirPath =
                    "${cmdclickAppDirPath}/${deleteAppDirName}"
                FileSystems.removeDir(
                    deleteAppDirPath
                )
                ListViewToolForListIndexAdapter.listIndexListUpdateFileList(
                    editFragment,
                    ListSettingsForListIndex.ListIndexListMaker.makeFileListHandler(
                        editFragment,
                        ListIndexForEditAdapter.indexListMap,
                        ListIndexForEditAdapter.listIndexTypeKey
                    )
                )
            }
            confirmDialog2?.setOnCancelListener {
                confirmDialog2?.dismiss()
            }
            confirmDialog2?.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            confirmDialog2?.window?.setGravity(
                Gravity.BOTTOM
            )
            confirmDialog2?.show()
        }
    }
}
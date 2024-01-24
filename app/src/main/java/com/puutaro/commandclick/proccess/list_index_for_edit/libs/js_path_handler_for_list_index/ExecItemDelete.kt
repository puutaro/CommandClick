package com.puutaro.commandclick.proccess.list_index_for_edit.libs.js_path_handler_for_list_index

import android.app.Dialog
import android.view.Gravity
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.proccess.list_index_for_edit.libs.ListIndexArgsMaker
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.ReadText

object ExecItemDelete {

    private var confirmDialog: Dialog? = null
    private var confirmDialog2: Dialog? = null

    fun execItemDelete(
        listIndexArgsMaker: ListIndexArgsMaker,
        selectedItem: String,
        extraMapForJsPath: Map<String, String>?,
    ){
        val editFragment = listIndexArgsMaker.editFragment
        val context = editFragment.context ?: return
        if(
            ListIndexArgsMaker.judgeNoFile(selectedItem)
        ) {
            ListIndexArgsMaker.noFileToast(
                context
            )
            return
        }

        val parentDirPath =
            ListIndexForEditAdapter.filterDir
        val scriptContents = ReadText(
            parentDirPath,
            selectedItem
        ).readText()
        val displayContents = "\tpath: ${parentDirPath}/${selectedItem}" +
                "\n---\n${scriptContents}"
        FileSystems.writeFile(
            UsePath.cmdclickDefaultAppDirPath,
            "lExecItemDelete.txt",
            "parentDirPath: ${parentDirPath}\n\n" +
                    "nofile: ${ListIndexArgsMaker.judgeNoFile(selectedItem)}\n\n" +
                    "extraMapForJsPath: ${extraMapForJsPath}\n\n" +
                    "selectedItem: ${selectedItem}\n\n"
        )


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
            FileSystems.removeFiles(
                parentDirPath,
                selectedItem
            )
            val deleteFannelDir =
                CcPathTool.makeFannelDirName(
                    selectedItem
                )
            FileSystems.removeDir(
                "${parentDirPath}/${deleteFannelDir}"
            )
            ListIndexForEditAdapter.listIndexListUpdateFileList(
                editFragment,
                ListIndexForEditAdapter.makeFileListHandler(
                    editFragment.busyboxExecutor,
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
                    ListIndexForEditAdapter.listIndexListUpdateFileList(
                        editFragment,
                        ListIndexForEditAdapter.makeFileListHandler(
                            editFragment.busyboxExecutor,
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
}
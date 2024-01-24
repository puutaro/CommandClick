package com.puutaro.commandclick.proccess.list_index_for_edit.libs.js_path_handler_for_list_index

import android.app.Dialog
import android.view.Gravity
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.WithIndexListView
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.list_index.CopyAppDirEventForEdit
import com.puutaro.commandclick.proccess.list_index_for_edit.libs.ListIndexArgsMaker

object ExecCopyAppDir {

    private var promptDialog: Dialog? = null

    fun copyAppDir(
        listIndexArgsMaker: ListIndexArgsMaker,
        selectedItem: String
    ){
        val editFragment = listIndexArgsMaker.editFragment
        val context = editFragment.context ?: return

        promptDialog = Dialog(
            context
        )
        promptDialog?.setContentView(
            R.layout.prompt_dialog_layout
        )
        val promptTitleTextView =
            promptDialog?.findViewById<AppCompatTextView>(
                R.id.prompt_dialog_title
            )
        promptTitleTextView?.text = "Input, destination App dir name"
        val promptMessageTextView =
            promptDialog?.findViewById<AppCompatTextView>(
                R.id.prompt_dialog_message
            )
        promptMessageTextView?.text = "current app dir name: ${selectedItem}"
        val promptEditText =
            promptDialog?.findViewById<AutoCompleteTextView>(
                R.id.prompt_dialog_input
            ) ?: return
        val promptCancelButton =
            promptDialog?.findViewById<AppCompatImageButton>(
                R.id.prompt_dialog_cancel
            )
        promptCancelButton?.setOnClickListener {
            promptDialog?.dismiss()
        }
        val promptOkButtonView =
            promptDialog?.findViewById<AppCompatImageButton>(
                R.id.prompt_dialog_ok
            )
        promptOkButtonView?.setOnClickListener {
            promptDialog?.dismiss()
            CopyAppDirEventForEdit.execCopyAppDir(
                editFragment,
                UsePath.cmdclickAppDirAdminPath,
                selectedItem,
                promptEditText
            )
            ListIndexForEditAdapter.listIndexListUpdateFileList(
                editFragment,
                ListIndexForEditAdapter.makeFileListHandler(
                    editFragment.busyboxExecutor,
                    ListIndexForEditAdapter.listIndexTypeKey
                )
            )
        }
        promptDialog?.setOnCancelListener {
            promptDialog?.dismiss()
        }
        promptDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        promptDialog?.window?.setGravity(
            Gravity.BOTTOM
        )
        promptDialog?.show()
    }
}
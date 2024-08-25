package com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs

import android.app.Dialog
import android.view.Gravity
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.component.adapter.ListIndexAdapter
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ListViewToolForListIndexAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.list_index.CopyAppDirEventForEdit
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex

object ExecCopyAppDir {

    private var promptDialog: Dialog? = null

    fun copyAppDir(
        editFragment: EditFragment,
        selectedItem: String
    ){
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
            promptDialog = null
        }
        val promptOkButtonView =
            promptDialog?.findViewById<AppCompatImageButton>(
                R.id.prompt_dialog_ok
            )
        promptOkButtonView?.setOnClickListener {
            promptDialog?.dismiss()
            promptDialog = null
            CopyAppDirEventForEdit.execCopyAppDir(
                UsePath.cmdclickAppDirAdminPath,
                selectedItem,
                promptEditText
            )
            ListViewToolForListIndexAdapter.listIndexListUpdateFileList(
                editFragment,
                ListSettingsForListIndex.ListIndexListMaker.makeFileListHandler(
                    editFragment,
                    ListIndexAdapter.indexListMap,
                    ListIndexAdapter.listIndexTypeKey
                )
            )
        }
        promptDialog?.setOnCancelListener {
            promptDialog?.dismiss()
            promptDialog = null
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
package com.puutaro.commandclick.proccess.list_index_for_edit.libs.js_path_handler_for_list_index

import android.app.Dialog
import android.view.Gravity
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.libs.ListIndexArgsMaker
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.file.FileSystems

object ExecRenameAppDir {

    private var promptDialog: Dialog? = null

    fun execRenameAppDir(
        listIndexArgsMaker: ListIndexArgsMaker,
        selectedItem: String,
    ){
        val editFragment = listIndexArgsMaker.editFragment
        execRenameForAppDirAdmin(
            listIndexArgsMaker,
            selectedItem,
        )
        ListIndexForEditAdapter.listIndexListUpdateFileList(
            editFragment,
            ListSettingsForListIndex.ListIndexListMaker.makeFileListHandler(
                editFragment,
                ListIndexForEditAdapter.indexListMap,
                ListIndexForEditAdapter.listIndexTypeKey
            )
        )
    }

    private fun execRenameForAppDirAdmin(
        listIndexArgsMaker: ListIndexArgsMaker,
        selectedItem: String,
    ){
        val editFragment = listIndexArgsMaker.editFragment
        val context = editFragment.context
            ?: return
        val jsSuffix = UsePath.JS_FILE_SUFFIX
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
        promptTitleTextView?.text = "Rename app dir"
        val promptMessageTextView =
            promptDialog?.findViewById<AppCompatTextView>(
                R.id.prompt_dialog_message
            )
        promptMessageTextView?.isVisible = false
        val promptEditText =
            promptDialog?.findViewById<AutoCompleteTextView>(
                R.id.prompt_dialog_input
            )
        promptEditText?.setText(
            selectedItem.removeSuffix(jsSuffix)
        )
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
            val inputEditable = promptEditText?.text
            if(
                inputEditable.isNullOrEmpty()
            ) {
                Toast.makeText(
                    context,
                    "No type item name",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            val renamedAppDirNameSource = inputEditable.toString()
            val renamedAppDirName = if(
                renamedAppDirNameSource.endsWith(jsSuffix)
            ) renamedAppDirNameSource
            else "${renamedAppDirNameSource}${jsSuffix}"
            if(
                selectedItem == renamedAppDirName
            ) return@setOnClickListener
            val selectedItemFannelDirName = CcPathTool.makeFannelDirName(selectedItem)
            val selectedItemFannelDirPath = "${UsePath.cmdclickAppDirAdminPath}/$selectedItemFannelDirName"
            val renamedFannelDirName = CcPathTool.makeFannelDirName(renamedAppDirName)
            val renamedFannelDirPath = "${UsePath.cmdclickAppDirAdminPath}/$renamedFannelDirName"
            FileSystems.moveDirectory(
                selectedItemFannelDirPath,
                renamedFannelDirPath
            )
            CommandClickScriptVariable.makeAppDirAdminFile(
                UsePath.cmdclickAppDirAdminPath,
                renamedAppDirName
            )
            FileSystems.removeFiles(
                UsePath.cmdclickAppDirAdminPath,
                selectedItem
            )
            val cmdclickAppDirPath = UsePath.cmdclickAppDirPath
            val beforeMoveDirPath = cmdclickAppDirPath + '/' +
                    selectedItem.removeSuffix(
                        UsePath.JS_FILE_SUFFIX
                    )
            val afterMoveDirPath = cmdclickAppDirPath + '/' +
                    renamedAppDirName.removeSuffix(
                        UsePath.JS_FILE_SUFFIX
                    )
            FileSystems.moveDirectory(
                beforeMoveDirPath,
                afterMoveDirPath,
            )
            ListIndexForEditAdapter.listIndexListUpdateFileList(
                editFragment,
                ListSettingsForListIndex.ListIndexListMaker.makeFileListHandler(
                    editFragment,
                    ListIndexForEditAdapter.indexListMap,
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
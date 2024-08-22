package com.puutaro.commandclick.proccess.js_macro_libs.list_index_libs

import android.app.Dialog
import android.view.Gravity
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ListViewToolForListIndexAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.TypeSettingsForListIndex
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.file.FileSystems
import java.io.File

object ExecRenameAppDir {

    private var promptDialog: Dialog? = null

    fun execRenameAppDir(
        editFragment: EditFragment,
        selectedItem: String,
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
        execRenameForAppDirAdmin(
            editFragment,
            selectedItem,
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

    private fun execRenameForAppDirAdmin(
        editFragment: EditFragment,
        selectedItem: String,
    ){
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
            promptDialog = null
        }
        val promptOkButtonView =
            promptDialog?.findViewById<AppCompatImageButton>(
                R.id.prompt_dialog_ok
            )
        promptOkButtonView?.setOnClickListener {
            promptDialog?.dismiss()
            promptDialog = null
            val inputEditable = promptEditText?.text
            if(
                inputEditable.isNullOrEmpty()
            ) {
                ToastUtils.showShort("No type item name")
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
                File(
                    UsePath.cmdclickAppDirAdminPath,
                    selectedItem
                ).absolutePath
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
            ListViewToolForListIndexAdapter.listIndexListUpdateFileList(
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
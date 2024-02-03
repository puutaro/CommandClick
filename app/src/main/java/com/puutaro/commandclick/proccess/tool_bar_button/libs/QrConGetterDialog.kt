package com.puutaro.commandclick.proccess.tool_bar_button.libs

import android.app.Dialog
import android.view.Gravity
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.extra_args.ExtraArgsTool
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.proccess.menu_tool.MenuSettingTool
import com.puutaro.commandclick.proccess.qr.QrScanner
import com.puutaro.commandclick.proccess.tool_bar_button.common_settings.JsPathMacroForToolbarButton
import java.io.File

object QrConGetterDialog {

    private var qrConGetterDialog: Dialog? = null
    fun launch(
        toolbarButtonArgsMaker: ToolbarButtonArgsMaker,
    ){
        val fragment = toolbarButtonArgsMaker.fragment
        if(fragment !is EditFragment) return
        val context = fragment.context
            ?: return
        val activity = fragment.activity
            ?: return
        qrConGetterDialog = Dialog(
            activity
        )
        qrConGetterDialog?.setContentView(
            R.layout.prompt_dialog_layout
        )
        val promptTitleTextView =
            qrConGetterDialog?.findViewById<AppCompatTextView>(
                R.id.prompt_dialog_title
            )
        promptTitleTextView?.text = "Input contents name"
        val promptMessageTextView =
            qrConGetterDialog?.findViewById<AppCompatTextView>(
                R.id.prompt_dialog_message
            )
        promptMessageTextView?.isVisible = false
        val promptEditText =
            qrConGetterDialog?.findViewById<AutoCompleteTextView>(
                R.id.prompt_dialog_input
            )
        val promptCancelButton =
            qrConGetterDialog?.findViewById<AppCompatImageButton>(
                R.id.prompt_dialog_cancel
            )
        promptCancelButton?.setOnClickListener {
            qrConGetterDialog?.dismiss()
        }
        val promptOkButtonView =
            qrConGetterDialog?.findViewById<AppCompatImageButton>(
                R.id.prompt_dialog_ok
            )
        promptOkButtonView?.setOnClickListener {
            qrConGetterDialog?.dismiss()
            val shortcutNameEditable = promptEditText?.text
            if(
                shortcutNameEditable.isNullOrEmpty()
            ) return@setOnClickListener
            val fileName = UsePath.compExtend(
                shortcutNameEditable.toString(),
                ".txt",
            )
            val settingButtonMenuMapList = toolbarButtonArgsMaker.makeSettingButtonMenuMapList()
            val extraMap = ExtraArgsTool.createExtraMapFromMenuMapList(
                settingButtonMenuMapList,
                JsPathMacroForToolbarButton.GET_QR_CON.name,
                MenuSettingTool.MenuSettingKey.JS_PATH.key,
                "!",
            )
            val parentDirPath = ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
                fragment,
                ListIndexForEditAdapter.indexListMap,
                ListIndexForEditAdapter.listIndexTypeKey
            )
            val filePath = "${parentDirPath}/${fileName}"
            if(File(filePath).isFile) {
                Toast.makeText(
                    context,
                    "Already exist: ${filePath}",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            getQrHandler(
                fragment,
                toolbarButtonArgsMaker,
                parentDirPath,
                fileName,
            )
        }
        qrConGetterDialog?.setOnCancelListener {
            qrConGetterDialog?.dismiss()
        }
        qrConGetterDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        qrConGetterDialog?.window?.setGravity(
            Gravity.BOTTOM
        )
        qrConGetterDialog?.show()
    }

    private fun getQrHandler(
        fragment: Fragment,
        toolbarButtonArgsMaker: ToolbarButtonArgsMaker,
        parentDirPath: String,
        fileName: String,
    ){
        QrScanner(
            fragment,
            parentDirPath,
            toolbarButtonArgsMaker,
        ).saveFromCamera(fileName)
    }
}